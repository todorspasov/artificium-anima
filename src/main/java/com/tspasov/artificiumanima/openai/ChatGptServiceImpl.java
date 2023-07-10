package com.tspasov.artificiumanima.openai;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.image.CreateImageRequest;
import com.theokanning.openai.image.Image;
import com.theokanning.openai.image.ImageResult;
import com.theokanning.openai.service.OpenAiService;
import com.tspasov.artificiumanima.tokens.TokenService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ChatGptServiceImpl implements ChatGptService {

  private static final String CHAT_GPT_ENGINE = "gpt-3.5-turbo";

  private static final String IMAGE_SIZE = "256x256";
  private static final String IMAGE_FORMAT = "url";

  private final OpenAiService openAiService;

  @Autowired
  public ChatGptServiceImpl(TokenService tokenService) {
    this.openAiService = new OpenAiService(tokenService.getGptToken());
  }

  @Override
  public String askQuestion(String question) {
    final ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), question);
    ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
        .model(CHAT_GPT_ENGINE).messages(Arrays.asList(systemMessage)).n(1).maxTokens(1000)
        .logitBias(new HashMap<>()).build();

    try {
      final ChatMessage responseMessage = this.openAiService
          .createChatCompletion(chatCompletionRequest).getChoices().iterator().next().getMessage();
      return responseMessage.getContent();
    } catch (RuntimeException e) {
      final String timeoutMessage = "Could not get an answer, timed out!";
      log.error(timeoutMessage, e);
      return timeoutMessage;
    }
  }

  @Override
  public List<String> createImage(String text) {
    CreateImageRequest imageRequest = CreateImageRequest.builder()
        .prompt(text)
        .responseFormat(IMAGE_FORMAT)
        .size(IMAGE_SIZE)
        .n(1)
        .build();
    final ImageResult imageResult = this.openAiService.createImage(imageRequest);
    log.info("Image result: {}", imageResult.toString());
    return imageResult.getData().stream().map(Image::getUrl).collect(Collectors.toList());
  }
}
