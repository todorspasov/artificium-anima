package com.tspasov.artificiumanima.commands;

import java.util.Map;

public interface CommandRegistry<T> {

  void registerCommandAction(Command<T> command);

  void onCommandReceived(String command, T channel);

  Map<String, String> getCommandsInfo();
}
