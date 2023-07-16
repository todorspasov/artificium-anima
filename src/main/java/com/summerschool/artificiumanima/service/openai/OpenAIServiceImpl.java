package com.summerschool.artificiumanima.service.openai;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.summerschool.artificiumanima.service.AIService;
import com.summerschool.artificiumanima.service.tokens.TokenService;
import com.theokanning.openai.audio.CreateTranscriptionRequest;
import com.theokanning.openai.audio.TranscriptionResult;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.image.CreateImageRequest;
import com.theokanning.openai.image.Image;
import com.theokanning.openai.image.ImageResult;
import com.theokanning.openai.service.OpenAiService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class OpenAIServiceImpl implements AIService {

  private static final String GPT_3_5_ENGINE = "gpt-3.5-turbo-16k";
  private static final String TRANSCRIPTION_ENGINE = "whisper-1";
  private static final int MAX_TOKENS = 3 * 4000;
  private static final int CHAT_RESPONSE_COUNT = 1;

  private static final String IMAGE_SIZE = "256x256";
  private static final String IMAGE_FORMAT = "url";
  private static final int IMAGE_RESPONSE_COUNT = 1;

  private final OpenAiService openAiService;
  private final String selectedEngine;

  @Autowired
  public OpenAIServiceImpl(TokenService tokenService,
      @Value("${openai.engine:" + GPT_3_5_ENGINE + "}") String selectedEngine) {
    this.openAiService = new OpenAiService(tokenService.getGptToken());
    this.selectedEngine = selectedEngine;
  }

  @Override
  public String askQuestion(String question) {
    final ChatMessage systemMessage =
        new ChatMessage(ChatMessageRole.SYSTEM.value(), "Act as a history teacher");

    /**
     * 
     * Tasks to finish: - Create a list of messages to keep history, with their roles (gpt answers
     * (assistant), human (user) - Create a !chatgptrole command to set the role (System message act
     * as a XYZ) - Create a command !chatgptpurge to purge the history and start over
     */

    final ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(), question);
    final List<ChatMessage> allMessages = Arrays.asList(systemMessage, userMessage);
    ChatCompletionRequest chatCompletionRequest =
        ChatCompletionRequest.builder().model(this.selectedEngine).messages(allMessages)
            .n(CHAT_RESPONSE_COUNT).maxTokens(MAX_TOKENS).logitBias(new HashMap<>()).build();

    try {
      final String responseMessages = this.openAiService.createChatCompletion(chatCompletionRequest)
          .getChoices().stream().map(ChatCompletionChoice::getMessage).map(ChatMessage::getContent)
          .collect(Collectors.joining());
      return responseMessages;
    } catch (RuntimeException e) {
      final String timeoutMessage = "Could not get an answer, timed out! :snail:";
      log.error(timeoutMessage, e);
      return timeoutMessage;
    }
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
}
