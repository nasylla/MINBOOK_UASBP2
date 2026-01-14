package com.example.minbook

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.minbook.databinding.ActivityTambahBukuBinding
import com.example.minbook.db.Buku
import com.example.minbook.db.BukuRepository
import com.example.minbook.db.MinbookDatabase
import java.util.Calendar

class TambahBukuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTambahBukuBinding
    private lateinit var bukuViewModel: BukuViewModel
    private var selectedImageUri: Uri? = null

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            Glide.with(this).load(it).into(binding.imgCoverPreview)
            binding.tvUploadHint.visibility = View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTambahBukuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val database = MinbookDatabase.getDatabase(this)
        val repository = BukuRepository(database.bukuDao())
        val factory = BukuViewModelFactory(repository)
        bukuViewModel = ViewModelProvider(this, factory)[BukuViewModel::class.java]

        setupActionListeners()
    }

    private fun setupActionListeners() {
        binding.flCover.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.etTanggalTerbit.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, {
                _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                binding.etTanggalTerbit.setText(selectedDate)
            }, year, month, day).show()
        }

        binding.btnBatal.setOnClickListener {
            finish() // Tutup activity ini
        }

        binding.btnSimpan.setOnClickListener {
            saveBuku()
        }
    }

    private fun saveBuku() {
        val judul = binding.etJudulBuku.text.toString()
        val penulis = binding.etPenulisBuku.text.toString()
        val selectedKategoriId = binding.rgKategori.checkedRadioButtonId
        val kategori = if (selectedKategoriId != -1) findViewById<RadioButton>(selectedKategoriId).text.toString() else ""
        val tanggalTerbit = binding.etTanggalTerbit.text.toString()

        if (judul.isBlank() || penulis.isBlank() || kategori.isBlank() || selectedImageUri == null) {
            Toast.makeText(this, "Semua kolom harus diisi dan cover dipilih", Toast.LENGTH_LONG).show()
            return
        }

        val bukuBaru = Buku(
            judul = judul,
            penulis = penulis,
            kategori = kategori,
            tanggalTerbit = tanggalTerbit,
            cover = selectedImageUri.toString()
        )
        bukuViewModel.insert(bukuBaru)
        Toast.makeText(this, "Buku berhasil ditambahkan", Toast.LENGTH_SHORT).show()
        finish() // Kembali ke halaman sebelumnya setelah menyimpan
    }
}