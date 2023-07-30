package com.summerschool.artificiumanima.service.gpt4all;

import java.io.File;
import java.util.Collections;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import com.summerschool.artificiumanima.service.AbstractAiService;

@Component
@ConditionalOnProperty(name = "ai.service.connection", havingValue = "local")
public class Gpt4AllServiceImpl extends AbstractAiService<String> {
  
  /**
   * TODO: FIXME: Tasks to finish:
   *  - auto-download openAI openAPI spec file
   *  - auto-generate Java SDK binding
   *  - consume the sdk by pointing it to localhost:4891
   */
  
  
  @Override
  public String askQuestion(String user, String question) {
    return null;
  }

  @Override
  public List<String> createImage(String text) {
    return Collections.emptyList();
  }

  @Override
  public String transcribeAudio(File file, String language) {
    return null;
  }

  @Override
  protected String getRoleMessage(String roleMessage) {
    return null;
  }

}
