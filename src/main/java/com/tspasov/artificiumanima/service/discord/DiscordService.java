package com.tspasov.artificiumanima.service.discord;

import java.nio.file.Path;
import java.util.Map;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;

public interface DiscordService {

  void initBot();

  void shutDownBot();

  AudioChannel joinAudio(Message message);

  void startRecordingAudio(AudioChannel audioChannel);

  void stopRecordingAudio();

  Map<String, Path> getRecordedAudio();

  void leaveAudio(Message message);

}
