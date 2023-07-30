package com.summerschool.artificiumanima.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;

public abstract class AbstractAiService<T> implements AiService {
  private final Map<String, List<T>> history;

  protected AbstractAiService() {
    this.history = new HashMap<>();
  }

  protected abstract T getRoleMessage(String roleMessage);

  @Override
  public boolean setRole(String user, String roleMessage) {
    final T systemMessage = getRoleMessage(roleMessage);
    addMessage(user, systemMessage);
    return true;
  }

  @Override
  public void forget(String user) {
    history.remove(user);
  }

  protected List<T> getMessages(String user) {
    history.computeIfAbsent(user, k -> new ArrayList<>());
    return history.get(user);
  }

  protected void addMessage(String user, T chatMessage) {
    history.computeIfAbsent(user, k -> new ArrayList<>());
    history.get(user).add(chatMessage);
  }

  protected void addMessages(String user, List<T> chatMessages) {
    CollectionUtils.emptyIfNull(chatMessages).forEach(cm -> this.addMessage(user, cm));
  }
}
