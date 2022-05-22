package hu.bme.aut.android.flappybird

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import hu.bme.aut.android.flappybird.data.Record
import hu.bme.aut.android.flappybird.notification.NotificationHelper
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class GameActivity : BaseActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private var isGuest = true
    private var recordCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val rightNow: Calendar = Calendar.getInstance()
        val currentHour: Int = rightNow.get(Calendar.HOUR_OF_DAY)

        window.statusBarColor = Color.parseColor("#008793")
        window.navigationBarColor = Color.parseColor("#ded895")

        if(currentHour in 7..19) {
            window.statusBarColor = Color.parseColor("#4ec0ca")
        }

        firebaseAuth = FirebaseAuth.getInstance()
        isGuest = intent.extras?.get("Guest") as Boolean
        NotificationHelper.createNotificationChannels(this)

        val gameOver: ImageView = findViewById(R.id.gameover)
            gameOver.visibility = View.INVISIBLE

        val pauseButton: ImageButton = findViewById(R.id.btnPause)
            pauseButton.visibility = View.INVISIBLE
            pauseButton.setOnClickListener{
                if(gameView.getGamestate() == 1) {
                    gameView.pause()
                    pause()
                }
        }

        val resumeButton: ImageButton = findViewById(R.id.btnResume)
            resumeButton.visibility = View.INVISIBLE
            resumeButton.setOnClickListener{
                gameView.resume()
                resume()
        }

        val menuButton: ImageButton = findViewById(R.id.btnMenu)
            menuButton.visibility = View.INVISIBLE
            menuButton.setOnClickListener{
                gameView.toMenu()
                menu()
        }

        val playButton: ImageButton = findViewById(R.id.btnPlay)
            playButton.setOnClickListener {
                gameView.startGame()
                play()
        }

        val leaderboardButton: ImageButton = findViewById(R.id.btnLeaderboard)
            leaderboardButton.setOnClickListener {
                if (isGuest) {
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.putExtra("Guest", true)
                    startActivity(intent)
                    finish()
                    toast("Please log in to view the leaderboard!")
                } else {
                    val intent = Intent(this, LeaderboardActivity::class.java)
                    intent.putExtra("Guest", false)
                    startActivity(intent)
                }
        }

        trackRecords()
    }

    override fun onResume() {
        super.onResume()
        gameView.toMenu()
        menu()
    }

    fun switchView(nextState: Int ){
        coroutine(nextState)
    }

    private fun menu() {
        val pauseButton: ImageButton = findViewById(R.id.btnPause)
            pauseButton.visibility = View.INVISIBLE
        val resumeButton: ImageButton = findViewById(R.id.btnResume)
            resumeButton.visibility = View.INVISIBLE
        val menuButton: ImageButton = findViewById(R.id.btnMenu)
            menuButton.visibility = View.INVISIBLE
        val title: ImageView = findViewById(R.id.title)
            title.visibility = View.VISIBLE
        val gameOver: ImageView = findViewById(R.id.gameover)
            gameOver.visibility = View.INVISIBLE
        val playButton: ImageButton = findViewById(R.id.btnPlay)
            playButton.visibility = View.VISIBLE
        val leaderboardButton: ImageButton = findViewById(R.id.btnLeaderboard)
            leaderboardButton.visibility = View.VISIBLE
    }

    private fun play(){
        val pauseButton: ImageButton = findViewById(R.id.btnPause)
            pauseButton.visibility = View.VISIBLE
        val menuButton: ImageButton = findViewById(R.id.btnMenu)
            menuButton.visibility = View.INVISIBLE
        val title: ImageView = findViewById(R.id.title)
            title.visibility = View.INVISIBLE
        val gameOver: ImageView = findViewById(R.id.gameover)
            gameOver.visibility = View.INVISIBLE
        val playButton: ImageButton = findViewById(R.id.btnPlay)
            playButton.visibility = View.INVISIBLE
        val leaderboardButton: ImageButton = findViewById(R.id.btnLeaderboard)
            leaderboardButton.visibility = View.INVISIBLE
    }

    private fun gameOver(){
        val pauseButton: ImageButton = findViewById(R.id.btnPause)
            pauseButton.visibility = View.VISIBLE
        val resumeButton: ImageButton = findViewById(R.id.btnResume)
            resumeButton.visibility = View.INVISIBLE
        val menuButton: ImageButton = findViewById(R.id.btnMenu)
            menuButton.visibility = View.VISIBLE
        val gameOver: ImageView = findViewById(R.id.gameover)
            gameOver.visibility = View.VISIBLE
        val title: ImageView = findViewById(R.id.title)
            title.visibility = View.INVISIBLE
        val playButton: ImageButton = findViewById(R.id.btnPlay)
            playButton.visibility = View.VISIBLE
        val leaderboardButton: ImageButton = findViewById(R.id.btnLeaderboard)
            leaderboardButton.visibility = View.VISIBLE

        updateScore()
    }

    private fun pause(){
        val pauseButton: ImageButton = findViewById(R.id.btnPause)
            pauseButton.visibility = View.INVISIBLE
        val resumeButton: ImageButton = findViewById(R.id.btnResume)
            resumeButton.visibility = View.VISIBLE
        val menuButton: ImageButton = findViewById(R.id.btnMenu)
            menuButton.visibility = View.VISIBLE
    }

    private fun resume(){
        val pauseButton: ImageButton = findViewById(R.id.btnPause)
            pauseButton.visibility = View.VISIBLE
        val resumeButton: ImageButton = findViewById(R.id.btnResume)
            resumeButton.visibility = View.INVISIBLE
        val menuButton: ImageButton = findViewById(R.id.btnMenu)
            menuButton.visibility = View.INVISIBLE
    }

    private fun coroutine(nextState: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                when(nextState){
                    0 -> menu()
                    1 -> play()
                    2 -> gameOver()
                }
            }
        }
    }

    private fun updateScore() {
        if (isGuest)
            return

        val curScore = gameView.getScore()
        val newRecord = Record(uid, userName, curScore, 0)
        val collection = Firebase.firestore.collection("Records")
        var found = false

        collection.get()
            .addOnSuccessListener { Records ->
                for (dc in Records) {
                    val prevScore = dc.get("score").toString().toInt()
                    if (dc.get("uid") == uid){
                        found = true
                        if (curScore!! > prevScore) {
                            collection.document(dc.id).delete()
                            collection.add(newRecord)
                            return@addOnSuccessListener
                        }
                    }
                }
                if(!found){
                    collection.add(newRecord)
                    recordCount++
                }

            }
            .addOnFailureListener { exception ->
                toast(exception.message)
            }
    }

    private fun trackRecords(){
        val collection = Firebase.firestore.collection("Records")
        collection.get()
            .addOnSuccessListener{ Records ->
                recordCount = Records.size()
                if(!isGuest)
                    initHighscoreListener()
            }
            .addOnFailureListener { exception ->
                toast(exception.message)
            }
    }

    private var oldRecord: Record = Record(uid, userName, -1, 0)
    private fun initHighscoreListener(){
        val db = Firebase.firestore
        db.collection("Records")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    toast(e.toString())
                    return@addSnapshotListener
                }

                for (dc in snapshots!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> {
                            val currentCount = snapshots.documents.size
                            val newRecord: Record = dc.document.toObject()
                            val newScore = newRecord.score!!
                            val oldScore = oldRecord.score!!
                            if(newRecord.uid != uid){
                                for(doc in snapshots.documents){
                                    val curUserScore = doc.get("score").toString().toInt()
                                    if(doc.get("uid") == uid &&
                                            newScore > curUserScore &&
                                            oldScore <= curUserScore &&
                                            recordCount < currentCount){
                                        sendNotification(newRecord)
                                        oldRecord = Record(uid, userName, -1, 0)
                                        recordCount++
                                    }
                                }
                            }
                        }
                        DocumentChange.Type.MODIFIED -> return@addSnapshotListener
                        DocumentChange.Type.REMOVED -> {
                            oldRecord = dc.document.toObject()
                            recordCount--
                        }
                    }
                }

            }
    }

    private fun sendNotification(newRecord: Record?){
        NotificationHelper.createHighscoreNotification(this, newRecord)
    }
}

