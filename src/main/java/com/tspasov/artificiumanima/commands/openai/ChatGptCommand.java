package com.tspasov.artificiumanima.commands.openai;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.tspasov.artificiumanima.commands.Command;
import com.tspasov.artificiumanima.commands.discord.MarkdownConstants;
import com.tspasov.artificiumanima.service.AIService;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

@Component
public class ChatGptCommand implements Command<MessageChannel> {

  private static final String CHATGPT_COMMAND_KEY = "!chatgpt";
  private static final String CHATGPT_COMMAND_INFO = "Ask the Artificial Oracle something";
  private static final String CHATGPT_COMMAND_REPLY_FORMAT =
      String.format(MarkdownConstants.BOLD_TEXT_FORMAT, "Artificial Oracle :desktop: :brain: answered: ") + "%s";

  private final AIService chatGptService;

  @Autowired
  public ChatGptCommand(AIService chatGptService) {
    this.chatGptService = chatGptService;
  }

  @Override
  public void execute(String commandStr, MessageChannel channel) {
    // ask ChatGPT about the commandStr
    // get response, and then paste it in discord
    final String answer = chatGptService.askQuestion(commandStr);
    channel.sendMessage(String.format(CHATGPT_COMMAND_REPLY_FORMAT, answer)).queue();
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
