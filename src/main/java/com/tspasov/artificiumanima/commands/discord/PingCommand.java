package com.tspasov.artificiumanima.commands.discord;

import com.tspasov.artificiumanima.commands.Command;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

public class PingCommand implements Command<MessageChannel> {

  private static final String PING_COMMAND_KEY = "!ping";

  @Override
  public void execute(String commandStr, MessageChannel channel) {
    channel.sendMessage(String.format("Pong! Why did you say: %s", commandStr)).queue();
  }

  @Override
  public String getCommandKey() {
    return PING_COMMAND_KEY;
  }
}
