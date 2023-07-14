package com.tspasov.artificiumanima.commands;

public interface Command<T> {
  void execute(String commandStr, T channel);

  String getCommandKey();

  String getCommandInfo();
}
