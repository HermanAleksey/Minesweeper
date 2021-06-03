package com.example.sapper.view.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.sapper.databinding.ActivityProfileBinding
import com.example.sapper.model.constant.Constant
import com.example.sapper.view.Utils

class ProfileActivity : AppCompatActivity() {

    lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.onActivityCreateSetTheme(this)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currentUsername =
            getSharedPreferences(
                Constant().APP_PREFERENCE_USER,
                MODE_PRIVATE
            ).getString(Constant().CURRENT_USER_NAME, "")

        val statusText: String
        if (currentUsername == "") {
            statusText = "Вы не авторизованы!"
            binding.btnProfileScreenLogOut.isEnabled = false
        } else {
            statusText = "Вы авторизованы как $currentUsername."
            binding.btnProfileScreenLogIn.isEnabled = false
        }
        binding.tvProfileScreenStatus.text = statusText

        binding.btnProfileScreenLogIn.setOnClickListener {
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.btnProfileScreenRegistration.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.btnProfileScreenLogOut.setOnClickListener {
            getSharedPreferences(
                Constant().APP_PREFERENCE_USER,
                MODE_PRIVATE
            ).edit()
                .putLong(Constant().CURRENT_USER_ID, -1)
                .putString(Constant().CURRENT_USER_NAME, "")
                .apply()
            startActivity(Intent(this,ProfileActivity::class.java))
            finish()
        }
    }
}