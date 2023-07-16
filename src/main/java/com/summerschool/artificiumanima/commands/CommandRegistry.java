package com.summerschool.artificiumanima.commands;

import java.util.Map;

public interface CommandRegistry<T> {

  void registerCommandAction(Command<T> command);

  void onCommandReceived(String command, T message);

  Map<String, String> getCommandsInfo();
}
