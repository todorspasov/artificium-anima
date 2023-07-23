package com.summerschool.artificiumanima.commands;

import java.util.List;

public interface CommandRegistry<T> {

  void registerCommandAction(Command<T> command);

  void onCommandReceived(String command, T message);

  List<CommandInfo> getCommandsInfo();
}
