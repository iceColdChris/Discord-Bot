import bot.DiscordBot

fun main(args: Array<String>) {
    val client = DiscordBot.createClient(args[0], true)
    val dispatcher = client?.dispatcher
}