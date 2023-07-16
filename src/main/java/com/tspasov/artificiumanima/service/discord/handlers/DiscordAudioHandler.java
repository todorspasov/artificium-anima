package com.tspasov.artificiumanima.service.discord.handlers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.UserAudio;

@Slf4j
public class DiscordAudioHandler implements AudioReceiveHandler {

  public static final String AUDIO_FILE_PATH = "C:\\Users\\User\\Downloads\\sample-recording.mp3";

  private static final int THRESHOLD_RECEIVE_SIZE = 1 * 1024 * 1024;

  private final Map<String, ByteArrayOutputStream> queues = new HashMap<>();

  @Override
  public boolean canReceiveUser() {
    return queues.isEmpty() || (queues.values().stream().map(ByteArrayOutputStream::size)
        .max(Integer::compare).orElse(0) < THRESHOLD_RECEIVE_SIZE);
  }

  @Override
  public void handleUserAudio(UserAudio userAudio) {
    final String speakerName = userAudio.getUser().getName();
    final byte[] audioData = userAudio.getAudioData(1.0);
    queues.putIfAbsent(speakerName, new ByteArrayOutputStream());
    final ByteArrayOutputStream speakerByteStream = queues.get(speakerName);
    try {
      log.info("Adding audio data (size {}B) to stream for user: {}", audioData.length,
          speakerName);
      speakerByteStream.write(audioData);
    } catch (IOException e) {
      log.error("Could not save byte audio data to stream", e);
    }
    if (speakerByteStream.size() >= THRESHOLD_RECEIVE_SIZE) {
      final File file = new File(AUDIO_FILE_PATH);
      log.info("Writing audio file {} for user {}", file.getName(), speakerName);
      writeQueueToFile(speakerByteStream, file);
    }
  }

  private void writeQueueToFile(ByteArrayOutputStream byteOutputSteram, File file) {
    final byte[] byteArray = byteOutputSteram.toByteArray();
    try (AudioInputStream is = new AudioInputStream(new ByteArrayInputStream(byteArray),
        OUTPUT_FORMAT, byteArray.length)) {
      AudioSystem.write(is, AudioFileFormat.Type.WAVE, file);
      // poll everything into a file.
      byteOutputSteram.reset();
    } catch (IOException ex) {
      log.error("Could not create audio file {}. Exception: {}", file.getName(), ex);
    }
  }
}
