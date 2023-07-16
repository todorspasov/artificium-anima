package com.tspasov.artificiumanima.service;

import java.nio.file.Path;

public interface TextToSpeechService {
  Path convertToSpeech(String text);
}
