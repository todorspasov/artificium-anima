package com.tspasov.artificiumanima.commands.openai;

import java.io.File;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.tspasov.artificiumanima.commands.Command;
import com.tspasov.artificiumanima.commands.discord.MarkdownConstants;
import com.tspasov.artificiumanima.service.AIService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.managers.AudioManager;

@Slf4j
@Component
public class AudioCommand implements Command<Message> {

  private static final String AUDIO_COMMAND_KEY = "!audio";
  private static final String AUDIO_COMMAND_INFO =
      "Ask the Artificial Oracle to convert audio to text";
  private static final String AUDIO_COMMAND_REPLY_FORMAT = String.format(
      MarkdownConstants.BOLD_TEXT_FORMAT,
      "Artificial Oracle :desktop: :brain: heard the following from the voice channel '%s': :loud_sound:")
      + System.lineSeparator() + "%s";

  private static final String NO_VOICE_CHANNEL_FORMAT = String.format(
      MarkdownConstants.BOLD_TEXT_FORMAT, "User %s has not joined any voice channel! :mute:");

  private static final String CANNOT_JOIN_VOICE_CHANNEL_FORMAT =
      String.format(MarkdownConstants.BOLD_TEXT_FORMAT, "%s cannot join voice channel %s! :mute:");

  private static final String JOINED_VOICE_CHANNEL_FORMAT =
      String.format(MarkdownConstants.BOLD_TEXT_FORMAT, "%s joined voice channel %s! :loud_sound:");

  private final AIService aiService;

  @Autowired
  public AudioCommand(AIService aiService) {
    this.aiService = aiService;
  }

  @Override
  public void execute(String commandStr, Message message) {
    log.info("Joining audio channel to create audio transcription. Args: {}", commandStr);
    final Guild guild = message.getGuild();
    final GuildVoiceState voiceState = message.getMember().getVoiceState();
    final String selfUserName = guild.getSelfMember().getUser().getName();
    if (voiceState != null && voiceState.getChannel() != null) {
      final AudioChannel audioChannel = voiceState.getChannel();
      if (!guild.getSelfMember().hasPermission(audioChannel, Permission.VOICE_CONNECT)) {
        message.getChannel().sendMessage(
            String.format(CANNOT_JOIN_VOICE_CHANNEL_FORMAT, selfUserName, audioChannel.getName()))
            .queue();
      } else {
        // Join the audio channel if not already connected
        final AudioManager audioManager = guild.getAudioManager();
        audioManager.openAudioConnection(audioChannel);
        message.getChannel()
            .sendMessage(
                String.format(JOINED_VOICE_CHANNEL_FORMAT, selfUserName, audioChannel.getName()))
            .queue();
        // https://medium.com/discord-bots/connecting-your-discord-bot-to-voice-channels-with-java-and-jda-403a9604e816
        // TODO: FIXME: Transcribe what the bot hears in the next 5 seconds
//        AudioReceiveHandler audioReceiveHandler = new AudioReceiveHandler() {};
//        audioManager.setReceivingHandler(audioReceiveHandler);
        
        final String response = StringUtils.isBlank(commandStr) ? ""
            : this.aiService.transcribeAudio(new File(commandStr));
        message.getChannel()
            .sendMessage(
                String.format(AUDIO_COMMAND_REPLY_FORMAT, audioChannel.getName(), response))
            .queue();
      }
    } else {
      log.info("The sender of the command {} has not joined any voice channel!",
          message.getAuthor().getName());
      message.getChannel()
          .sendMessage(String.format(NO_VOICE_CHANNEL_FORMAT, message.getAuthor().getName()))
          .queue();
    }


  }

  @Override
  public String getCommandKey() {
    return AUDIO_COMMAND_KEY;
  }

  @Override
  public String getCommandInfo() {
    return AUDIO_COMMAND_INFO;
  }
}
