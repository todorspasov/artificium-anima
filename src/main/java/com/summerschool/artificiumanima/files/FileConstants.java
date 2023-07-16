package com.summerschool.artificiumanima.files;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FileConstants {

  public static final Path AUDIO_FILES_PATH =
      Paths.get("C:\\Users\\User\\Downloads\\discord-audio\\");

  public static final String AUDIO_FILE_NAME_FORMAT = "%s.wav";

  public static final String AUDIO_SPEECH_FILE_NAME = "text-to-speech";

  public static Path getAudioFilePath(String filename) {
    return AUDIO_FILES_PATH.resolve(String.format(AUDIO_FILE_NAME_FORMAT, filename));
  }
}
