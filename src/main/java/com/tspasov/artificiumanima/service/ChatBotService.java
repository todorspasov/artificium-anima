package com.tspasov.artificiumanima.service;

import java.nio.file.Path;
import java.util.Map;

public interface ChatBotService<C, M> {
  void initBot();

  void shutDownBot();

  C joinAudio(M message);

  void startRecordingAudio(C audioChannel);

  void stopRecordingAudio();

  Map<String, Path> getRecordedAudio();

  void leaveAudio(M message);

  void speak(Path audioPath, C audioChannel);
}
