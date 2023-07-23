package com.summerschool.artificiumanima.commands.discord.audio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import com.summerschool.artificiumanima.commands.Command;
import com.summerschool.artificiumanima.commands.CommandInfo;
import com.summerschool.artificiumanima.service.AudioPlayerService;
import com.summerschool.artificiumanima.service.ChatBotService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;

@Slf4j
@Component
public class PlayAudioCommand implements Command<Message> {

  private static final String PLAY_AUDIO_COMMAND_KEY = "!play-audio";
  private static final String PLAY_AUDIO_COMMAND_DESCRIPTION =
      "Ask the Artificial Oracle to play a song in a voice channel";

  private final ChatBotService<AudioChannel, Message> chatService;
  private final AudioPlayerService<Message> audioPlayerService;

  @Autowired
  public PlayAudioCommand(@Lazy ChatBotService<AudioChannel, Message> chatService,
      AudioPlayerService<Message> audioPlayerService) {
    this.chatService = chatService;
    this.audioPlayerService = audioPlayerService;
  }

  @Override
  public void execute(String commandStr, Message message) {
    log.info("Joining audio channel to play audio tracks. Args: {}", commandStr);
    final AudioChannel audioChannel = this.chatService.joinAudio(message);
    if (audioChannel != null) {
      this.audioPlayerService.loadAndPlay(message, commandStr);
    }
  }

  @Override
  public CommandInfo getCommandInfo() {
    return CommandInfo.builder().commandKey(PLAY_AUDIO_COMMAND_KEY)
        .commandDescription(PLAY_AUDIO_COMMAND_DESCRIPTION).commandGroup("Audio").build();
  }
}
