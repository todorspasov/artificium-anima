package com.summerschool.artificiumanima.commands.openai;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import com.summerschool.artificiumanima.commands.Command;
import com.summerschool.artificiumanima.service.AiService;
import com.summerschool.artificiumanima.service.ChatBotService;
import com.summerschool.artificiumanima.utils.MarkdownConstants;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;

@Component
public class ChatGptCommand implements Command<Message> {

  private static final String CHATGPT_COMMAND_KEY = "!chatgpt";
  private static final String CHATGPT_COMMAND_INFO =
      "Ask the Artificial Oracle something. Example: '!chatgpt what is the age of the Sun'";
  private static final String CHATGPT_COMMAND_REPLY_FORMAT =
      String.format(MarkdownConstants.BOLD_TEXT_FORMAT,
          "Artificial Oracle :desktop: :brain: answered: ") + "%s";

  private final AiService aiService;
  private final ChatBotService<AudioChannel, Message> chatService;

  @Autowired
  public ChatGptCommand(AiService aiService,
      @Lazy ChatBotService<AudioChannel, Message> chatService) {
    this.aiService = aiService;
    this.chatService = chatService;
  }

  @Override
  public void execute(String commandStr, Message message) {
    // ask ChatGPT about the commandStr
    // get response, and then paste it in discord
    final String answer = aiService.askQuestion(commandStr);
    final String chatMessage = String.format(CHATGPT_COMMAND_REPLY_FORMAT, answer);
    this.chatService.sendMessage(chatMessage, message);
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
