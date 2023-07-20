package com.summerschool.artificiumanima.service.slack;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import com.summerschool.artificiumanima.service.tokens.TokenService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SlackServiceImpl implements SlackService {

  private final Slack slack;
  private final MethodsClient methodsClient;
  private final String defaultChannelId;


  @Autowired
  public SlackServiceImpl(TokenService tokenService,
      @Value("${slack.bot.channel}") String defaultChannelId) {
    this.slack = Slack.getInstance();
    this.methodsClient = this.slack.methods(tokenService.getSlackBotToken());
    this.defaultChannelId = defaultChannelId;
  }

  @Override
  public boolean sendMessage(String message, String channelId) {
    boolean isOK = false;
    final ChatPostMessageRequest request =
        ChatPostMessageRequest.builder().channel(channelId).text(message).build();
    try {
      final ChatPostMessageResponse response = this.methodsClient.chatPostMessage(request);
      isOK = response.isOk();
    } catch (IOException | SlackApiException e) {
      log.error("Could not send slack message to channel {}.", channelId, e);
    }
    return isOK;
  }

  @Override
  public String getDefaultChannelId() {
    return this.defaultChannelId;
  }
}
