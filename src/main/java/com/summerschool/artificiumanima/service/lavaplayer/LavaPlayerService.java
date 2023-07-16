package com.summerschool.artificiumanima.service.lavaplayer;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.summerschool.artificiumanima.service.AudioPlayerService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.managers.AudioManager;

@Slf4j
@Component
public class LavaPlayerService implements AudioPlayerService<Message> {
  private final AudioPlayerManager playerManager;
  private final Map<Long, GuildMusicManager> musicManagers;

  public LavaPlayerService() {
    this.playerManager = new DefaultAudioPlayerManager();
    AudioSourceManagers.registerLocalSource(playerManager);
    this.musicManagers = new HashMap<>();
  }

  private synchronized GuildMusicManager getGuildAudioPlayer(Guild guild) {
    long guildId = Long.parseLong(guild.getId());
    if (!musicManagers.containsKey(guildId)) {
      musicManagers.put(guildId, new GuildMusicManager(playerManager));
    }
    final GuildMusicManager musicManager = musicManagers.get(guildId);
    guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

    return musicManager;
  }

  @Override
  public void loadAndPlay(final Message message, final String trackUrl) {
    final GuildMusicManager musicManager = getGuildAudioPlayer(message.getGuild());
    final MessageChannelUnion channel = message.getChannel();

    playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
      @Override
      public void trackLoaded(AudioTrack track) {
        log.info("Adding to queue {}", track.getInfo().title);
        channel.sendMessage("Adding to queue " + track.getInfo().title).queue();

        play(message.getGuild(), musicManager, track);
      }

      @Override
      public void playlistLoaded(AudioPlaylist playlist) {
        AudioTrack firstTrack = playlist.getSelectedTrack();

        if (firstTrack == null) {
          firstTrack = playlist.getTracks().get(0);
        }

        log.info("Adding to queue {} (first track of playlist {})", firstTrack.getInfo().title,
            playlist.getName());
        channel.sendMessage("Adding to queue " + firstTrack.getInfo().title
            + " (first track of playlist " + playlist.getName() + ")").queue();

        play(message.getGuild(), musicManager, firstTrack);
      }

      @Override
      public void noMatches() {
        log.warn("Nothing found by {}", trackUrl);
        channel.sendMessage("Nothing found by " + trackUrl).queue();
      }

      @Override
      public void loadFailed(FriendlyException exception) {
        log.error("Could not play.", exception);
        channel.sendMessage("Could not play: " + exception.getMessage()).queue();
      }
    });
  }

  @Override
  public void skipTrack(Message message) {
    GuildMusicManager musicManager = getGuildAudioPlayer(message.getGuild());
    musicManager.scheduler.nextTrack();

    log.info("Skipped to next track.");
    message.getChannel().sendMessage("Skipped to next track.").queue();
  }

  private void play(Guild guild, GuildMusicManager musicManager, AudioTrack track) {
    connectToFirstVoiceChannel(guild.getAudioManager());

    musicManager.scheduler.queue(track);
  }

  private static void connectToFirstVoiceChannel(AudioManager audioManager) {
    if (!audioManager.isConnected()) {
      for (VoiceChannel voiceChannel : audioManager.getGuild().getVoiceChannels()) {
        audioManager.openAudioConnection(voiceChannel);
        break;
      }
    }
  }
}
