package com.summerschool.artificiumanima.service.discord;

import net.dv8tion.jda.api.JDA;

public interface DiscordBotFactory {
  JDA createBot();
}
