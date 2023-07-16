package com.summerschool.artificiumanima.service;

import java.io.File;
import java.util.List;

public interface AiService {
  String askQuestion(String question);

  List<String> createImage(String text);

  String transcribeAudio(File file, String language);
}
