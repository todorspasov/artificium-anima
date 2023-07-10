package com.tspasov.artificiumanima.api.discord;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.tspasov.artificiumanima.api.CommandRegistry;
import com.tspasov.artificiumanima.api.CommandRegistry.Command;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Slf4j
@Component
public class DiscordListener extends ListenerAdapter {
  private static final String CMD_PING = "!ping";
  private static final String CMD_CHATGPT = "!chatgpt";

  private final CommandRegistry commandRegistry;

  @Autowired
  public DiscordListener(CommandRegistry commandRegistry) {
    this.commandRegistry = commandRegistry;
    registerDiscordCommands();
  }


  private void registerDiscordCommands() {
    this.commandRegistry.registerCommandAction(new Command() {
      @Override
      public void execute(String commandStr, MessageChannel channel) {
        channel.sendMessage("Pong!").queue();
      }
    }, CMD_PING);
  }


  @Override
  public void onMessageReceived(MessageReceivedEvent event) {
    final String contentRaw = event.getMessage().getContentRaw();
    log.info("Received message from '{}' in channel '{}', channel type '{}', contents: '{}'",
        event.getAuthor().getName(), event.getChannel().getName(), event.getChannel().getType(),
        contentRaw);
    this.commandRegistry.onCommandReceived(contentRaw, event.getChannel());
  }
}
