package com.summerschool.artificiumanima.commands.discord;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import com.summerschool.artificiumanima.commands.Command;
import com.summerschool.artificiumanima.commands.CommandInfo;
import com.summerschool.artificiumanima.commands.CommandRegistry;
import com.summerschool.artificiumanima.service.ChatBotService;
import com.summerschool.artificiumanima.utils.MarkdownConstants;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;

@Component
public class HelpCommand implements Command<Message> {

  private static final String HELP_COMMAND_KEY = "!help";
  private static final String HELP_COMMAND_DESCRIPTION = "Show available commands";

  private static final String HELP_COMMAND_RESPONSE_FORMAT = MarkdownConstants.HEADER_3
      + ":information_source: Here are the available commands: :information_source:"
      + System.lineSeparator() + "%s";

  private static final String COMMAND_GROUP_HEADER_FORMAT = MarkdownConstants.HEADER_3.concat("%s");
  private static final String COMMAND_FORMAT = MarkdownConstants.BULLET_LIST
      + MarkdownConstants.BOLD_TEXT_FORMAT + " -> " + MarkdownConstants.ITALICS_TEXT_FORMAT;
  private static final String COMMANDS_SEPARATOR = System.lineSeparator();

  private final CommandRegistry<Message> commandRegistry;
  private final ChatBotService<AudioChannel, Message> chatService;


  @Autowired
  public HelpCommand(@Lazy CommandRegistry<Message> commandRegistry,
      @Lazy ChatBotService<AudioChannel, Message> chatService) {
    this.commandRegistry = commandRegistry;
    this.chatService = chatService;
  }

  @Override
  public void execute(String commandStr, Message message) {
    final StringBuilder sb = new StringBuilder();

    final Map<String, List<CommandInfo>> groupedCommands =
        CollectionUtils.emptyIfNull(commandRegistry.getCommandsInfo()).stream().collect(
            Collectors.groupingBy(x -> StringUtils.getIfBlank(x.getCommandGroup(), () -> "")));
    groupedCommands.entrySet().stream().forEach(entry -> {
      final String groupHeader = String.format(COMMAND_GROUP_HEADER_FORMAT, entry.getKey());
      sb.append(groupHeader).append(COMMANDS_SEPARATOR);
      entry.getValue().stream()
          .forEach(ci -> sb
              .append(String.format(COMMAND_FORMAT, ci.getCommandKey(), ci.getCommandDescription()))
              .append(COMMANDS_SEPARATOR));
      sb.append(COMMANDS_SEPARATOR);
    });
    final String overallHelpMessage = String.format(HELP_COMMAND_RESPONSE_FORMAT, sb.toString());
    this.chatService.sendMessage(overallHelpMessage, message);
  }

  @Override
  public CommandInfo getCommandInfo() {
    return CommandInfo.builder().commandKey(HELP_COMMAND_KEY)
        .commandDescription(HELP_COMMAND_DESCRIPTION).commandGroup("General").build();
  }
}
