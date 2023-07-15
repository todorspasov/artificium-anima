package com.tspasov.artificiumanima.service.discord;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;

public interface DiscordService {

  void initBot();

  void shutDownBot();
  
  AudioChannel joinAudio(Message message);
  
  void leaveAudio(Message message);

}
