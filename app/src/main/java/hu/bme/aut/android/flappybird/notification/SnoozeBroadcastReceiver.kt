package hu.bme.aut.android.flappybird.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat

class SnoozeBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        with(NotificationManagerCompat.from(context.applicationContext)) {
            cancelAll()
        }
    }
}