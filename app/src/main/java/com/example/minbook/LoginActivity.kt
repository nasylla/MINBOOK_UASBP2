package com.example.minbook

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.minbook.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    // Daftar admin (username to password)
    private val validAdmins = mapOf(
        "admin" to "admin",
        "nasylla" to "nasylla123",
        "intan" to "intan123",
        "mutya" to "mutya123"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString()
            val password = binding.etPassword.text.toString()

            if (validAdmins.containsKey(username) && validAdmins[username] == password) {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("LOGGED_IN_USER", username)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Username atau Password Salah", Toast.LENGTH_SHORT).show()
            }
        }
    }
}