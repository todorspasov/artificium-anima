package com.tspasov.artificiumanima.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

@Slf4j
@Component
public class CommandRegistry {
  private static final Pattern REGEX_PATTERN = Pattern.compile("^(\\S+)\\s+(.*?)(?:\\s+)?$");

  private Map<String, List<Command>> registry = new HashMap<>();

  public static interface Command {
    public void execute(String commandStr, MessageChannel channel);
  }

  public void registerCommandAction(final Command command, final String commandPrefix) {
    registry.putIfAbsent(commandPrefix, new ArrayList<>());
    registry.get(commandPrefix).add(command);
  }

  public void onCommandReceived(final String command, final MessageChannel channel) {
    if (StringUtils.isNotBlank(command)) {
      Matcher m = REGEX_PATTERN.matcher(command);
      if (m.find()) {
        List<Command> commands = registry.get(m.group(1));
        log.info("Found regex command: group1 {}, group2 {}, commands size: {}", m.group(1), m.group(2), CollectionUtils.emptyIfNull(commands).size());
        CollectionUtils.emptyIfNull(commands).stream().forEach(cmd -> cmd.execute(command, channel));
      } else {
        log.info("No matched command for: {}", command);
      }
    }
  }
}
