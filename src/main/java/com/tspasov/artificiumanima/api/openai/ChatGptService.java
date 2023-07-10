package com.tspasov.artificiumanima.api.openai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Component;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;

@Component
public class ChatGptService {

  public String askQuestion(String question) {
    OpenAiService service = new OpenAiService("<my-token-here>");
    final List<ChatMessage> messages = new ArrayList<>();
    final ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), question);
    messages.add(systemMessage);
    ChatCompletionRequest chatCompletionRequest =
        ChatCompletionRequest.builder().model("gpt-3.5-turbo").messages(messages).n(1).maxTokens(50)
            .logitBias(new HashMap<>()).build();

    // service.streamChatCompletion(chatCompletionRequest).doOnError(Throwable::printStackTrace)
    // .blockingForEach(System.out::println);

    ChatMessage responseMessage = service.createChatCompletion(chatCompletionRequest).getChoices()
        .iterator().next().getMessage();

    service.shutdownExecutor();
    return responseMessage.getContent();
  }
}
