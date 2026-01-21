package com.example.minbook

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.minbook.databinding.ActivityEditBukuBinding
import com.example.minbook.db.Buku
import com.example.minbook.db.BukuRepository
import com.example.minbook.db.MinbookDatabase
import java.util.Calendar

class EditBukuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditBukuBinding
    private lateinit var bukuViewModel: BukuViewModel
    private var selectedImageUri: Uri? = null
    private var currentBuku: Buku? = null

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
        binding = ActivityEditBukuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViewModel()

        currentBuku = intent.getParcelableExtra("buku")
        currentBuku?.let { populateForm(it) }

        setupActionListeners()
    }

    private fun initViewModel() {
        val database = MinbookDatabase.getDatabase(this)
        val repository = BukuRepository(database.bukuDao())
        val factory = BukuViewModelFactory(repository)
        bukuViewModel = ViewModelProvider(this, factory)[BukuViewModel::class.java]
    }

    private fun populateForm(buku: Buku) {
        binding.etJudulBuku.setText(buku.judul)
        binding.etPenulisBuku.setText(buku.penulis)
        binding.etTanggalTerbit.setText(buku.tanggalTerbit)

        if (buku.kategori == "Fiksi") {
            binding.rgKategori.check(R.id.rbFiksi)
        } else {
            binding.rgKategori.check(R.id.rbNonFiksi)
        }

        if (buku.cover.isNotBlank()) {
            selectedImageUri = buku.cover.toUri()
            Glide.with(this).load(selectedImageUri).into(binding.imgCoverPreview)
            binding.tvUploadHint.visibility = View.GONE
        }
    }

    private fun setupActionListeners() {
        binding.flCover.setOnClickListener { pickImageLauncher.launch("image/*") }

        binding.etTanggalTerbit.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, { _, y, m, d ->
                binding.etTanggalTerbit.setText("$d/${m + 1}/$y")
            }, year, month, day).show()
        }

        binding.btnBatal.setOnClickListener { finish() }

        binding.btnSimpan.setOnClickListener { updateBuku() }
    }

    private fun updateBuku() {
        val judul = binding.etJudulBuku.text.toString().trim()
        val penulis = binding.etPenulisBuku.text.toString().trim()
        val selectedKategoriId = binding.rgKategori.checkedRadioButtonId
        val kategori = if (selectedKategoriId != -1) findViewById<RadioButton>(selectedKategoriId).text.toString() else ""
        val tanggalTerbit = binding.etTanggalTerbit.text.toString().trim()

        if (judul.isBlank() || penulis.isBlank() || kategori.isBlank() || selectedImageUri == null) {
            Toast.makeText(this, "Semua kolom harus diisi dan cover dipilih", Toast.LENGTH_LONG).show()
            return
        }

        val updatedBuku = Buku(
            id = currentBuku!!.id,
            judul = judul,
            penulis = penulis,
            kategori = kategori,
            tanggalTerbit = tanggalTerbit,
            cover = selectedImageUri.toString()
        )

        bukuViewModel.update(updatedBuku)
        Toast.makeText(this, "Buku berhasil diperbarui", Toast.LENGTH_SHORT).show()
        finish()
    }
}