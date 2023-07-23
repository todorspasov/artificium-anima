package com.summerschool.artificiumanima.commands.discord.slack;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import com.summerschool.artificiumanima.commands.Command;
import com.summerschool.artificiumanima.commands.CommandInfo;
import com.summerschool.artificiumanima.service.ChatBotService;
import com.summerschool.artificiumanima.service.slack.SlackService;
import com.summerschool.artificiumanima.utils.MarkdownConstants;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;

@Slf4j
@Component
public class SlackListenCommand implements Command<Message> {

  private static final String SLACK_LISTEN_COMMAND_KEY = "!slack-listen";
  private static final String SLACK_LISTEN_COMMAND_DESCRIPTION =
      "Listen to what top secret agents are writing in a top secret slack channel. No arguments needed. Example: '!slack-listen'";

  private static final String LISTEN_RESPONSE_FORMAT = MarkdownConstants.HEADER_2
      .concat("Artificial Oracle heard the following conversation in slack: :pencil:")
      .concat(System.lineSeparator()).concat("%s");

  private final SlackService slackService;
  private final ChatBotService<AudioChannel, Message> chatService;

  @Autowired
  public SlackListenCommand(SlackService slackService,
      @Lazy ChatBotService<AudioChannel, Message> chatService) {
    this.slackService = slackService;
    this.chatService = chatService;
  }

  @Override
  public void execute(String commandStr, Message message) {
    final String slackChannelId = this.slackService.getDefaultChannelId();
    final String slackChannelName = this.slackService.getChannelName(slackChannelId);
    log.info("Listening to top-secret slack channel with id: '{}' and name '{}'.", slackChannelId,
        slackChannelName);
    final List<String> channelMessages = this.slackService.getChannelMessages(slackChannelId);
    final String combinedMessage =
        channelMessages.stream().collect(Collectors.joining(System.lineSeparator()));
    final String response = String.format(LISTEN_RESPONSE_FORMAT, combinedMessage);
    this.chatService.sendMessage(response, message);
  }

  @Override
  public CommandInfo getCommandInfo() {
    return CommandInfo.builder().commandKey(SLACK_LISTEN_COMMAND_KEY)
        .commandDescription(SLACK_LISTEN_COMMAND_DESCRIPTION).commandGroup("Slack").build();
  }
}
