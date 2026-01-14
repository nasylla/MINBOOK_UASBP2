package com.example.minbook

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.minbook.databinding.ActivityPilihBukuBinding
import com.example.minbook.db.Buku
import com.example.minbook.db.BukuRepository
import com.example.minbook.db.MinbookDatabase

class PilihBukuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPilihBukuBinding
    private lateinit var pilihBukuAdapter: PilihBukuAdapter
    private lateinit var bukuViewModel: BukuViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPilihBukuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvPilihBuku.layoutManager = LinearLayoutManager(this)

        val onBukuSelected = { buku: Buku ->
            val resultIntent = Intent()
            resultIntent.putExtra("SELECTED_BOOK_ID", buku.id)
            resultIntent.putExtra("SELECTED_BOOK_TITLE", buku.judul)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        pilihBukuAdapter = PilihBukuAdapter(emptyList(), onBukuSelected)
        binding.rvPilihBuku.adapter = pilihBukuAdapter

        val database = MinbookDatabase.getDatabase(this)
        val repository = BukuRepository(database.bukuDao())
        val factory = BukuViewModelFactory(repository)
        bukuViewModel = ViewModelProvider(this, factory)[BukuViewModel::class.java]

        bukuViewModel.semuaBuku.observe(this) { bukuList ->
            pilihBukuAdapter.updateData(bukuList)
        }
    }
}