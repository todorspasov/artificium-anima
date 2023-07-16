package com.tspasov.artificiumanima.commands.discord;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import com.tspasov.artificiumanima.commands.Command;
import com.tspasov.artificiumanima.service.ChatBotService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;

@Slf4j
@Component
public class LeaveAudioCommand implements Command<Message> {

  private static final String LEAVE_AUDIO_COMMAND_KEY = "!leave";
  private static final String LEAVE_AUDIO_COMMAND_INFO =
      "Ask the Artificial Oracle to leave voice channels";

  private final ChatBotService<AudioChannel, Message> discordService;

  @Autowired
  public LeaveAudioCommand(@Lazy ChatBotService<AudioChannel, Message> discordService) {
    this.discordService = discordService;
  }

  @Override
  public void execute(String commandStr, Message message) {
    log.info("Leaving all voice channels. Args: {}", commandStr);
    this.discordService.leaveAudio(message);
  }

  @Override
  public String getCommandKey() {
    return LEAVE_AUDIO_COMMAND_KEY;
  }

  @Override
  public String getCommandInfo() {
    return LEAVE_AUDIO_COMMAND_INFO;
  }
}
