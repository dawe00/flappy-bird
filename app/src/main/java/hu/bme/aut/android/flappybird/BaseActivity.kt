package hu.bme.aut.android.flappybird

import android.app.ProgressDialog
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import hu.bme.aut.android.flappybird.alarm.AlarmHelper
import hu.bme.aut.android.flappybird.notification.NotificationHelper

abstract class BaseActivity : AppCompatActivity() {

    private var progressDialog: ProgressDialog? = null

    private val firebaseUser: FirebaseUser?
        get() = FirebaseAuth.getInstance().currentUser

    protected val uid: String?
        get() = firebaseUser?.uid

    protected val userName: String?
        get() = firebaseUser?.displayName

    override fun onDestroy() {
        super.onDestroy()
        val pendingIntent = NotificationHelper.createPendingIntentForPromotionNotification(this)
        AlarmHelper.scheduleAlarm(this, 86400, pendingIntent)
    }

    fun showProgressDialog() {
        if (progressDialog != null) {
            return
        }

        progressDialog = ProgressDialog(this).apply {
            setCancelable(false)
            setMessage("Loading...")
            show()
        }
    }

    protected fun hideProgressDialog() {
        progressDialog?.let { dialog ->
            if (dialog.isShowing) {
                dialog.dismiss()
            }
        }
        progressDialog = null
    }

    protected fun toast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}