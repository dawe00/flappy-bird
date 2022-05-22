package hu.bme.aut.android.flappybird.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context

class AlarmHelper {
    companion object {
        fun scheduleAlarm(ctx: Context, delaySeconds : Long, pendingIntent: PendingIntent) {
            val triggerDateTime = System.currentTimeMillis() + delaySeconds * 1000
            val alarmManager =
                ctx.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            alarmManager?.set(AlarmManager.RTC_WAKEUP, triggerDateTime, pendingIntent)
        }
    }
}