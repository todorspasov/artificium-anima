package com.summerschool.artificiumanima.service.stock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import org.springframework.stereotype.Component;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.Interval;

@Component
public class StockApi {

  private static final String STOCK_INFO_FORMAT = "Name: %s\nExchange: %s\nPrice: %s\n";
  
  public String printStockData(String symbol) throws IOException {
    Calendar from = Calendar.getInstance();
    from.add(Calendar.DAY_OF_MONTH, -7);
    Calendar to = Calendar.getInstance();

    Stock stock = YahooFinance.get(symbol);
    stock.print();
    return stock.getHistory(from, to, Interval.DAILY).toString();
  }
  
  public String getStockInfo(String symbol) throws IOException {
    Stock stock = YahooFinance.get(symbol);
    String name = stock.getName();
    String exchange = stock.getStockExchange();
    double price = stock.getQuote().getPrice().doubleValue();
    return String.format(STOCK_INFO_FORMAT, name, exchange, price);
  }
}
