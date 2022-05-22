package hu.bme.aut.android.flappybird.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class PromoBroadcastReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        NotificationHelper.createPromoNotification(context.applicationContext)
    }
}