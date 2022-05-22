package hu.bme.aut.android.flappybird.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import hu.bme.aut.android.flappybird.LeaderboardActivity
import hu.bme.aut.android.flappybird.LoginActivity
import hu.bme.aut.android.flappybird.R
import hu.bme.aut.android.flappybird.data.Record
import kotlin.random.Random

class NotificationHelper {
    companion object {
        private const val ACTION_SHOW_AD = "hu.bme.aut.android.flappybird.showLeaderboard"

        fun createNotificationChannels(ctx: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                AdBrowserNotificationChannel.values().forEach {
                    val name = it.channelName
                    val descriptionText = it.channelDescription
                    val importance = NotificationManager.IMPORTANCE_DEFAULT
                    val channel = NotificationChannel(it.id, name, importance).apply {
                        description = descriptionText
                    }
                    with(NotificationManagerCompat.from(ctx)) {
                        createNotificationChannel(channel)
                    }
                }

            }
        }

        fun createHighscoreNotification(ctx: Context, newRecord: Record?) {
            val intent = Intent(ctx, LeaderboardActivity::class.java).apply {
                action = ACTION_SHOW_AD
            }
            val pendingIntent: PendingIntent =
                PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

            val snoozeIntent = Intent(ctx, SnoozeBroadcastReceiver::class.java)
            val snoozePendingIntent: PendingIntent =
                PendingIntent.getBroadcast(ctx, 0, snoozeIntent, 0)

            val builder =
                NotificationCompat.Builder(ctx, AdBrowserNotificationChannel.HIGHSCORE_BEATEN.id)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle("Someone has passed you on the leaderboard!")
                    .setContentText(newRecord?.player + " has beaten your record in Flappy Bird!")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .addAction(
                        0,
                        "Ignore",
                        snoozePendingIntent
                    )
                    .setAutoCancel(true)

            with(NotificationManagerCompat.from(ctx)) {
                notify(Random.Default.nextInt(10000, 100000), builder.build())
            }
        }

        fun createPendingIntentForPromotionNotification(ctx: Context): PendingIntent =
            PendingIntent.getBroadcast(ctx, 0, Intent(ctx, PromoBroadcastReceiver::class.java), 0)

        fun createPromoNotification(ctx: Context) {
            val intent = Intent(ctx, LoginActivity::class.java)
            val pendingIntent: PendingIntent =
                PendingIntent.getActivity(ctx, 0, intent, 0)

            val builder =
                NotificationCompat.Builder(ctx, AdBrowserNotificationChannel.PROMO.id)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle("We haven't seen you in a while...")
                    .setContentText("Play Now! You don't want your friends catching up...")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)

            with(NotificationManagerCompat.from(ctx)) {
                notify(Random.Default.nextInt(10000, 100000), builder.build())
            }
        }
    }
}