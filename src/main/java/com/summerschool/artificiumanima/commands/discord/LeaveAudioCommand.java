package com.summerschool.artificiumanima.commands.discord;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import com.summerschool.artificiumanima.commands.Command;
import com.summerschool.artificiumanima.service.ChatBotService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;

@Slf4j
@Component
public class LeaveAudioCommand implements Command<Message> {

  private static final String LEAVE_AUDIO_COMMAND_KEY = "!leave-audio";
  private static final String LEAVE_AUDIO_COMMAND_INFO =
      "Ask the Artificial Oracle to leave voice channels";

  private final ChatBotService<AudioChannel, Message> chatService;

  @Autowired
  public LeaveAudioCommand(@Lazy ChatBotService<AudioChannel, Message> chatService) {
    this.chatService = chatService;
  }

  @Override
  public void execute(String commandStr, Message message) {
    log.info("Leaving all voice channels. Args: {}", commandStr);
    this.chatService.leaveAudio(message);
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
