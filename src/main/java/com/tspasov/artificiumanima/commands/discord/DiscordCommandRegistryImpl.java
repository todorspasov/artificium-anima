package com.tspasov.artificiumanima.commands.discord;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.tspasov.artificiumanima.commands.AbstractCommandRegistry;
import com.tspasov.artificiumanima.commands.Command;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

@Component
public class DiscordCommandRegistryImpl extends AbstractCommandRegistry<MessageChannel> {

  @Autowired
  public DiscordCommandRegistryImpl(List<Command<MessageChannel>> discordCommands) {
    super(discordCommands);
  }
}
