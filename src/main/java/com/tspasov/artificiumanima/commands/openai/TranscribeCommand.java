package com.tspasov.artificiumanima.commands.openai;

import java.nio.file.Path;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import com.tspasov.artificiumanima.commands.Command;
import com.tspasov.artificiumanima.markdown.MarkdownConstants;
import com.tspasov.artificiumanima.service.AIService;
import com.tspasov.artificiumanima.service.discord.DiscordService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;

@Slf4j
@Component
public class TranscribeCommand implements Command<Message> {

  private static final String TRANSCRIBE_COMMAND_KEY = "!transcribe";
  private static final String TRANSCRIBE_COMMAND_INFO =
      "Ask the Artificial Oracle to write what it heard in the voice channel. Optional argument: language. Example: '!transcribe bg'";

  private static final String DEFAULT_LANGUAGE = "en";

  private static final String USER_TRANSCRIPTION_FORMAT =
      String.format(MarkdownConstants.BOLD_TEXT_FORMAT, "%s: ") + "%s" + System.lineSeparator();

  private static final String STARTED_TRANSCRIBING_MESSAGE =
      String.format(MarkdownConstants.BOLD_TEXT_FORMAT,
          "The Artificial Oracle heard the following:") + System.lineSeparator();

  private final AIService aiService;
  private final DiscordService discordService;

  @Autowired
  public TranscribeCommand(AIService aiService, @Lazy DiscordService discordService) {
    this.aiService = aiService;
    this.discordService = discordService;
  }

  @Override
  public void execute(String commandStr, Message message) {
    log.info("Creating audio transcription. Args: {}", commandStr);
    final String language = StringUtils.isNotBlank(commandStr) ? commandStr : DEFAULT_LANGUAGE;
    this.discordService.stopRecordingAudio();
    final Map<String, Path> recordedAudio = this.discordService.getRecordedAudio();
    message.getChannel().sendMessage(STARTED_TRANSCRIBING_MESSAGE).queue();
    recordedAudio.entrySet().stream().forEach(entry -> {
      final String response = this.aiService.transcribeAudio(entry.getValue().toFile(), language);
      message.getChannel()
          .sendMessage(String.format(USER_TRANSCRIPTION_FORMAT, entry.getKey(), response)).queue();
    });
  }

  @Override
  public String getCommandKey() {
    return TRANSCRIBE_COMMAND_KEY;
  }

  @Override
  public String getCommandInfo() {
    return TRANSCRIBE_COMMAND_INFO;
  }
}
