package com.example.sapper.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sapper.R
import com.example.sapper.databinding.ActivityRegistrationBinding
import com.example.sapper.view.Utils

class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        Utils.onActivityCreateSetTheme(this)
        setContentView(binding.root)

        binding.tvRegistrationScreenHasAccount.setOnClickListener { view1 ->
            val intent = Intent(
                this@RegistrationActivity,
                AuthActivity::class.java
            )
            startActivity(intent)
        }
    }
}