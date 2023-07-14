package com.tspasov.artificiumanima.commands.openai;

import java.io.File;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.tspasov.artificiumanima.commands.Command;
import com.tspasov.artificiumanima.commands.discord.MarkdownConstants;
import com.tspasov.artificiumanima.service.AIService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;

@Slf4j
@Component
public class AudioCommand implements Command<Message> {

  private static final String AUDIO_COMMAND_KEY = "!audio";
  private static final String AUDIO_COMMAND_INFO =
      "Ask the Artificial Oracle to convert audio to text";
  private static final String AUDIO_COMMAND_REPLY_FORMAT =
      String.format(MarkdownConstants.BOLD_TEXT_FORMAT,
          "Artificial Oracle :desktop: :brain: heard the following from the voice channel '%s': :loud_sound:")
          + System.lineSeparator() + "%s";

  private final AIService aiService;

  @Autowired
  public AudioCommand(AIService aiService) {
    this.aiService = aiService;
  }

  @Override
  public void execute(String commandStr, Message message) {
    log.info("Creating audio transcription from file: {}", commandStr);
    final GuildVoiceState voiceState = message.getMember().getVoiceState();
    final AudioChannelUnion audioChannel = voiceState == null ? null : voiceState.getChannel();
    
    //TODO: FIXME: Ask the bot to join the audio channel and then transcribe what they hear
    //follow https://medium.com/discord-bots/connecting-your-discord-bot-to-voice-channels-with-java-and-jda-403a9604e816
    
    final String response = StringUtils.isBlank(commandStr) ? "" : this.aiService.transcribeAudio(new File(commandStr));
    message.getChannel().sendMessage(String.format(AUDIO_COMMAND_REPLY_FORMAT, audioChannel != null ? audioChannel.getName() : "N/A", response)).queue();
  }

  @Override
  public String getCommandKey() {
    return AUDIO_COMMAND_KEY;
  }

  @Override
  public String getCommandInfo() {
    return AUDIO_COMMAND_INFO;
  }
}
