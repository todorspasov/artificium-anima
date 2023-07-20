package com.summerschool.artificiumanima.service.slack;

import java.util.List;

public interface SlackService {
  boolean sendMessage(String message, String channelId);

  boolean testAuthentication();

  String getDefaultChannelId();

  String getChannelName(String channelId);

  List<String> getChannelMessages(String channelId);
}
