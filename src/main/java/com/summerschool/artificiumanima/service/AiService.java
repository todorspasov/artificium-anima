package com.summerschool.artificiumanima.service;

import java.io.File;
import java.util.List;

public interface AiService {
  boolean setRole(String user, String roleMessage);

  String askQuestion(String user, String question);

  void forget(String user);

  List<String> createImage(String text);

  String transcribeAudio(File file, String language);
}
