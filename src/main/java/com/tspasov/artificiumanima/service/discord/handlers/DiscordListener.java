package com.tspasov.artificiumanima.service.discord.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.tspasov.artificiumanima.commands.CommandRegistry;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Slf4j
@Component
public class DiscordListener extends ListenerAdapter {

  private final CommandRegistry<Message> commandRegistry;

  @Autowired
  public DiscordListener(CommandRegistry<Message> commandRegistry) {
    this.commandRegistry = commandRegistry;
  }

  @Override
  public void onMessageReceived(MessageReceivedEvent event) {
    final String contentRaw = event.getMessage().getContentRaw();
    log.info("Received message from '{}' in channel '{}', channel type '{}', contents: '{}'",
        event.getAuthor().getName(), event.getChannel().getName(), event.getChannel().getType(),
        contentRaw);
    if (!event.getAuthor().isBot()) {
      this.commandRegistry.onCommandReceived(contentRaw, event.getMessage());
    } else {
      log.info("Skipping processing message as it is from bot: {}", event.getAuthor().getName());
    }
  }

}
