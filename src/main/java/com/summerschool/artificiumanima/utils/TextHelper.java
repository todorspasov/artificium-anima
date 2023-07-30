package com.summerschool.artificiumanima.utils;

import java.util.ArrayList;
import java.util.List;

public class TextHelper {

  private TextHelper() {}

  public static List<String> splitInChunks(final String text, int chunkSize) {
    final List<String> result = new ArrayList<>();
    for (int i = 0; text != null && i < text.length(); i += chunkSize) {
      final String chunk = text.substring(i, Math.min(i + chunkSize, text.length()));
      result.add(chunk);
    }
    return result;
  }
}
