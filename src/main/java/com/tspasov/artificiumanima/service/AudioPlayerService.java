package com.tspasov.artificiumanima.service;

public interface AudioPlayerService<M> {

  void loadAndPlay(M message, String trackUrl);

  void skipTrack(M message);
}
