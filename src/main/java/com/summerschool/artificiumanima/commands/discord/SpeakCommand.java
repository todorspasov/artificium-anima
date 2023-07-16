package com.summerschool.artificiumanima.commands.discord;

import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import com.summerschool.artificiumanima.commands.Command;
import com.summerschool.artificiumanima.markdown.MarkdownConstants;
import com.summerschool.artificiumanima.service.ChatBotService;
import com.summerschool.artificiumanima.service.TextToSpeechService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;

@Slf4j
@Component
public class SpeakCommand implements Command<Message> {

  private static final String SPEAK_COMMAND_KEY = "!speak";
  private static final String SPEAK_COMMAND_INFO =
      "Ask the Artificial Oracle to speak. Argument: text to speak. Example: '!speak This is the National History Museum'";

  private static final String CANNOT_SPEAK_ERROR = String.format(MarkdownConstants.BOLD_TEXT_FORMAT,
      "Artificial Oracle :desktop: :brain: couldn't say a word :speaking_head:");

  private static final String SPEAKING_MESSAGE_FORMAT =
      String.format(MarkdownConstants.BOLD_TEXT_FORMAT,
          "Artificial Oracle :desktop: :brain: currently speaking in channel '%s': :loudspeaker:")
          + System.lineSeparator();

  private final ChatBotService<AudioChannel, Message> discordService;
  private final TextToSpeechService textToSpeechService;

  @Autowired
  public SpeakCommand(@Lazy ChatBotService<AudioChannel, Message> discordService,
      TextToSpeechService textToSpeechService) {
    this.discordService = discordService;
    this.textToSpeechService = textToSpeechService;
  }

  @Override
  public void execute(String commandStr, Message message) {
    log.info("Speaking the folowing text: {}", commandStr);
    final AudioChannel audioChannel = this.discordService.joinAudio(message);
    if (audioChannel != null) {
      final Path audioPath = this.textToSpeechService.convertToSpeech(commandStr);
      if (audioPath != null) {
        message.getChannel()
            .sendMessage(String.format(SPEAKING_MESSAGE_FORMAT, audioChannel.getName())).queue();
        this.discordService.speak(audioPath, audioChannel, message);
      } else {
        message.getChannel().sendMessage(CANNOT_SPEAK_ERROR).queue();
      }
    }
  }

  @Override
  public String getCommandKey() {
    return SPEAK_COMMAND_KEY;
  }

  @Override
  public String getCommandInfo() {
    return SPEAK_COMMAND_INFO;
  }
}
