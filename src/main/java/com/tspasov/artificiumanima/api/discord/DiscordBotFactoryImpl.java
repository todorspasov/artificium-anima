package com.tspasov.artificiumanima.api.discord;

import java.util.EnumSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.tspasov.artificiumanima.api.tokens.TokenService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

@Component
@Slf4j
public class DiscordBotFactoryImpl implements DiscordBotFactory {
  private static final EnumSet<GatewayIntent> BOT_INTENTS = EnumSet.of(
      // Enables MessageReceivedEvent for guild (also known as servers)
      GatewayIntent.GUILD_MESSAGES,
      // Enables the event for private channels (also known as direct messages)
      GatewayIntent.DIRECT_MESSAGES,
      // Enables access to message.getContentRaw()
      GatewayIntent.MESSAGE_CONTENT,
      // Enables MessageReactionAddEvent for guild
      GatewayIntent.GUILD_MESSAGE_REACTIONS,
      // Enables MessageReactionAddEvent for private channels
      GatewayIntent.DIRECT_MESSAGE_REACTIONS);

  private DiscordListener discordListener;
  private TokenService tokenService;

  @Autowired
  public DiscordBotFactoryImpl(DiscordListener discordListener, TokenService tokenService) {
    this.discordListener = discordListener;
    this.tokenService = tokenService;
  }

  @Override
  public JDA createBot() {
    final String token = this.tokenService.getDiscordBotToken();
    final JDA jda =
        JDABuilder.createLight(token, BOT_INTENTS).addEventListeners(discordListener).build();
    log.info(
        "Successfully created a discord bot with id '{}', name '{}' having the following intents: {}",
        jda.getSelfUser().getId(), jda.getSelfUser().getName(), BOT_INTENTS);
    return jda;
  }
}
