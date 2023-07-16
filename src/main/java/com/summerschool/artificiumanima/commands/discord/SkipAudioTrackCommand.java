package com.summerschool.artificiumanima.commands.discord;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import com.summerschool.artificiumanima.commands.Command;
import com.summerschool.artificiumanima.service.AudioPlayerService;
import com.summerschool.artificiumanima.service.ChatBotService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;

@Slf4j
@Component
public class SkipAudioTrackCommand implements Command<Message> {

  private static final String SKIP_AUDIO_TRACK_COMMAND_KEY = "!skip-audio-track";
  private static final String SKIP_AUDIO_TRACK_COMMAND_INFO =
      "Ask the Artificial Oracle to skip the current audio track (if one is playing)";

  private final ChatBotService<AudioChannel, Message> discordService;
  private final AudioPlayerService<Message> audioPlayerService;

  @Autowired
  public SkipAudioTrackCommand(@Lazy ChatBotService<AudioChannel, Message> discordService,
      AudioPlayerService<Message> audioPlayerService) {
    this.discordService = discordService;
    this.audioPlayerService = audioPlayerService;
  }

  @Override
  public void execute(String commandStr, Message message) {
    log.info("Skipping the currently playing audio track. Args: {}", commandStr);
    final AudioChannel audioChannel = this.discordService.joinAudio(message);
    if (audioChannel != null) {
      this.audioPlayerService.skipTrack(message);
    }
  }

  @Override
  public String getCommandKey() {
    return SKIP_AUDIO_TRACK_COMMAND_KEY;
  }

  @Override
  public String getCommandInfo() {
    return SKIP_AUDIO_TRACK_COMMAND_INFO;
  }
}
