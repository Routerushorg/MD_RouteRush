package com.example.routerush.ui.register

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
import com.example.routerush.databinding.ActivityRegisterBinding
import com.example.routerush.ui.ViewModelFactory
import com.example.routerush.ui.login.LoginViewModel
import com.example.routerush.ui.main.MainActivity

class RegisterActivity : AppCompatActivity() {
    private val viewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setupView()
        setupAction()
        viewModel.registerResult.observe(this) { result ->
            result.onSuccess { response ->
                // Tampilkan hasil sukses
                showSuccessDialog(response.message ?: "Registrasi berhasil")

            }.onFailure { exception ->
                // Tampilkan pesan error
                showErrorDialog((exception.message + getString(R.string.error_existingemail)) ?: "Registrasi gagal")
            }
        }
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
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
        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            // Pengecekan untuk validitas input
            when {
                name.isBlank() && email.isBlank() && password.isBlank() -> {
                    showErrorDialog(getString(R.string.error_empty))
                }
                name.isBlank() -> {
                    showErrorDialog(getString(R.string.error_empty_name))
                }
                email.isBlank() -> {
                    showErrorDialog(getString(R.string.error_empty_email))
                }
                password.isBlank() -> {
                    showErrorDialog(getString(R.string.error_empty_password))
                }
                password.length < 8 -> {
                    showErrorDialog(getString(R.string.error_short_password))
                }
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    showErrorDialog(getString(R.string.error_invalid_email))
                }
                else -> {
                    // Semua validasi berhasil, lanjutkan dengan registrasi
                    viewModel.register(name, email, password)

                }
            }
        }
    }


    private fun showSuccessDialog(message: String) {
        AlertDialog.Builder(this).apply {
            setTitle(message)
            setMessage(R.string.register_success_message)
            setPositiveButton(R.string.next) { _, _ ->
                finish()
            }
            create()
            show()
        }
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(this).apply {
            setTitle(R.string.register_fail)
            setMessage(message)
            setPositiveButton(R.string.retry, null)
            create()
            show()
        }
    }
}