package com.summerschool.artificiumanima.commands;

public interface Command<T> {
  void execute(String commandArgs, T message);

  String getCommandKey();

  String getCommandInfo();
}