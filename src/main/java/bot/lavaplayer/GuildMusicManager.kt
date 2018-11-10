package bot.lavaplayer

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener

class GuildMusicManager(manager: AudioPlayerManager) {
    private val player: AudioPlayer = manager.createPlayer()
    private val provider: AudioProvider = AudioProvider(player)
    private val scheduler: TrackScheduler = TrackScheduler(player)

    fun addAudioListener(listener: AudioEventListener) = player.addListener(listener)
    fun removeAudioListener(listener: AudioEventListener) = player.removeListener(listener)
    fun getScheduler(): TrackScheduler = this.scheduler
    fun getAudioProvider(): AudioProvider = provider
}