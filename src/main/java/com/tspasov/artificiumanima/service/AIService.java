package com.tspasov.artificiumanima.service;

import java.io.File;
import java.util.List;

public interface AIService {
  String askQuestion(String question);

  List<String> createImage(String text);

  String transcribeAudio(File file);
}
