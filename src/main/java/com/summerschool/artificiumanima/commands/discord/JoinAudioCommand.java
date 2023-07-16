package com.summerschool.artificiumanima.commands.discord;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import com.summerschool.artificiumanima.commands.Command;
import com.summerschool.artificiumanima.markdown.MarkdownConstants;
import com.summerschool.artificiumanima.service.ChatBotService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;

@Slf4j
@Component
public class JoinAudioCommand implements Command<Message> {

  private static final String AUDIO_COMMAND_KEY = "!join-audio";
  private static final String AUDIO_COMMAND_INFO =
      "Ask the Artificial Oracle to listen to voice channel";
  private static final String AUDIO_COMMAND_REPLY_FORMAT =
      String.format(MarkdownConstants.BOLD_TEXT_FORMAT,
          "Artificial Oracle :desktop: :brain: listening to voice channel '%s': :ear:")
          + System.lineSeparator();

  private final ChatBotService<AudioChannel, Message> discordService;

  @Autowired
  public JoinAudioCommand(@Lazy ChatBotService<AudioChannel, Message> discordService) {
    this.discordService = discordService;
  }

  @Override
  public void execute(String commandStr, Message message) {
    message.getGuild();
    log.info("Joining audio channel to create audio transcription. Args: {}", commandStr);
    final AudioChannel audioChannel = this.discordService.joinAudio(message);
    if (audioChannel != null) {
      // Record discord audio to a local file. Then transcribe it through the AI service
      this.discordService.startRecordingAudio(audioChannel);
      message.getChannel()
          .sendMessage(String.format(AUDIO_COMMAND_REPLY_FORMAT, audioChannel.getName())).queue();
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
