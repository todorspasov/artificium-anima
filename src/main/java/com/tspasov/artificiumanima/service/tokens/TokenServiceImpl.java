package com.tspasov.artificiumanima.service.tokens;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TokenServiceImpl implements TokenService {

  private final String gptToken;
  private final String discordBotToken;

  @Autowired
  public TokenServiceImpl(@Value("${gpt.token}") String gptToken,
      @Value("${discord.bot.token}") String discordBotToken) {
    this.gptToken = gptToken;
    this.discordBotToken = discordBotToken;
  }

  @Override
  public String getGptToken() {
    return this.gptToken;
  }

  @Override
  public String getDiscordBotToken() {
    return this.discordBotToken;
  }

}
