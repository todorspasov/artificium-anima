package com.summerschool.artificiumanima.commands.discord;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.summerschool.artificiumanima.commands.Command;
import com.summerschool.artificiumanima.markdown.MarkdownConstants;
import com.summerschool.artificiumanima.service.slack.SlackService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;

@Slf4j
@Component
public class SlackListenCommand implements Command<Message> {

  private static final String SLACK_LISTEN_COMMAND_KEY = "!slack-listen";
  private static final String SLACK_LISTEN_COMMAND_INFO =
      "Listen to what top secret agents are writing in a top secret slack channel. No arguments needed. Example: '!slack-listen'";

  private static final String LISTEN_RESPONSE_FORMAT = MarkdownConstants.HEADER_2
      .concat("Artificial Oracle heard the following conversation in slack: :pencil:")
      .concat(System.lineSeparator()).concat("%s");

  private final SlackService slackService;

  @Autowired
  public SlackListenCommand(SlackService slackService) {
    this.slackService = slackService;
  }

  @Override
  public void execute(String commandStr, Message message) {
    final String slackChannelId = this.slackService.getDefaultChannelId();
    final String slackChannelName = this.slackService.getChannelName(slackChannelId);
    log.info("Listening to top-secret slack channel with id: '{}' and name '{}'.", slackChannelId,
        slackChannelName);
    final List<String> channelMessages = this.slackService.getChannelMessages(slackChannelId);
    final String fullMessage =
        channelMessages.stream().collect(Collectors.joining(System.lineSeparator()));
    message.getChannel().sendMessage(String.format(LISTEN_RESPONSE_FORMAT, fullMessage)).queue();
  }

  @Override
  public String getCommandKey() {
    return SLACK_LISTEN_COMMAND_KEY;
  }

  @Override
  public String getCommandInfo() {
    return SLACK_LISTEN_COMMAND_INFO;
  }
}
