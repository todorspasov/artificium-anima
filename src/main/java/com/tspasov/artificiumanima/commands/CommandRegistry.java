package com.tspasov.artificiumanima.commands;

public interface CommandRegistry<T> {

  void registerCommandAction(Command<T> command);

  void onCommandReceived(String command, T channel);

}
