package com.example.sapper.view.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sapper.databinding.ActivityAuthBinding
import com.example.sapper.model.constant.Constant
import com.example.sapper.model.dto.AuthenticationRequestDto
import com.example.sapper.model.dto.LoginResponseDto
import com.example.sapper.model.entity.web.WebPlayer
import com.example.sapper.controller.network.NetworkService
import com.example.sapper.view.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalTime

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private val TAG = "AuthActivityTAG:"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        val view: View = binding.root
        Utils.onActivityCreateSetTheme(this)
        setContentView(view)

        binding.tvLoginScreenNotRegister.setOnClickListener {
            val intent = Intent(
                this@AuthActivity,
                RegistrationActivity::class.java
            )
            startActivity(intent)
        }
        binding.btnLoginScreenLogin.setOnClickListener(clickListener)
        //TODO(удалить эти листенеры. Нужны дял быстрого входа на тесты)
        binding.imageView.setOnClickListener { v ->
            val body = AuthenticationRequestDto()
            body.username = "qqqq"
            body.password = "qqqq"

            NetworkService.getAuthApi()
                .login(body)
                .enqueue(object : Callback<LoginResponseDto> {
                    override fun onResponse(
                        call: Call<LoginResponseDto>,
                        response: Response<LoginResponseDto>
                    ) {
                        val body = response.body()
                        Log.e(TAG, "onResponse: $body")
                        when (response.body().status) {
                            "Success." -> {
                                Toast.makeText(
                                    this@AuthActivity,
                                    "logged in",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val webPlayer = WebPlayer(body!!)

                                val sp = getSharedPreferences(
                                    Constant().APP_PREFERENCE_USER,
                                    MODE_PRIVATE
                                )
                                sp.edit()
                                    .putLong(Constant().CURRENT_USER_ID, webPlayer.userId)
                                    .putString(Constant().CURRENT_USER_NAME, webPlayer.username)
                                    .apply()
                                finish()
                            }
                            "Error." -> {
                                Toast.makeText(
                                    this@AuthActivity,
                                    body.description,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }

                    override fun onFailure(
                        call: Call<LoginResponseDto>,
                        t: Throwable
                    ) {
                        //Произошла ошибка
                        Toast.makeText(
                            this@AuthActivity,
                            "Error",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
        binding.imageView.setOnLongClickListener { v ->
            val body = AuthenticationRequestDto()
            body.username = "test"
            body.password = "test"
            NetworkService.getAuthApi()
                .login(body)
                .enqueue(object : Callback<LoginResponseDto> {
                    override fun onResponse(
                        call: Call<LoginResponseDto>,
                        response: Response<LoginResponseDto>
                    ) {
                        val body = response.body()
                        Log.e(TAG, "onResponse: $body")
                        when (response.body().status) {
                            "Success." -> {
                                Toast.makeText(
                                    this@AuthActivity,
                                    "logged in",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val webPlayer = WebPlayer(body!!)

                                val sp = getSharedPreferences(
                                    Constant().APP_PREFERENCE_USER,
                                    MODE_PRIVATE
                                )
                                sp.edit()
                                    .putLong(Constant().CURRENT_USER_ID, webPlayer.userId)
                                    .putString(Constant().CURRENT_USER_NAME, webPlayer.username)
                                    .apply()
                                finish()
                            }
                            "Error." -> {
                                Toast.makeText(
                                    this@AuthActivity,
                                    body.description,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }

                    override fun onFailure(
                        call: Call<LoginResponseDto>,
                        t: Throwable
                    ) {
                        //Произошла ошибка
                        Toast.makeText(
                            this@AuthActivity,
                            "Error",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            true
        }
    }

    private var clickListener = View.OnClickListener {
        if (binding.tilLoginScreenLogin.error != null
            || binding.tilLoginScreenPassword.error != null
        ) {
            Log.e(TAG, "onClick: HAVE AN ERROR IN INPUIt")
            return@OnClickListener
        }
        /**----------------------------------------------------------------- */
        val body = AuthenticationRequestDto()
        body.username = binding.etLoginScreenLogin.text.toString()
        body.password = binding.etLoginScreenPassword.text.toString()

        NetworkService.getAuthApi()
            .login(body)
            .enqueue(object : Callback<LoginResponseDto?> {
                override fun onResponse(
                    call: Call<LoginResponseDto?>?,
                    response: Response<LoginResponseDto?>
                ) {
                    val body: LoginResponseDto? = response.body()
                    Log.e(TAG, "onResponse: " + body.toString())
                    when (response.body()?.status) {
                        "Success." -> {
                            Toast.makeText(this@AuthActivity, "logged in", Toast.LENGTH_SHORT)
                                .show()
                            val webPlayer = WebPlayer(body!!)

                            val sp =
                                getSharedPreferences(Constant().APP_PREFERENCE_USER, MODE_PRIVATE)
                            sp.edit()
                                .putLong(Constant().CURRENT_USER_ID, webPlayer.userId)
                                .putString(Constant().CURRENT_USER_NAME, webPlayer.username)
                                .apply()
                            finish()
                        }
                        "Error." -> {
                            Toast.makeText(
                                this@AuthActivity,
                                body?.description,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                override fun onFailure(call: Call<LoginResponseDto?>?, t: Throwable?) {
                    Toast.makeText(this@AuthActivity, "Error", Toast.LENGTH_SHORT).show()
                }
            })
    }
}