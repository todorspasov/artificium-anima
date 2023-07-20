package com.summerschool.artificiumanima.service.slack;

public interface SlackService {
  boolean sendMessage(String message, String channelId);
  String getDefaultChannelId();
}
