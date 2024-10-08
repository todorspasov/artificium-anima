package com.summerschool.artificiumanima.service.lavaplayer;

import java.util.HashMap;
import java.util.Map;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.summerschool.artificiumanima.service.AudioPlayerService;
import com.summerschool.artificiumanima.service.ChatBotService;
import com.summerschool.artificiumanima.utils.MarkdownConstants;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;

@Slf4j
@Component
public class LavaPlayerService implements AudioPlayerService<Message> {
  private final AudioPlayerManager playerManager;
  private final Map<Long, GuildMusicManager> musicManagers;

  private static final String ADDING_SONG_TO_QUEUE_MESSAGE =
      String.format(MarkdownConstants.BOLD_TEXT_FORMAT, "Adding to queue: :musical_note: ")
          + MarkdownConstants.ITALICS_TEXT_FORMAT;

  private static final String ADDING_PLAYLIST_TO_QUEUE_MESSAGE =
      String.format(MarkdownConstants.BOLD_TEXT_FORMAT, "Adding to queue: :musical_note: ")
          + MarkdownConstants.ITALICS_TEXT_FORMAT + String
              .format(MarkdownConstants.BOLD_TEXT_FORMAT, ". This is the first track of playlist: ")
          + MarkdownConstants.ITALICS_TEXT_FORMAT;

  private static final String NOTHING_FOUND_MESSAGE_FORMAT =
      String.format(MarkdownConstants.BOLD_TEXT_FORMAT, ":warning: Nothing found by %s");

  private static final String COULD_NOT_PLAY_MESSAGE_FORMAT =
      String.format(MarkdownConstants.BOLD_TEXT_FORMAT, ":warning: Could not play %s");

  private static final String SKIPPED_SONG_MESSAGE =
      String.format(MarkdownConstants.BOLD_TEXT_FORMAT, "Skipped to next track :track_next:");

  private final ChatBotService<AudioChannel, Message> chatService;

  public LavaPlayerService(@Lazy ChatBotService<AudioChannel, Message> chatService) {
    this.chatService = chatService;
    this.playerManager = new DefaultAudioPlayerManager();
    AudioSourceManagers.registerRemoteSources(playerManager);
    AudioSourceManagers.registerLocalSource(playerManager);
    this.musicManagers = new HashMap<>();
  }

  @Override
  public void loadAndPlay(final Message message, final String trackUrl) {
    final GuildMusicManager musicManager = getGuildAudioPlayer(message.getGuild());

    playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
      @Override
      public void trackLoaded(AudioTrack track) {
        log.info("Adding to queue {}", track.getInfo().title);
        final String addingSong =
            String.format(ADDING_SONG_TO_QUEUE_MESSAGE, track.getInfo().title);
        chatService.sendMessage(addingSong, message);
        play(musicManager, track);
      }

      @Override
      public void playlistLoaded(AudioPlaylist playlist) {
        AudioTrack firstTrack = playlist.getSelectedTrack();

        if (firstTrack == null) {
          firstTrack = playlist.getTracks().get(0);
        }

        log.info("Adding to queue {} (first track of playlist {})", firstTrack.getInfo().title,
            playlist.getName());
        final String addingPlaylist = String.format(ADDING_PLAYLIST_TO_QUEUE_MESSAGE,
            firstTrack.getInfo().title, playlist.getName());
        chatService.sendMessage(addingPlaylist, message);

        play(musicManager, firstTrack);
      }

      @Override
      public void noMatches() {
        log.warn("Nothing found by {}", trackUrl);
        final String nothingFound = String.format(NOTHING_FOUND_MESSAGE_FORMAT, trackUrl);
        chatService.sendMessage(nothingFound, message);
      }

      @Override
      public void loadFailed(FriendlyException exception) {
        log.error("Could not play.", exception);
        final String cannotPlay =
            String.format(COULD_NOT_PLAY_MESSAGE_FORMAT, exception.getMessage());
        chatService.sendMessage(cannotPlay, message);
      }
    });
  }

  @Override
  public void skipTrack(Message message) {
    GuildMusicManager musicManager = getGuildAudioPlayer(message.getGuild());
    musicManager.scheduler.nextTrack();

    log.info("Skipped to next track.");
    this.chatService.sendMessage(SKIPPED_SONG_MESSAGE, message);
  }

  private void play(GuildMusicManager musicManager, AudioTrack track) {
    musicManager.scheduler.queue(track);
  }

  private synchronized GuildMusicManager getGuildAudioPlayer(Guild guild) {
    long guildId = Long.parseLong(guild.getId());
    musicManagers.computeIfAbsent(guildId, k -> new GuildMusicManager(playerManager));
    final GuildMusicManager musicManager = musicManagers.get(guildId);
    guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

    return musicManager;
  }
}
