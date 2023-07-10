package com.tspasov.artificiumanima.openai;

import java.util.Arrays;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import com.tspasov.artificiumanima.tokens.TokenService;

@Component
public class ChatGptServiceImpl implements ChatGptService {

  private static final String CHAT_GPT_ENGINE = "gpt-3.5-turbo";

  private final OpenAiService openAiService;

  @Autowired
  public ChatGptServiceImpl(TokenService tokenService) {
    this.openAiService = new OpenAiService(tokenService.getGptToken());
  }

  @Override
  public String askQuestion(String question) {
    final ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), question);
    ChatCompletionRequest chatCompletionRequest =
        ChatCompletionRequest.builder().model(CHAT_GPT_ENGINE).messages(Arrays.asList(systemMessage)).n(1).maxTokens(50)
            .logitBias(new HashMap<>()).build();

    final ChatMessage responseMessage = this.openAiService.createChatCompletion(chatCompletionRequest).getChoices()
        .iterator().next().getMessage();

    return responseMessage.getContent();
  }
}
