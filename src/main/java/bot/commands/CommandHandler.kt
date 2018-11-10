package bot.commands

import bot.DiscordBot
import bot.interfaces.Command
import bot.lavaplayer.GuildMusicManager
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import sx.blah.discord.api.events.EventSubscriber
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.handle.obj.IGuild
import java.util.*
import kotlin.collections.ArrayList


class CommandHandler {
    private val commandMap: MutableMap<String, Command> = HashMap()
    private val musicManagers: MutableMap<Long, GuildMusicManager> = HashMap()
    private val playerManager: AudioPlayerManager = DefaultAudioPlayerManager()

    init {
        AudioSourceManagers.registerRemoteSources(playerManager)
        AudioSourceManagers.registerLocalSource(playerManager)
    }

    fun addCommand(command: String, runCommand: Command) = commandMap.put(command, runCommand)

    fun getGuildAudioPlayer(guild: IGuild): GuildMusicManager {
        val guildId = guild.longID
        var musicManager = musicManagers[guildId]

        if (musicManager == null) {
            musicManager = GuildMusicManager(playerManager)
            musicManagers[guildId] = musicManager
        }

        guild.audioManager.audioProvider = musicManager.getAudioProvider()

        return musicManager
    }

    fun loadAndPlay(channel: IChannel, trackURL: String) {
        val musicManager = getGuildAudioPlayer(channel.guild)

        playerManager.loadItemOrdered(musicManager, trackURL, object : AudioLoadResultHandler {
            override fun loadFailed(exception: FriendlyException?) {
                DiscordBot.sendMessage(channel, "Error playing track: ${exception?.message}")
            }

            override fun trackLoaded(track: AudioTrack?) {
                DiscordBot.sendMessage(channel, "Adding to queue: ${track?.info?.title}")
                play(musicManager, track!!)
            }

            override fun noMatches() {
                DiscordBot.sendMessage(channel, "Nothing found by $trackURL")
            }

            override fun playlistLoaded(playlist: AudioPlaylist?) {
                var firstTrack: AudioTrack? = playlist?.selectedTrack
                if (firstTrack == null) firstTrack = playlist?.tracks?.get(0)

                DiscordBot.sendMessage(channel, "Adding to queue ${firstTrack?.info?.title} (first track of playlist ${playlist?.name})")
                play(musicManager, firstTrack!!)
            }

        })
    }

    private fun play(musicManager: GuildMusicManager, track: AudioTrack) = musicManager.getScheduler().queue(track)

    fun skipTrack(channel: IChannel) {
        val musicManager = getGuildAudioPlayer(channel.guild)
        musicManager.getScheduler().nextTrack()

        DiscordBot.sendMessage(channel, "Skipped to next track.");
    }

    @EventSubscriber
    fun onMessageReceived(event: MessageReceivedEvent) {
        val argArray = event.message.content.split(" ")

        if (argArray.isEmpty()) return
        if (!argArray[0].startsWith(DiscordBot.BOT_PREFIX)) return

        val command: String = argArray[0].substring(DiscordBot.BOT_PREFIX.length)
        val argsList: MutableList<String> = ArrayList(argArray)
        argsList.removeAt(0)

        if (commandMap.containsKey(command))
            commandMap[command]?.runCommand(event, argsList)
    }
}