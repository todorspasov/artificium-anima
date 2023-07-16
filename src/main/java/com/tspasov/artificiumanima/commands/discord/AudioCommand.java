package com.tspasov.artificiumanima.commands.discord;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import com.tspasov.artificiumanima.commands.Command;
import com.tspasov.artificiumanima.markdown.MarkdownConstants;
import com.tspasov.artificiumanima.service.discord.DiscordService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;

@Slf4j
@Component
public class AudioCommand implements Command<Message> {

  private static final String AUDIO_COMMAND_KEY = "!audio";
  private static final String AUDIO_COMMAND_INFO =
      "Ask the Artificial Oracle to listen to voice channel";
  private static final String AUDIO_COMMAND_REPLY_FORMAT =
      String.format(MarkdownConstants.BOLD_TEXT_FORMAT,
          "Artificial Oracle :desktop: :brain: listening to voice channel '%s': :ear:")
          + System.lineSeparator();

  private final DiscordService discordService;

  @Autowired
  public AudioCommand(@Lazy DiscordService discordService) {
    this.discordService = discordService;
  }

  @Override
  public void execute(String commandStr, Message message) {
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
