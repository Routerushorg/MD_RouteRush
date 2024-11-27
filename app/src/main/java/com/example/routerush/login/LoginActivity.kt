package com.example.routerush.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.routerush.R
import com.example.routerush.activity_home

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Tombol Login
        val btnLogin = findViewById<Button>(R.id.btn_login)

        // Ketika tombol Login ditekan
        btnLogin.setOnClickListener {
            // Pindah ke HomeActivity
            val intent = Intent(this, activity_home::class.java)
            startActivity(intent)
            finish() // Mengakhiri LoginActivity agar tidak kembali
        }
    }

    class app_bar_activity_home {

    }
}