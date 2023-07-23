package com.summerschool.artificiumanima.commands;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommandInfo {
  private final String commandKey;
  private final String commandDescription;
  private final String commandGroup;
}
