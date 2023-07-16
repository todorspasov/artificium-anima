package com.summerschool.artificiumanima.commands.discord;

import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import com.summerschool.artificiumanima.commands.Command;
import com.summerschool.artificiumanima.commands.CommandRegistry;
import com.summerschool.artificiumanima.markdown.MarkdownConstants;
import net.dv8tion.jda.api.entities.Message;

@Component
public class HelpCommand implements Command<Message> {

  private static final String HELP_COMMAND_KEY = "!help";
  private static final String HELP_COMMAND_INFO = "Show available commands";

  private static final String HELP_COMMAND_RESPONSE_FORMAT = MarkdownConstants.HEADER_3
      + " :information_source: Here are the available commands:" + System.lineSeparator() + "%s";

  private static final String COMMAND_FORMAT = MarkdownConstants.BULLET_LIST
      + MarkdownConstants.BOLD_TEXT_FORMAT + " -> " + MarkdownConstants.ITALICS_TEXT_FORMAT;
  private static final String COMMANDS_SEPARATOR = System.lineSeparator();

  private final CommandRegistry<Message> commandRegistry;

  @Autowired
  public HelpCommand(@Lazy CommandRegistry<Message> commandRegistry) {
    this.commandRegistry = commandRegistry;
  }

  @Override
  public void execute(String commandStr, Message message) {
    final String helpMessage =
        CollectionUtils.emptyIfNull(commandRegistry.getCommandsInfo().entrySet()).stream()
            .map(e -> String.format(COMMAND_FORMAT, e.getKey(), e.getValue()))
            .collect(Collectors.joining(COMMANDS_SEPARATOR));
    message.getChannel().sendMessage(String.format(HELP_COMMAND_RESPONSE_FORMAT, helpMessage))
        .queue();
  }

  @Override
  public String getCommandKey() {
    return HELP_COMMAND_KEY;
  }

  @Override
  public String getCommandInfo() {
    return HELP_COMMAND_INFO;
  }
}
