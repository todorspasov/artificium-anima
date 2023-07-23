package com.summerschool.artificiumanima.commands.discord.openai;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import com.summerschool.artificiumanima.commands.Command;
import com.summerschool.artificiumanima.commands.CommandInfo;
import com.summerschool.artificiumanima.service.AiService;
import com.summerschool.artificiumanima.service.ChatBotService;
import com.summerschool.artificiumanima.utils.MarkdownConstants;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;

@Component
public class ChatCommand implements Command<Message> {

  private static final String CHATGPT_COMMAND_KEY = "!chatgpt";
  private static final String CHATGPT_COMMAND_DESCRIPTION =
      "Ask the Artificial Oracle something. Example: '!chatgpt what is the age of the Sun'";
  private static final String CHATGPT_COMMAND_REPLY_FORMAT =
      String.format(MarkdownConstants.BOLD_TEXT_FORMAT,
          "Artificial Oracle :desktop: :brain: answered: ") + "%s";

  private final AiService aiService;
  private final ChatBotService<AudioChannel, Message> chatService;

  @Autowired
  public ChatCommand(AiService aiService, @Lazy ChatBotService<AudioChannel, Message> chatService) {
    this.aiService = aiService;
    this.chatService = chatService;
  }

  @Override
  public void execute(String commandStr, Message message) {
    final String userName = message.getAuthor().getName();
    final String answer = aiService.askQuestion(userName, commandStr);
    final String chatMessage = String.format(CHATGPT_COMMAND_REPLY_FORMAT, answer);
    this.chatService.sendMessage(chatMessage, message);
  }

  @Override
  public CommandInfo getCommandInfo() {
    return CommandInfo.builder().commandKey(CHATGPT_COMMAND_KEY)
        .commandDescription(CHATGPT_COMMAND_DESCRIPTION).commandGroup("OpenAI").build();
  }
}
