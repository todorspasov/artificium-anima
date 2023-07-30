package com.summerschool.artificiumanima.service.slack;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.auth.AuthTestRequest;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.request.conversations.ConversationsHistoryRequest;
import com.slack.api.methods.request.conversations.ConversationsInfoRequest;
import com.slack.api.methods.response.auth.AuthTestResponse;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import com.slack.api.methods.response.conversations.ConversationsHistoryResponse;
import com.slack.api.methods.response.conversations.ConversationsInfoResponse;
import com.summerschool.artificiumanima.service.tokens.TokenService;
import com.summerschool.artificiumanima.utils.MarkdownConstants;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SlackServiceImpl implements SlackService {

  private static final String HISTORY_MESSAGE_FORMAT = MarkdownConstants.BOLD_TEXT_FORMAT
      .concat(" wrote: ").concat(MarkdownConstants.BOLD_TEXT_FORMAT);

  private final TokenService tokenService;
  private final Slack slack;
  private final MethodsClient methodsClient;
  private final String defaultChannelId;

  @Autowired
  public SlackServiceImpl(TokenService tokenService,
      @Value("${slack.bot.channel}") String defaultChannelId) {
    this.tokenService = tokenService;
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
      if (!isOK) {
        log.error("Received slack error response: {}", response.getErrors());
      }
    } catch (IOException | SlackApiException e) {
      log.error("Could not send slack message to channel {}.", channelId, e);
    }
    return isOK;
  }

  @Override
  public String getDefaultChannelId() {
    return this.defaultChannelId;
  }

  @Override
  public boolean testAuthentication() {
    boolean isOK = false;
    final AuthTestRequest authRequest =
        AuthTestRequest.builder().token(this.tokenService.getSlackBotToken()).build();
    try {
      final AuthTestResponse authResponse = this.methodsClient.authTest(authRequest);
      log.info("Received auth response: {}", authResponse);
      isOK = authResponse.isOk();
      if (!isOK) {
        log.error("Could not authenticate with slack");
      }
    } catch (IOException | SlackApiException e) {
      log.error("Exception occurred while trying to authenticate with slack.", e);
    }
    return isOK;
  }

  @Override
  public String getChannelName(String channelId) {
    final ConversationsInfoRequest req =
        ConversationsInfoRequest.builder().channel(channelId).build();
    try {
      final ConversationsInfoResponse response = this.methodsClient.conversationsInfo(req);
      if (response.isOk()) {
        return response.getChannel().getName();
      } else {
        log.error("Could not get channel info from channel with Id {}. Errors: {}", channelId,
            response.getError());
      }
    } catch (IOException | SlackApiException e) {
      log.error("Exception occurred while trying to retrieve channel info.", e);
    }
    return null;
  }

  @Override
  public List<String> getChannelMessages(String channelId) {
    ConversationsHistoryRequest req =
        ConversationsHistoryRequest.builder().channel(channelId).build();
    try {
      final ConversationsHistoryResponse response = this.methodsClient.conversationsHistory(req);
      if (response.isOk()) {
        return response.getMessages().stream()
            .map(m -> String.format(HISTORY_MESSAGE_FORMAT,
                m.getBotProfile() != null ? m.getBotProfile().getName() : m.getUser(), m.getText()))
            .toList();
      } else {
        log.error("Could not read channel messages. Error: {}", response.getError());
      }
    } catch (IOException | SlackApiException e) {
      log.error("Exception occurred while trying to read channel messages", e);
    }
    return Collections.emptyList();
  }
}
