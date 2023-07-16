package com.summerschool.artificiumanima.commands.discord;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.summerschool.artificiumanima.commands.AbstractCommandRegistry;
import com.summerschool.artificiumanima.commands.Command;
import net.dv8tion.jda.api.entities.Message;

@Component
public class DiscordCommandRegistryImpl extends AbstractCommandRegistry<Message> {

  @Autowired
  public DiscordCommandRegistryImpl(List<Command<Message>> discordCommands) {
    super(discordCommands);
  }
}
