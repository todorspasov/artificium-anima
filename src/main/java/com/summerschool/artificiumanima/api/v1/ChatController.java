package com.summerschool.artificiumanima.api.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.summerschool.artificiumanima.service.AiService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class ChatController {

  private AiService aiService;

  @Autowired
  public ChatController(AiService aiService) {
    this.aiService = aiService;
  }

  @PostMapping("/proxy")
  public String hitGpt(@RequestBody String question) {
    // example question: "Create a piano chord progression in Dminor scale"
    log.info("Asking ChatGPT a question: {}", question);
    final String user = ChatController.class.getSimpleName();
    final String answer = aiService.askQuestion(user, question);
    log.info("Answer from ChatGPT: {}", answer);
    aiService.forget(user);
    return answer;
  }

}
