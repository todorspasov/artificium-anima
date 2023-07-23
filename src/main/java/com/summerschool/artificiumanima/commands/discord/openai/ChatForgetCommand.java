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
public class ChatForgetCommand implements Command<Message> {

  private static final String CHATGPT_FORGET_COMMAND_KEY = "!chatgpt-forget";
  private static final String CHATGPT_FORGET_COMMAND_DESCRIPTION =
      "Tell the Artificial Oracle to forget all previous chat history. Example: '!chatgpt-forget'";
  private static final String CHATGPT_FORGET_COMMAND_REPLY_FORMAT = String.format(
      MarkdownConstants.BOLD_TEXT_FORMAT,
      "The Artificial Oracle :desktop: :brain: forgot everything that user %s has told him so far.");

  private final AiService aiService;
  private final ChatBotService<AudioChannel, Message> chatService;

  @Autowired
  public ChatForgetCommand(AiService aiService,
      @Lazy ChatBotService<AudioChannel, Message> chatService) {
    this.aiService = aiService;
    this.chatService = chatService;
  }

  @Override
  public void execute(String commandStr, Message message) {
    final String userName = message.getAuthor().getName();
    this.aiService.forget(userName);
    final String forgottenResponse = String.format(CHATGPT_FORGET_COMMAND_REPLY_FORMAT, userName);
    this.chatService.sendMessage(forgottenResponse, message);
  }

  @Override
  public CommandInfo getCommandInfo() {
    return CommandInfo.builder().commandKey(CHATGPT_FORGET_COMMAND_KEY)
        .commandDescription(CHATGPT_FORGET_COMMAND_DESCRIPTION).commandGroup("OpenAI").build();
  }
}
