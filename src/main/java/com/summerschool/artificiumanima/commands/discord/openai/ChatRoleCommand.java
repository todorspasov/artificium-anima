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
public class ChatRoleCommand implements Command<Message> {

  private static final String CHATGPT_ROLE_COMMAND_KEY = "!chatgpt-role";
  private static final String CHATGPT_ROLE_COMMAND_DESCRIPTION =
      "Tell the Artificial Oracle to behave as a certain role. Example: '!chatgpt-role Act as a history teacher'";
  private static final String CHATGPT_ROLE_COMMAND_REPLY_FORMAT = String.format(
      MarkdownConstants.BOLD_TEXT_FORMAT,
      "When user %s asks the Artificial Oracle :desktop: :brain: something, it will try to behave according to the role: ")
      .concat(MarkdownConstants.ITALICS_TEXT_FORMAT);
  private static final String CANNOT_SET_ROLE_FORMAT = String.format(
      MarkdownConstants.BOLD_TEXT_FORMAT, "Could not set role for user %s, please try again.");

  private final AiService aiService;
  private final ChatBotService<AudioChannel, Message> chatService;

  @Autowired
  public ChatRoleCommand(AiService aiService,
      @Lazy ChatBotService<AudioChannel, Message> chatService) {
    this.aiService = aiService;
    this.chatService = chatService;
  }

  @Override
  public void execute(String commandStr, Message message) {
    final String userName = message.getAuthor().getName();
    if (aiService.setRole(userName, commandStr)) {
      final String roleSet = String.format(CHATGPT_ROLE_COMMAND_REPLY_FORMAT, userName, commandStr);
      this.chatService.sendMessage(roleSet, message);
    } else {
      final String roleNotSet = String.format(CANNOT_SET_ROLE_FORMAT, userName);
      this.chatService.sendMessage(roleNotSet, message);
    }
  }

  @Override
  public CommandInfo getCommandInfo() {
    return CommandInfo.builder().commandKey(CHATGPT_ROLE_COMMAND_KEY)
        .commandDescription(CHATGPT_ROLE_COMMAND_DESCRIPTION).commandGroup("OpenAI").build();
  }
}
