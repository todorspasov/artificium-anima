package com.summerschool.artificiumanima.commands.discord.audio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import com.summerschool.artificiumanima.commands.Command;
import com.summerschool.artificiumanima.commands.CommandInfo;
import com.summerschool.artificiumanima.service.ChatBotService;
import com.summerschool.artificiumanima.utils.MarkdownConstants;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;

@Slf4j
@Component
public class JoinAudioCommand implements Command<Message> {

  private static final String AUDIO_COMMAND_KEY = "!join-audio";
  private static final String AUDIO_COMMAND_DESCRIPTION =
      "Ask the Artificial Oracle to listen to voice channel";
  private static final String AUDIO_COMMAND_REPLY_FORMAT =
      String.format(MarkdownConstants.BOLD_TEXT_FORMAT,
          "Artificial Oracle :desktop: :brain: listening to voice channel '%s': :ear:")
          + System.lineSeparator();

  private final ChatBotService<AudioChannel, Message> chatService;

  @Autowired
  public JoinAudioCommand(@Lazy ChatBotService<AudioChannel, Message> chatService) {
    this.chatService = chatService;
  }

  @Override
  public void execute(String commandStr, Message message) {
    message.getGuild();
    log.info("Joining audio channel to create audio transcription. Args: {}", commandStr);
    final AudioChannel audioChannel = this.chatService.joinAudio(message);
    if (audioChannel != null) {
      // Record discord audio to a local file. Then transcribe it through the AI service
      this.chatService.startRecordingAudio(audioChannel);
      final String audioCommandReply =
          String.format(AUDIO_COMMAND_REPLY_FORMAT, audioChannel.getName());
      this.chatService.sendMessage(audioCommandReply, message);
    }
  }

  @Override
  public CommandInfo getCommandInfo() {
    return CommandInfo.builder().commandKey(AUDIO_COMMAND_KEY)
        .commandDescription(AUDIO_COMMAND_DESCRIPTION).commandGroup("Audio").build();
  }
}
