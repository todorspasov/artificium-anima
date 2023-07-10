package com.tspasov.artificiumanima.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AbstractCommandRegistry<T> implements CommandRegistry<T> {
  private static final Pattern COMMAND_PATTERN = Pattern.compile("^(\\S+)\\s+(.*?)(?:\\s+)?$");

  protected Map<String, List<Command<T>>> registry;

  protected AbstractCommandRegistry(List<Command<T>> commands) {
    this.registry = new HashMap<>();
    CollectionUtils.emptyIfNull(commands).stream().forEach(this::registerCommandAction);
  }

  @Override
  public void registerCommandAction(final Command<T> command) {
    registry.putIfAbsent(command.getCommandKey(), new ArrayList<>());
    registry.get(command.getCommandKey()).add(command);
  }

  @Override
  public void onCommandReceived(String command, T channel) {
    if (StringUtils.isNotBlank(command)) {
      final Matcher m = COMMAND_PATTERN.matcher(command);
      if (m.find()) {
        final String commandKey = m.group(1);
        final String commandArgs = m.group(2);
        List<Command<T>> commands = registry.get(commandKey);
        log.info("Found {} commands to match command '{}'. Passing arguments '{}'.",
            CollectionUtils.emptyIfNull(commands).size(), commandKey, commandArgs);
        CollectionUtils.emptyIfNull(commands).stream()
            .forEach(cmd -> cmd.execute(commandArgs, channel));
      } else {
        log.info("No command available to serve: {}", command);
      }
    }
  }
}
