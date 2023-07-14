package com.tspasov.artificiumanima.commands.discord;

import org.springframework.stereotype.Component;
import com.tspasov.artificiumanima.commands.Command;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

@Component
public class PingCommand implements Command<MessageChannel> {

  private static final String PING_COMMAND_KEY = "!ping";
  private static final String PING_COMMAND_INFO = "Reply as a parrot";

  private static final String PING_COMMAND_REPLY_FORMAT =
      "Pong! Why did you say: " + MarkdownConstants.ITALICS_TEXT_FORMAT;

  @Override
  public void execute(String commandStr, MessageChannel channel) {
    channel.sendMessage(String.format(PING_COMMAND_REPLY_FORMAT, commandStr)).queue();
  }

  @Override
  public String getCommandKey() {
    return PING_COMMAND_KEY;
  }

  @Override
  public String getCommandInfo() {
    return PING_COMMAND_INFO;
  }
}
