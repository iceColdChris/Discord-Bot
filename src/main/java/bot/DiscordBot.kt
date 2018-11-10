package bot

import sx.blah.discord.api.ClientBuilder
import sx.blah.discord.api.IDiscordClient
import sx.blah.discord.util.DiscordException

class DiscordBot {
    companion object {
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
    }
}