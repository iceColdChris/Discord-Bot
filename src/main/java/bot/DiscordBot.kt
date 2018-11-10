package bot

import sx.blah.discord.api.ClientBuilder
import sx.blah.discord.api.IDiscordClient
import sx.blah.discord.handle.obj.IChannel
import sx.blah.discord.util.DiscordException
import sx.blah.discord.util.RequestBuffer

class DiscordBot {
    companion object {

        var BOT_PREFIX = "!"

        fun createClient(token: String, login: Boolean): IDiscordClient? {
            val clientBuilder = ClientBuilder()
            clientBuilder.withToken(token)
            return try {
                if (login) {
                    clientBuilder.login()
                } else {
                    clientBuilder.build()
                }
            } catch (e: DiscordException) {
                e.printStackTrace()
                null
            }
        }

        fun sendMessage(channel: IChannel, message: String) {
            RequestBuffer.request {
                try {
                    channel.sendMessage(message)
                } catch (e: DiscordException) {
                    e.printStackTrace()
                }
            }
        }
    }
}