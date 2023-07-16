package com.tspasov.artificiumanima.commands.openai;

import java.io.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import com.tspasov.artificiumanima.commands.Command;
import com.tspasov.artificiumanima.markdown.MarkdownConstants;
import com.tspasov.artificiumanima.service.AIService;
import com.tspasov.artificiumanima.service.discord.DiscordService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;

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

  private final AIService aiService;
  private final DiscordService discordService;

  @Autowired
  public AudioCommand(AIService aiService, @Lazy DiscordService discordService) {
    this.aiService = aiService;
    this.discordService = discordService;
  }

  @Override
  public void execute(String commandStr, Message message) {
    log.info("Joining audio channel to create audio transcription. Args: {}", commandStr);
    final AudioChannel audioChannel = this.discordService.joinAudio(message);
    if (audioChannel != null) {
      // Record discord audio to a local file. Then transcribe it through the AI service
      final File recordedFile = this.discordService.recordAudio(audioChannel);

      final String response =
          recordedFile == null ? "" : this.aiService.transcribeAudio(recordedFile);
      message.getChannel()
          .sendMessage(String.format(AUDIO_COMMAND_REPLY_FORMAT, audioChannel.getName(), response))
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
