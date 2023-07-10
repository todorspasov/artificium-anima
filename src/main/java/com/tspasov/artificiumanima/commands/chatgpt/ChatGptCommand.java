package com.tspasov.artificiumanima.commands.chatgpt;

import org.springframework.stereotype.Component;
import com.tspasov.artificiumanima.commands.Command;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

@Component
public class ChatGptCommand implements Command<MessageChannel> {

  private static final String CHATGPT_COMMAND_KEY = "!chatgpt";

  @Override
  public void execute(String commandStr, MessageChannel channel) {
    //ask ChatGPT about the commandStr
    //get response, and then paste it in discord
    channel.sendMessage(String.format("Pong! Why did you say: %s", commandStr)).queue();
  }

  @Override
  public String getCommandKey() {
    return CHATGPT_COMMAND_KEY;
  }
}
