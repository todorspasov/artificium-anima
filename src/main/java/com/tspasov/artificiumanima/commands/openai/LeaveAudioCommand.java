package com.tspasov.artificiumanima.commands.openai;

import org.springframework.stereotype.Component;
import com.tspasov.artificiumanima.commands.Command;
import com.tspasov.artificiumanima.commands.discord.MarkdownConstants;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.managers.AudioManager;

@Slf4j
@Component
public class LeaveAudioCommand implements Command<Message> {

  private static final String LEAVE_AUDIO_COMMAND_KEY = "!leave";
  private static final String LEAVE_AUDIO_COMMAND_INFO =
      "Ask the Artificial Oracle to leave all voice channels";

  private static final String NO_VOICE_CHANNEL_FORMAT = String.format(
      MarkdownConstants.BOLD_TEXT_FORMAT, "User %s has not joined any voice channel! :mute:");

  private static final String LEFT_VOICE_CHANNEL_FORMAT =
      String.format(MarkdownConstants.BOLD_TEXT_FORMAT, "%s left voice channel %s! :loud_sound:");

  @Override
  public void execute(String commandStr, Message message) {
    log.info("Leaving all voice channels. Args: {}", commandStr);
    final Guild guild = message.getGuild();
    final GuildVoiceState voiceState = message.getGuild().getSelfMember().getVoiceState();
    final String selfUserName = guild.getSelfMember().getUser().getName();
    if (voiceState != null && voiceState.getChannel() != null) {
      final String audioChannelName = voiceState.getChannel().getName();
      // Leave the audio channel
      final AudioManager audioManager = guild.getAudioManager();
      audioManager.closeAudioConnection();
      message.getChannel()
          .sendMessage(String.format(LEFT_VOICE_CHANNEL_FORMAT, selfUserName, audioChannelName))
          .queue();
    } else {
      log.info("User {} has not joined any voice channel!", selfUserName);
      message.getChannel().sendMessage(String.format(NO_VOICE_CHANNEL_FORMAT, selfUserName))
          .queue();
    }
  }

  @Override
  public String getCommandKey() {
    return LEAVE_AUDIO_COMMAND_KEY;
  }

  @Override
  public String getCommandInfo() {
    return LEAVE_AUDIO_COMMAND_INFO;
  }
}
