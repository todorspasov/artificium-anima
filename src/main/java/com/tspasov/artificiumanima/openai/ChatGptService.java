package com.tspasov.artificiumanima.openai;

import java.util.List;

public interface ChatGptService {
  String askQuestion(String question);
  List<String> createImage(String text);
}
