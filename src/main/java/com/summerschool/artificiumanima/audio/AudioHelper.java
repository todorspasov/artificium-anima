package com.summerschool.artificiumanima.audio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AudioHelper {

  public void writeStreamToFile(ByteArrayOutputStream byteOutputStream, AudioFormat audioFormat,
      File file) {
    final byte[] byteArray = byteOutputStream.toByteArray();
    try (AudioInputStream is =
        new AudioInputStream(new ByteArrayInputStream(byteArray), audioFormat, byteArray.length)) {
      writeStreamToFile(is, file);
    } catch (IOException ex) {
      log.error("Could not create audio file {}. Exception: {}", file.getName(), ex);
    }
  }

  public void writeStreamToFile(AudioInputStream audioInputStream, File file) {
    try {
      AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, file);
    } catch (IOException ex) {
      log.error("Could not create audio file {}. Exception: {}", file.getName(), ex);
    }
  }

}
