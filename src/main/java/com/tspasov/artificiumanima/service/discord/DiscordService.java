package com.tspasov.artificiumanima.service.discord;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDA.Status;

@Component
@Slf4j
public class DiscordService {

  private final DiscordBotFactory botFactory;
  private JDA discordBot;

  @Autowired
  public DiscordService(@Value("${connect.bot.on.startup:true}") boolean connectBotOnStartup,
      DiscordBotFactory botFactory) {
    this.botFactory = botFactory;
    if (connectBotOnStartup) {
      log.info("Initializing discord bot at startup");
      initBot();
    }
  }

  public void initBot() {
    if (this.discordBot == null || this.discordBot.getStatus() == Status.SHUTDOWN) {
      this.discordBot = botFactory.createBot();
    }
  }

  public void shutDownBot() {
    if (this.discordBot != null && this.discordBot.getStatus() != Status.SHUTDOWN
        && this.discordBot.getStatus() != Status.SHUTTING_DOWN) {
      log.info("Shutting down discord bot, current bot status: {}", this.discordBot.getStatus());
      this.discordBot.shutdown();
    }
  }
}
