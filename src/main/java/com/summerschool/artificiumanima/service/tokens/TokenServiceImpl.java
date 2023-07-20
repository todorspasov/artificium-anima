package com.summerschool.artificiumanima.service.tokens;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TokenServiceImpl implements TokenService {

  private final String gptToken;
  private final String discordBotToken;
  private final String slackBotToken;

  @Autowired
  public TokenServiceImpl(@Value("${openai.token}") String gptToken,
      @Value("${discord.bot.token}") String discordBotToken,
      @Value("${slack.bot.token}") String slackBotToken) {
    this.gptToken = gptToken;
    this.discordBotToken = discordBotToken;
    this.slackBotToken = slackBotToken;
  }

  @Override
  public String getOpenAiToken() {
    return this.gptToken;
  }

  @Override
  public String getDiscordBotToken() {
    return this.discordBotToken;
  }

  @Override
  public String getSlackBotToken() {
    return this.slackBotToken;
  }
}
