package com.summerschool.artificiumanima.service.discord;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import com.summerschool.artificiumanima.service.AudioPlayerService;
import com.summerschool.artificiumanima.service.ChatBotService;
import com.summerschool.artificiumanima.service.discord.handlers.DiscordAudioReceiveHandler;
import com.summerschool.artificiumanima.utils.MarkdownConstants;
import com.summerschool.artificiumanima.utils.TextHelper;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDA.Status;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.audio.AudioNatives;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.managers.AudioManager;

@Component
@Slf4j
public class DiscordServiceImpl implements ChatBotService<AudioChannel, Message> {

  private static final int MAX_MESSAGE_SIZE = 2000;

  private static final String NO_VOICE_CHANNEL_FORMAT = String.format(
      MarkdownConstants.BOLD_TEXT_FORMAT, "User %s has not joined any voice channel! :mute:");

  private static final String CANNOT_JOIN_VOICE_CHANNEL_FORMAT =
      String.format(MarkdownConstants.BOLD_TEXT_FORMAT, "%s cannot join voice channel %s! :mute:");

  private static final String JOINED_VOICE_CHANNEL_FORMAT =
      String.format(MarkdownConstants.BOLD_TEXT_FORMAT, "%s joined voice channel %s!");

  private static final String LEFT_VOICE_CHANNEL_FORMAT =
      String.format(MarkdownConstants.BOLD_TEXT_FORMAT, "%s left voice channel %s! :loud_sound:");

  private final DiscordBotFactory botFactory;
  private final DiscordAudioReceiveHandler discordAudioHandler;
  private final AudioPlayerService<Message> audioPlayerService;
  private JDA discordBot;

  @Autowired
  public DiscordServiceImpl(@Value("${connect.bot.on.startup:true}") boolean connectBotOnStartup,
      DiscordBotFactory botFactory, DiscordAudioReceiveHandler discordAudioHandler,
      @Lazy AudioPlayerService<Message> audioPlayerService) {
    this.botFactory = botFactory;
    this.discordAudioHandler = discordAudioHandler;
    this.audioPlayerService = audioPlayerService;
    if (connectBotOnStartup) {
      log.info("Initializing discord bot at startup");
      initBot();
    }
  }

  @Override
  public void initBot() {
    if (this.discordBot == null || this.discordBot.getStatus() == Status.SHUTDOWN) {
      this.discordBot = botFactory.createBot();
      // Load native opus library
      final boolean opusLoaded = AudioNatives.ensureOpus();
      log.info("Opus library load status: {}", opusLoaded);
    }
  }

  @Override
  public void shutDownBot() {
    if (this.discordBot != null && this.discordBot.getStatus() != Status.SHUTDOWN
        && this.discordBot.getStatus() != Status.SHUTTING_DOWN) {
      log.info("Shutting down discord bot, current bot status: {}", this.discordBot.getStatus());
      this.discordBot.shutdown();
    }
  }

  @Override
  public AudioChannel joinAudio(Message message) {
    AudioChannel result = null;
    final Guild guild = message.getGuild();
    final GuildVoiceState voiceState = message.getMember().getVoiceState();
    final String selfUserName = guild.getSelfMember().getUser().getName();
    if (voiceState != null && voiceState.getChannel() != null) {
      final AudioChannel audioChannel = voiceState.getChannel();
      if (!guild.getSelfMember().hasPermission(audioChannel, Permission.VOICE_CONNECT)) {
        final String couldNotJoin =
            String.format(CANNOT_JOIN_VOICE_CHANNEL_FORMAT, selfUserName, audioChannel.getName());
        sendMessage(couldNotJoin, message);
      } else {
        // Join the audio channel if not already connected
        final AudioManager audioManager = guild.getAudioManager();
        if (!audioManager.isConnected()) {
          audioManager.openAudioConnection(audioChannel);
        }
        final String joinedChannelResponse =
            String.format(JOINED_VOICE_CHANNEL_FORMAT, selfUserName, audioChannel.getName());
        sendMessage(joinedChannelResponse, message);
        result = audioChannel;
      }
    } else {
      log.info("The sender of the command {} has not joined any voice channel!",
          message.getAuthor().getName());
      final String noUserVoiceChannel =
          String.format(NO_VOICE_CHANNEL_FORMAT, message.getAuthor().getName());
      sendMessage(noUserVoiceChannel, message);
    }
    return result;
  }

  @Override
  public void leaveAudio(Message message) {
    final Guild guild = message.getGuild();
    final GuildVoiceState voiceState = message.getGuild().getSelfMember().getVoiceState();
    final String selfUserName = guild.getSelfMember().getUser().getName();
    if (voiceState != null && voiceState.getChannel() != null) {
      final String audioChannelName = voiceState.getChannel().getName();
      stopRecordingAudio();
      this.discordAudioHandler.clearUserFiles();
      final AudioManager audioManager = guild.getAudioManager();
      audioManager.closeAudioConnection();
      final String leftVoiceChannel =
          String.format(LEFT_VOICE_CHANNEL_FORMAT, selfUserName, audioChannelName);
      sendMessage(leftVoiceChannel, message);
    } else {
      log.info("User {} has not joined any voice channel!", selfUserName);
      final String noUserVoiceChannel = String.format(NO_VOICE_CHANNEL_FORMAT, selfUserName);
      sendMessage(noUserVoiceChannel, message);
    }
  }

  @Override
  public void startRecordingAudio(AudioChannel audioChannel) {
    final AudioManager audioManager = audioChannel.getGuild().getAudioManager();
    audioManager.setReceivingHandler(this.discordAudioHandler);
    this.discordAudioHandler.startRecording();
  }

  @Override
  public void stopRecordingAudio() {
    this.discordAudioHandler.stopRecording();
  }

  @Override
  public Map<String, Path> getRecordedAudio() {
    return this.discordAudioHandler.getRecordedUserFiles();
  }

  @Override
  public void speak(Path audioPath, AudioChannel audioChannel, Message message) {
    this.audioPlayerService.loadAndPlay(message, audioPath.toString());
  }

  @Override
  public void sendMessage(String text, Message message) {
    final List<String> chunks = TextHelper.splitInChunks(text, MAX_MESSAGE_SIZE);
    CollectionUtils.emptyIfNull(chunks).stream()
        .forEach(x -> message.getChannel().sendMessage(x).queue());
  }
}
