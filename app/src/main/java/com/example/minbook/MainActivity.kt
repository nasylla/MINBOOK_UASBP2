package com.example.minbook

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.minbook.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = intent.getStringExtra("LOGGED_IN_USER")

        // Buat instance HomeFragment dengan menyertakan username
        val homeFragment = HomeFragment.newInstance(username ?: "Admin")

        // Fragment default
        loadFragment(homeFragment)

        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> loadFragment(HomeFragment.newInstance(username ?: "Admin"))
                R.id.nav_buku -> loadFragment(BukuFragment())
                R.id.nav_peminjaman -> loadFragment(PeminjamanFragment())
            }
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.container.id, fragment)
            .commit()
    }
}
