package com.tspasov.artificiumanima.api.discord;

import net.dv8tion.jda.api.JDA;

public interface DiscordBotFactory {
  JDA createBot();
}
