package com.summerschool.artificiumanima.commands.discord.stock;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import com.summerschool.artificiumanima.commands.Command;
import com.summerschool.artificiumanima.commands.CommandInfo;
import com.summerschool.artificiumanima.service.ChatBotService;
import com.summerschool.artificiumanima.service.stock.StockApi;
import com.summerschool.artificiumanima.utils.MarkdownConstants;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;

@Slf4j
@Component
public class StockHistoryCommand implements Command<Message> {

  private static final String STOCK_HISTORY_COMMAND_KEY = "!stock-history";
  private static final String STOCK_HISTORY_COMMAND_DESCRIPTION =
      "See stock market history data about some instrument. Example: '!stock-history NVDA'";

  private static final String STOCK_HISTORY_COMMAND_REPLY_FORMAT =
      String.format(MarkdownConstants.BOLD_TEXT_FORMAT,
          "Stock History Data :moneybag: :\n") + "%s";

  private final StockApi stockApi;

  private final ChatBotService<AudioChannel, Message> chatService;

  @Autowired
  public StockHistoryCommand(StockApi stockApi, @Lazy ChatBotService<AudioChannel, Message> chatService) {
    this.stockApi = stockApi;
    this.chatService = chatService;
  }

  @Override
  public void execute(String commandStr, Message message) {
    log.info(
        "Querying stock market history data about instrument: {}", commandStr);
    String answer;
    try {
      answer = this.stockApi.printStockData(commandStr);
      log.info("Stock response: {}", answer);
      
    } catch (IOException e) {
      log.error("Could not obtain stock data", e);
      answer = "Error while obtaining stock data";
    }
    final String chatMessage = String.format(STOCK_HISTORY_COMMAND_REPLY_FORMAT, answer);
    this.chatService.sendMessage(chatMessage, message);
  }

  @Override
  public CommandInfo getCommandInfo() {
    return CommandInfo.builder().commandKey(STOCK_HISTORY_COMMAND_KEY)
        .commandDescription(STOCK_HISTORY_COMMAND_DESCRIPTION).commandGroup("Stocks").build();
  }
}
