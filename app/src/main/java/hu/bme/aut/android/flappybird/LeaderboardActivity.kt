package hu.bme.aut.android.flappybird

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupWindow
import android.widget.Toast
import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import hu.bme.aut.android.flappybird.adapter.LeaderboardAdapter
import hu.bme.aut.android.flappybird.data.Record
import hu.bme.aut.android.flappybird.databinding.ActivityLeaderboardBinding
import hu.bme.aut.android.flappybird.extensions.validateNonEmpty
import kotlinx.android.synthetic.main.popup_changename.view.*

class LeaderboardActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityLeaderboardBinding
    private lateinit var leaderboardAdapter: LeaderboardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeaderboardBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        window.navigationBarColor = Color.parseColor("#ded895")

        binding.navView.setNavigationItemSelectedListener(this)
        leaderboardAdapter = LeaderboardAdapter(applicationContext)
        binding.appBarPosts.contentPosts.rvLeaderboard.layoutManager = LinearLayoutManager(this).apply {
            reverseLayout = true
            stackFromEnd = true
        }
        binding.appBarPosts.contentPosts.rvLeaderboard.adapter = leaderboardAdapter
        binding.appBarPosts.contentPosts.srlLeaderboard.setOnRefreshListener { rankPlayers() }
        initLeaderboardListener()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_logout -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, LoginActivity::class.java)
                finishAffinity()
                startActivity(intent)
                finish()
            }
            R.id.nav_exit -> {
                if(intent.extras?.get("Guest") == false){
                    finish()
                } else {
                    val intent = Intent(this, GameActivity::class.java)
                    intent.putExtra("Guest", false)
                    startActivity(intent)
                    finish()
                }
            }
            R.id.nav_change_nickname -> {
                showPopUpWindow()
            }
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    @SuppressLint("InflateParams")
    private fun showPopUpWindow(){
        val window = PopupWindow(this)
        val view = layoutInflater.inflate(R.layout.popup_changename, null)
        window.contentView = view
        window.isFocusable = true
        window.width = ViewGroup.LayoutParams.MATCH_PARENT
        window.inputMethodMode = PopupWindow.INPUT_METHOD_NEEDED
        window.height = 450

        val saveButton: Button = view.findViewById(R.id.btnSave)
        saveButton.setOnClickListener {
            if(!view.etNickname.validateNonEmpty())
                return@setOnClickListener

            val collection = Firebase.firestore.collection("Records")
            val newName = view.etNickname.text.toString()
            var matched = false
            collection.get()
                    .addOnSuccessListener { Records ->
                        for (dc in Records) {
                            if(dc.get("player") == newName.substringBefore(' ')){
                                matched = true
                                toast("This username is already taken!")
                            }
                        }
                        if(!matched) {
                            changeNickname(newName)
                            window.dismiss()
                        }
                    }
                    .addOnFailureListener { exception ->
                        toast(exception.message)
                    }
        }
        val cancelButton: Button = view.findViewById(R.id.btnCancel)
        cancelButton.setOnClickListener { window.dismiss() }

        window.showAsDropDown(view)
    }

    private fun initLeaderboardListener() {
        val db = Firebase.firestore
        db.collection("Records")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                for (dc in snapshots!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> {
                            leaderboardAdapter.addPost(dc.document.toObject())
                        }
                        DocumentChange.Type.MODIFIED -> return@addSnapshotListener
                        DocumentChange.Type.REMOVED -> return@addSnapshotListener
                    }
                }
                rankPlayers()
            }
    }

    private fun rankPlayers() {
        val collection = Firebase.firestore.collection("Records")
        collection
            .orderBy("score", Query.Direction.DESCENDING).get()
            .addOnSuccessListener { Records ->
                var rank = 0
                var recordAbove: Record? = null
                for (dc in Records) {
                    if(recordAbove == null || recordAbove.score!! > dc.get("score").toString().toInt())
                        rank++
                    val record = Record(dc.get("uid").toString(), dc.get("player").toString(), dc.get("score").toString().toInt(), rank)
                    collection.document(dc.id).set(record)
                    recordAbove = record
                }
                leaderboardAdapter.sort()
            }
            .addOnFailureListener{ exception ->
                toast(exception.message)
            }

        reloadLeaderboard()
    }

    private fun reloadLeaderboard() {
        leaderboardAdapter = LeaderboardAdapter(applicationContext)
        val collection = Firebase.firestore.collection("Records")
        collection.get()
            .addOnSuccessListener { Records ->
                for (dc in Records) {
                    leaderboardAdapter.addPost(dc.toObject())
                }
                binding.appBarPosts.contentPosts.rvLeaderboard.adapter = leaderboardAdapter
                binding.appBarPosts.contentPosts.srlLeaderboard.isRefreshing = false
            }
            .addOnFailureListener { exception ->
                toast(exception.message)
            }
    }

    private fun changeNickname(name: String){
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val profileChangeRequest = UserProfileChangeRequest.Builder()
            .setDisplayName(name)
            .build()
        val collection = Firebase.firestore.collection("Records")
        collection.get()
            .addOnSuccessListener { Records ->
                for (dc in Records) {
                    if (dc.get("uid") == uid) {
                        val newRecord = Record(dc.get("uid").toString(), name, dc.get("score").toString().toInt(), dc.get("rank").toString().toInt())
                        collection.document(dc.id).set(newRecord)
                    }
                }
                firebaseUser?.updateProfile(profileChangeRequest)
            }
            .addOnFailureListener { exception ->
                toast(exception.message)
            }
    }
}