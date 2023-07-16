package com.summerschool.artificiumanima.commands.openai;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.summerschool.artificiumanima.commands.Command;
import com.summerschool.artificiumanima.markdown.MarkdownConstants;
import com.summerschool.artificiumanima.service.AiService;
import net.dv8tion.jda.api.entities.Message;

@Component
public class ChatGptCommand implements Command<Message> {

  private static final String CHATGPT_COMMAND_KEY = "!chatgpt";
  private static final String CHATGPT_COMMAND_INFO =
      "Ask the Artificial Oracle something. Example: '!chatgpt what is the age of the Sun'";
  private static final String CHATGPT_COMMAND_REPLY_FORMAT =
      String.format(MarkdownConstants.BOLD_TEXT_FORMAT,
          "Artificial Oracle :desktop: :brain: answered: ") + "%s";

  private final AiService aiService;

  @Autowired
  public ChatGptCommand(AiService aiService) {
    this.aiService = aiService;
  }

  @Override
  public void execute(String commandStr, Message message) {
    // ask ChatGPT about the commandStr
    // get response, and then paste it in discord
    final String answer = aiService.askQuestion(commandStr);
    message.getChannel().sendMessage(String.format(CHATGPT_COMMAND_REPLY_FORMAT, answer)).queue();
  }

  @Override
  public String getCommandKey() {
    return CHATGPT_COMMAND_KEY;
  }

  @Override
  public String getCommandInfo() {
    return CHATGPT_COMMAND_INFO;
  }
}
