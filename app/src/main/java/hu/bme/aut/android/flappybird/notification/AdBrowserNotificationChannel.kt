package hu.bme.aut.android.flappybird.notification

enum class AdBrowserNotificationChannel(
    val id: String,
    val channelName: String,
    val channelDescription: String
) {
    HIGHSCORE_BEATEN("hu.bme.aut.android.flappybird.highscore", "Highscore", "Someone passed you on the leaderboard"),
    PROMO("hu.bme.aut.android.flappybird.promo", "Promotion", "You haven't played in a while")
}