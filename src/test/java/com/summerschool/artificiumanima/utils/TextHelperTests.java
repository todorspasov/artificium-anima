package com.summerschool.artificiumanima.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TextHelperTests {

  @BeforeEach
  void setUp() throws Exception {}

  @Test
  void testSplitInChunks() {
    final int maxChunkSize = 2000;
    StringBuilder sb = new StringBuilder();
    char[] firstPart = new char[maxChunkSize];
    for (int i = 0; i < firstPart.length; i++) {
      firstPart[i] = '1';
    }
    sb.append(firstPart);
    char[] secondPart = new char[maxChunkSize / 2];
    for (int i = 0; i < secondPart.length; i++) {
      secondPart[i] = '2';
    }
    sb.append(secondPart);
    sb.append("333");
    final String text = sb.toString();
    final List<String> chunks = TextHelper.splitInChunks(text, maxChunkSize);
    assertNotNull(chunks);
    assertEquals(2, chunks.size());
    assertEquals('1', chunks.get(0).charAt(chunks.get(0).length() - 1));
    assertEquals('2', chunks.get(1).charAt(0));
    assertEquals('3', chunks.get(1).charAt(chunks.get(1).length() - 1));
  }

  @Test
  public void testSplitInChunksCustomSize() {
    final String text = "1112223334445";
    final List<String> chunks = TextHelper.splitInChunks(text, 3);
    assertNotNull(chunks);
    assertEquals(5, chunks.size());
    assertEquals("111", chunks.get(0));
    assertEquals("222", chunks.get(1));
    assertEquals("333", chunks.get(2));
    assertEquals("444", chunks.get(3));
    assertEquals("5", chunks.get(4));
  }
}
