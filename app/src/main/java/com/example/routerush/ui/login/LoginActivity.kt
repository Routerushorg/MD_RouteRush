package com.example.routerush.ui.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.routerush.R
import com.example.routerush.databinding.ActivityLoginBinding
import com.example.routerush.ui.ViewModelFactory
import com.example.routerush.ui.map.HomeActivity

class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding : ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()

    }
    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            when {
                email.isBlank() -> {
                    Toast.makeText(this,getString(R.string.error_empty_email), Toast.LENGTH_SHORT).show()
                }
                password.isBlank() -> {
                    Toast.makeText(this,getString(R.string.error_empty_password), Toast.LENGTH_SHORT).show()
                }
                password.length < 8 -> {
                    Toast.makeText(this,getString(R.string.error_short_password), Toast.LENGTH_SHORT).show()
                }
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    Toast.makeText(this,getString(R.string.error_invalid_email), Toast.LENGTH_SHORT).show()
                }
                else -> {
                    // Semua validasi berhasil, lanjutkan dengan registrasi
                    viewModel.login( email, password)

                }
            }
        loginResponse()
        observeErrorState()
        observeLoadingState()

        }
    }

    private fun loginResponse() {
        viewModel.loginResponse.observe(this) { response ->
            if (response.error != null ) {
                Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
            } else {
                AlertDialog.Builder(this).apply {
                    setTitle(getString(R.string.loginResult))
                    setPositiveButton(getString(R.string.next)) { _, _ ->
                        val intent = Intent(context, HomeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                        showLoading(false)
                    }
                    create()
                    show()
                }
            }
        }
    }
    private fun observeErrorState() {
        viewModel.isError.observe(this) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Toast.makeText(this, errorMessage + " | Email or Password not found, Please Try Again!", Toast.LENGTH_SHORT).show()
                showLoading(false)
            }
        }
    }
    private fun showLoading(isLoading: Boolean) {
        if (isLoading){
            binding.progressBar.visibility = View.VISIBLE
        }else{
            binding.progressBar.visibility = View.INVISIBLE
        }
    }
    private fun observeLoadingState() {
        viewModel.isLoading.observe(this){ isLoading ->
            showLoading(isLoading)
        }
    }
}