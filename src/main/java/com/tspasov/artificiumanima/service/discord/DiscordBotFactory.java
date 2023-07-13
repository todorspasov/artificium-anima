package com.tspasov.artificiumanima.service.discord;

import net.dv8tion.jda.api.JDA;

public interface DiscordBotFactory {
  JDA createBot();
}
