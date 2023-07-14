package com.tspasov.artificiumanima.commands.openai;

import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.tspasov.artificiumanima.commands.Command;
import com.tspasov.artificiumanima.commands.discord.MarkdownConstants;
import com.tspasov.artificiumanima.service.AIService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

@Slf4j
@Component
public class ImageCommand implements Command<MessageChannel> {

  private static final String IMAGE_COMMAND_KEY = "!image";
  private static final String IMAGE_COMMAND_INFO =
      "Ask the Artificial Oracle to draw an image based on text";
  private static final String IMAGE_COMMAND_REPLY_FORMAT =
      String.format(MarkdownConstants.BOLD_TEXT_FORMAT, "Artificial Oracle :desktop: :brain: created image(s): :frame_photo:") + System.lineSeparator()
          + "%s";

  private final AIService chatGptService;

  @Autowired
  public ImageCommand(AIService chatGptService) {
    this.chatGptService = chatGptService;
  }

  @Override
  public void execute(String commandStr, MessageChannel channel) {
    final String imageStr = StringUtils.substring(commandStr, 0, 1000);
    log.info("Creating image from prompt: {}", imageStr);
    final List<String> images = chatGptService.createImage(imageStr);
    final String imagesRef =
        CollectionUtils.emptyIfNull(images).stream().collect(Collectors.joining(", "));
    channel.sendMessage(String.format(IMAGE_COMMAND_REPLY_FORMAT, imagesRef)).queue();
  }

  @Override
  public String getCommandKey() {
    return IMAGE_COMMAND_KEY;
  }

  @Override
  public String getCommandInfo() {
    return IMAGE_COMMAND_INFO;
  }
}
