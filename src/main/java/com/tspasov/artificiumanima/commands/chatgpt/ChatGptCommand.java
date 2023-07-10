package com.tspasov.artificiumanima.commands.chatgpt;

import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.tspasov.artificiumanima.commands.Command;
import com.tspasov.artificiumanima.openai.ChatGptService;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

@Component
public class ChatGptCommand implements Command<MessageChannel> {

  private static final String CHATGPT_COMMAND_KEY = "!chatgpt";

  private final ChatGptService chatGptService;

  @Autowired
  public ChatGptCommand(ChatGptService chatGptService) {
    this.chatGptService = chatGptService;
  }

  @Override
  public void execute(String commandStr, MessageChannel channel) {
    // ask ChatGPT about the commandStr
    // get response, and then paste it in discord
    final String answer = chatGptService.askQuestion(commandStr);
    channel.sendMessage(String.format("Artificial oracle answered: %s", answer)).queue();
    final List<String> images = chatGptService.createImage(commandStr);
    final String imagesRef = CollectionUtils.emptyIfNull(images).stream().collect(Collectors.joining(", "));
    channel.sendMessage(String.format("GPT created images: %s", imagesRef)).queue();
  }

  @Override
  public String getCommandKey() {
    return CHATGPT_COMMAND_KEY;
  }
}
