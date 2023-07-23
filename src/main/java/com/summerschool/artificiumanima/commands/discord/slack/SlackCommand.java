package com.summerschool.artificiumanima.commands.discord.slack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.summerschool.artificiumanima.commands.Command;
import com.summerschool.artificiumanima.commands.CommandInfo;
import com.summerschool.artificiumanima.service.slack.SlackService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;

@Slf4j
@Component
public class SlackCommand implements Command<Message> {

  private static final String SLACK_COMMAND_KEY = "!slack";
  private static final String SLACK_COMMAND_DESCRIPTION =
      "Send a command to a top secret slack channel. Example: '!slack Hello Mr. Anderson!'";

  private static final String SLACK_MESSAGE_FORMAT = "Discord user '%s' wrote in channel '%s': %s";

  private final SlackService slackService;

  @Autowired
  public SlackCommand(SlackService slackService) {
    this.slackService = slackService;
  }

  @Override
  public void execute(String commandStr, Message message) {
    final String discordUserName = message.getAuthor().getName();
    final String discordChannel = message.getChannel().getName();
    final String slackChannelId = this.slackService.getDefaultChannelId();
    final String slackChannelName = this.slackService.getChannelName(slackChannelId);
    final String slackMessage =
        String.format(SLACK_MESSAGE_FORMAT, discordUserName, discordChannel, commandStr);
    log.info(
        "Sending message to top-secret slack channel with Id: '{}' and name: '#{}'. Message: '{}'",
        slackChannelId, slackChannelName, discordUserName, slackMessage);
    final boolean isOK = this.slackService.sendMessage(slackMessage, slackChannelId);
    log.info("Message sent: {}", isOK);
  }

  @Override
  public CommandInfo getCommandInfo() {
    return CommandInfo.builder().commandKey(SLACK_COMMAND_KEY)
        .commandDescription(SLACK_COMMAND_DESCRIPTION).commandGroup("Slack").build();
  }
}
