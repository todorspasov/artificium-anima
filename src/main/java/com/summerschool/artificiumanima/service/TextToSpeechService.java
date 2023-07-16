package com.summerschool.artificiumanima.service;

import java.nio.file.Path;

public interface TextToSpeechService {
  Path convertToSpeech(String text);
}
