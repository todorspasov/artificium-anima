package com.tspasov.artificiumanima.commands.discord;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import com.tspasov.artificiumanima.commands.Command;
import com.tspasov.artificiumanima.service.ChatBotService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;

@Slf4j
@Component
public class QuitCommand implements Command<Message> {

  private static final String QUIT_COMMAND_KEY = "!quit";
  private static final String QUIT_COMMAND_INFO =
      "Ask the Artificial Oracle to disappear from discord";

  private final ChatBotService<AudioChannel, Message> discordService;

  @Autowired
  public QuitCommand(@Lazy ChatBotService<AudioChannel, Message> discordService) {
    this.discordService = discordService;
  }

  @Override
  public void execute(String commandStr, Message message) {
    log.info("Quitting discord. Args: {}", commandStr);
    this.discordService.shutDownBot();
  }

  @Override
  public String getCommandKey() {
    return QUIT_COMMAND_KEY;
  }

  @Override
  public String getCommandInfo() {
    return QUIT_COMMAND_INFO;
  }
}
