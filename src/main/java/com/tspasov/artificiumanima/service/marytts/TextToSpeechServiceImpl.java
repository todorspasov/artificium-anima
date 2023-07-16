package com.tspasov.artificiumanima.service.marytts;

import java.nio.file.Path;
import javax.sound.sampled.AudioInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.tspasov.artificiumanima.audio.AudioHelper;
import com.tspasov.artificiumanima.files.FileConstants;
import com.tspasov.artificiumanima.service.TextToSpeechService;
import lombok.extern.slf4j.Slf4j;
import marytts.LocalMaryInterface;
import marytts.exceptions.MaryConfigurationException;
import marytts.exceptions.SynthesisException;

@Slf4j
@Component
public class TextToSpeechServiceImpl implements TextToSpeechService {
  public static final String VOICE_NAME = "cmu-slt-hsmm";

  private final AudioHelper audioHelper;

  @Autowired
  public TextToSpeechServiceImpl(AudioHelper audioHelper) {
    this.audioHelper = audioHelper;
  }

  @Override
  public Path convertToSpeech(String text) {
    final Path outputFilePath =
        FileConstants.getAudioFilePath(FileConstants.AUDIO_SPEECH_FILE_NAME);

    try {
      LocalMaryInterface mary = new LocalMaryInterface();
      mary.setVoice(VOICE_NAME);
      AudioInputStream audio = mary.generateAudio(text);
      this.audioHelper.writeStreamToFile(audio, outputFilePath.toFile());
      log.info("Output written to {}", outputFilePath.toString());
      return outputFilePath;
    } catch (MaryConfigurationException | SynthesisException e) {
      log.error("Could not initialize MaryTTS interface or synthesize audio.", e);
    }
    return null;
  }
}
