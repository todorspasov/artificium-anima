package com.tspasov.artificiumanima.service.discord.handlers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import org.springframework.stereotype.Component;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.UserAudio;

@Slf4j
@Component
public class DiscordAudioHandler implements AudioReceiveHandler {

  public static final Path AUDIO_FILES_PATH =
      Paths.get("C:\\Users\\User\\Downloads\\discord-audio\\");
  private static final String AUDIO_FILE_NAME_FORMAT = "%s.wav";

  private static final int THRESHOLD_RECEIVE_SIZE = 100 * 1024 * 1024;

  private final Map<String, ByteArrayOutputStream> userByteStreams = new HashMap<>();

  private boolean isRecording;

  private @Getter Map<String, Path> recordedUserFiles;

  public DiscordAudioHandler() {
    this.recordedUserFiles = new HashMap<>();
    this.isRecording = false;
  }

  @Override
  public boolean canReceiveUser() {
    return this.isRecording && (userByteStreams.isEmpty()
        || (userByteStreams.values().stream().map(ByteArrayOutputStream::size).max(Integer::compare)
            .orElse(0) < THRESHOLD_RECEIVE_SIZE));
  }

  @Override
  public void handleUserAudio(UserAudio userAudio) {
    final String speakerName = userAudio.getUser().getName();
    final byte[] audioData = userAudio.getAudioData(1.0);
    userByteStreams.putIfAbsent(speakerName, new ByteArrayOutputStream());
    final ByteArrayOutputStream speakerByteStream = userByteStreams.get(speakerName);
    try {
      log.info("Adding audio data (size {}B) to stream for user: {}", audioData.length,
          speakerName);
      speakerByteStream.write(audioData);
    } catch (IOException e) {
      log.error("Could not save byte audio data to stream", e);
    }
    if (speakerByteStream.size() >= THRESHOLD_RECEIVE_SIZE) {
      persistUserRecording(speakerName, speakerByteStream);
    }
  }

  private void persistUserRecording(final String speakerName,
      final ByteArrayOutputStream speakerByteStream) {
    final File speakerFile =
        AUDIO_FILES_PATH.resolve(String.format(AUDIO_FILE_NAME_FORMAT, speakerName)).toFile();
    log.info("Writing audio file {} for user {}", speakerFile.getName(), speakerName);
    writeStreamToFile(speakerByteStream, speakerFile);
    this.recordedUserFiles.put(speakerName, speakerFile.toPath());
    speakerByteStream.reset();
  }

  public void startRecording() {
    this.isRecording = true;
  }

  public void stopRecording() {
    if (this.isRecording) {
      this.isRecording = false;
      flushUserStreams();
    }
  }

  private void flushUserStreams() {
    // flush any leftover streams that have not reached the limits
    this.userByteStreams.entrySet().stream()
        .forEach(entry -> persistUserRecording(entry.getKey(), entry.getValue()));
  }

  public void clearUserFiles() {
    this.recordedUserFiles.values().forEach(userPath -> {
      try {
        log.error("Removing user file {}", userPath.getFileName());
        Files.deleteIfExists(userPath);
      } catch (IOException e) {
        log.error("Could not clear up user file {}", userPath.getFileName());
      }
    });
    this.recordedUserFiles.clear();
  }

  private static void writeStreamToFile(ByteArrayOutputStream byteOutputStream, File file) {
    final byte[] byteArray = byteOutputStream.toByteArray();
    try (AudioInputStream is = new AudioInputStream(new ByteArrayInputStream(byteArray),
        OUTPUT_FORMAT, byteArray.length)) {
      AudioSystem.write(is, AudioFileFormat.Type.WAVE, file);
    } catch (IOException ex) {
      log.error("Could not create audio file {}. Exception: {}", file.getName(), ex);
    }
  }
}
