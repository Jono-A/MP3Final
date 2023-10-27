package com.example.MP3

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import com.example.MP3.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: AuthenticationViewModel
    private var auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = AuthenticationViewModel()
        viewModel.getStates().observe(this@LoginActivity){
            renderUi(it)
        }

        with(binding){
            btnLogin.setOnClickListener {
                viewModel.login(
                   binding.etEmail.text.toString(),
                    binding.etPassword.text.toString()
                )
            }

            tvSignup.apply {
                text = addClickableLink("Don't have an account yet? Sign up", "Sign up"){
                    SignupActivity.launch(this@LoginActivity)
                }
                movementMethod = LinkMovementMethod.getInstance()
            }

        }


    }
    private fun renderUi(states : AuthenticationStates) {
        when(states) {
            is AuthenticationStates.IsLoggedIn -> {
                if(states.isSignedIn) {
                    MainActivity.launch(this@LoginActivity)
                    finish()
                }
            }
            AuthenticationStates.LoggedIn -> {
                MainActivity.launch(this@LoginActivity)
                finish()
            }
            AuthenticationStates.Error -> {}
            else -> {}
        }
    }



    private fun addClickableLink(fullText: String, linkText: String, callback: () -> Unit): SpannableString {
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                callback.invoke()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true
                ds.typeface = Typeface.DEFAULT_BOLD
                ds.color = Color.WHITE
            }

        }
        val startIndex = fullText.indexOf(linkText, 0, true)

        return SpannableString(fullText).apply {
            setSpan(clickableSpan, startIndex, startIndex + linkText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 2) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val exception = task.exception

            try {
                val account = task.getResult(ApiException::class.java)!!
                viewModel.firebaseAuthWithGoogle(account)

            } catch (e: ApiException) {
                //TODO
            }

        }
    }
    override fun onStart() {
        super.onStart()
        viewModel.isLoggedIn()
    }
    companion object {
        fun launch(activity : Activity) = activity.startActivity(Intent(activity, LoginActivity::class.java))
    }
}
