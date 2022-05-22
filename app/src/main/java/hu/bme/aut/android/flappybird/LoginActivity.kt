package hu.bme.aut.android.flappybird

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import hu.bme.aut.android.flappybird.databinding.ActivityLoginBinding
import hu.bme.aut.android.flappybird.extensions.validateNonEmpty

class LoginActivity : BaseActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnRegister.setOnClickListener { registerClick() }
        binding.btnLogin.setOnClickListener { loginClick() }
        binding.btnContinueAsGuest.setOnClickListener { playAsGuestClick() }

        window.navigationBarColor = Color.parseColor("#ded895")
    }

    private fun validateForm() = binding.etEmail.validateNonEmpty() && binding.etPassword.validateNonEmpty()

    private fun registerClick() {
        if (!validateForm()) {
            return
        }

        showProgressDialog()

        firebaseAuth
            .createUserWithEmailAndPassword(binding.etEmail.text.toString(), binding.etPassword.text.toString())
            .addOnSuccessListener { result ->
                hideProgressDialog()

                val firebaseUser = result.user
                val collection = Firebase.firestore.collection("Records")
                var nickname = firebaseUser?.email?.substringBefore('@')
                var nameOffset = 1
                collection.get()
                    .addOnSuccessListener { Records ->
                    for (dc in Records) {
                        if(dc.get("player") == nickname){
                            nickname = firebaseUser?.email?.substringBefore('@') + "(" + nameOffset +")"
                            nameOffset++
                        }
                    }

                    val profileChangeRequest = UserProfileChangeRequest.Builder()
                        .setDisplayName(nickname)
                        .build()
                    firebaseUser?.updateProfile(profileChangeRequest)
                    firebaseUser!!.sendEmailVerification()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                toast("Please check your mailbox for e-mail verification!")
                            }
                        }
                }
            }
            .addOnFailureListener { exception ->
                hideProgressDialog()
                toast(exception.message)
            }
    }

    private fun loginClick() {
        if (!validateForm()) {
            return
        }

        showProgressDialog()

        firebaseAuth
            .signInWithEmailAndPassword(binding.etEmail.text.toString(), binding.etPassword.text.toString())
            .addOnSuccessListener { result ->
                hideProgressDialog()

                val firebaseUser = result.user
                if(!firebaseUser!!.isEmailVerified) {
                    FirebaseAuth.getInstance().signOut()
                    toast("Please verify your e-mail address before logging in!")
                    return@addOnSuccessListener
                }
                if(intent.extras?.get("Guest") == true){
                    val intent = Intent(this, LeaderboardActivity::class.java)
                    intent.putExtra("Guest", true)
                    startActivity(intent)
                    finish()
                } else {
                    val intent = Intent(this, GameActivity::class.java)
                    intent.putExtra("Guest", false)
                    startActivity(intent)
                    finish()
                }
            }
            .addOnFailureListener { exception ->
                hideProgressDialog()
                toast(exception.localizedMessage)
            }
    }

    private fun playAsGuestClick(){
        val intent = Intent(this, GameActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("Guest", true)
        startActivity(intent)
        finish()
    }
}