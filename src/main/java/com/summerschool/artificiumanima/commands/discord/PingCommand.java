package com.summerschool.artificiumanima.commands.discord;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import com.summerschool.artificiumanima.commands.Command;
import com.summerschool.artificiumanima.commands.CommandInfo;
import com.summerschool.artificiumanima.service.ChatBotService;
import com.summerschool.artificiumanima.utils.MarkdownConstants;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;

@Component
public class PingCommand implements Command<Message> {

  private static final String PING_COMMAND_KEY = "!ping";
  private static final String PING_COMMAND_DESCRIPTION =
      "Reply as a parrot. Optional argument: free text. Example: '!ping repeat after me'";

  private static final String PING_COMMAND_REPLY_FORMAT =
      "Pong! Why did you say: " + MarkdownConstants.ITALICS_TEXT_FORMAT;

  private final ChatBotService<AudioChannel, Message> chatService;

  @Autowired
  public PingCommand(@Lazy ChatBotService<AudioChannel, Message> chatService) {
    this.chatService = chatService;
  }

  @Override
  public void execute(String commandStr, Message message) {
    final String replyMessage = String.format(PING_COMMAND_REPLY_FORMAT, commandStr);
    this.chatService.sendMessage(replyMessage, message);
  }

  @Override
  public CommandInfo getCommandInfo() {
    return CommandInfo.builder().commandKey(PING_COMMAND_KEY)
        .commandDescription(PING_COMMAND_DESCRIPTION).commandGroup("General").build();
  }
}
