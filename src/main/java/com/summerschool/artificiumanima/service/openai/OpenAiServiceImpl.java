package com.summerschool.artificiumanima.service.openai;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.summerschool.artificiumanima.service.AiService;
import com.summerschool.artificiumanima.service.tokens.TokenService;
import com.theokanning.openai.audio.CreateTranscriptionRequest;
import com.theokanning.openai.audio.TranscriptionResult;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.image.CreateImageRequest;
import com.theokanning.openai.image.Image;
import com.theokanning.openai.image.ImageResult;
import com.theokanning.openai.service.OpenAiService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class OpenAiServiceImpl implements AiService {

  private static final String GPT_3_5_ENGINE = "gpt-3.5-turbo-16k";
  private static final String TRANSCRIPTION_ENGINE = "whisper-1";
  private static final int MAX_TOKENS = 3 * 4000;
  private static final int CHAT_RESPONSE_COUNT = 1;

  private static final String IMAGE_SIZE = "256x256";
  private static final String IMAGE_FORMAT = "url";
  private static final int IMAGE_RESPONSE_COUNT = 1;

  private final OpenAiService openAiService;
  private final String selectedEngine;

  private final Map<String, List<ChatMessage>> history;

  @Autowired
  public OpenAiServiceImpl(TokenService tokenService,
      @Value("${openai.engine:" + GPT_3_5_ENGINE + "}") String selectedEngine) {
    this.history = new HashMap<>();
    this.openAiService = new OpenAiService(tokenService.getOpenAiToken());
    this.selectedEngine = selectedEngine;
  }

  @Override
  public boolean setRole(String user, String roleMessage) {
    final ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), roleMessage);
    addMessage(user, systemMessage);
    return true;
  }

  @Override
  public String askQuestion(String user, String question) {

    final ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(), question);
    addMessage(user, userMessage);
    final List<ChatMessage> allMessages = getMessages(user);
    ChatCompletionRequest chatCompletionRequest =
        ChatCompletionRequest.builder().model(this.selectedEngine).messages(allMessages)
            .n(CHAT_RESPONSE_COUNT).maxTokens(MAX_TOKENS).logitBias(new HashMap<>()).build();

    try {
      final ChatCompletionResult chatCompletionResult =
          this.openAiService.createChatCompletion(chatCompletionRequest);
      final List<ChatMessage> responseChatMessages =
          CollectionUtils.emptyIfNull(chatCompletionResult.getChoices()).stream()
              .map(ChatCompletionChoice::getMessage).collect(Collectors.toList());
      addMessages(user, responseChatMessages);

      return responseChatMessages.stream().map(ChatMessage::getContent)
          .collect(Collectors.joining());
    } catch (RuntimeException e) {
      final String timeoutMessage = "Could not get an answer, timed out! :snail:";
      log.error(timeoutMessage, e);
      return timeoutMessage;
    }
  }

  @Override
  public void forget(String user) {
    history.remove(user);
  }

  @Override
  public List<String> createImage(String text) {
    CreateImageRequest imageRequest = CreateImageRequest.builder().prompt(text)
        .responseFormat(IMAGE_FORMAT).size(IMAGE_SIZE).n(IMAGE_RESPONSE_COUNT).build();
    final ImageResult imageResult = this.openAiService.createImage(imageRequest);
    log.info("Image result: {}", imageResult.toString());
    return imageResult.getData().stream().map(Image::getUrl).collect(Collectors.toList());
  }

  @Override
  public String transcribeAudio(File file, String language) {
    CreateTranscriptionRequest transcriptionRequest =
        CreateTranscriptionRequest.builder().language(language).model(TRANSCRIPTION_ENGINE).build();
    final TranscriptionResult transcription =
        this.openAiService.createTranscription(transcriptionRequest, file);
    log.info("Got the following answer: {}", transcription.getText());
    return transcription.getText();
  }

  private List<ChatMessage> getMessages(String user) {
    if (!history.containsKey(user)) {
      history.put(user, new ArrayList<>());
    }
    return history.get(user);
  }

  private void addMessage(String user, ChatMessage chatMessage) {
    if (!history.containsKey(user)) {
      history.put(user, new ArrayList<>());
    }
    history.get(user).add(chatMessage);
  }

  private void addMessages(String user, List<ChatMessage> chatMessages) {
    CollectionUtils.emptyIfNull(chatMessages).forEach(cm -> this.addMessage(user, cm));
  }
}
