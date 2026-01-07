package com.example.minbook

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.minbook.db.Buku
import com.example.minbook.db.BukuRepository
import com.example.minbook.db.MinbookDatabase
import java.util.Calendar

class TambahBukuActivity : AppCompatActivity() {

    private lateinit var etJudul: EditText
    private lateinit var etPenulis: EditText
    private lateinit var rgKategori: RadioGroup
    private lateinit var etTanggalTerbit: EditText
    private lateinit var flCover: FrameLayout
    private lateinit var imgCoverPreview: ImageView
    private lateinit var tvUploadHint: TextView
    private lateinit var btnSimpan: Button
    private lateinit var btnBatal: Button

    private lateinit var bukuViewModel: BukuViewModel
    private var selectedImageUri: Uri? = null

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            Glide.with(this).load(it).into(imgCoverPreview)
            tvUploadHint.visibility = View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_buku)

        etJudul = findViewById(R.id.etJudulBuku)
        etPenulis = findViewById(R.id.etPenulisBuku)
        rgKategori = findViewById(R.id.rgKategori)
        etTanggalTerbit = findViewById(R.id.etTanggalTerbit)
        flCover = findViewById(R.id.flCover)
        imgCoverPreview = findViewById(R.id.imgCoverPreview)
        tvUploadHint = findViewById(R.id.tvUploadHint)
        btnSimpan = findViewById(R.id.btnSimpan)
        btnBatal = findViewById(R.id.btnBatal)

        val database = MinbookDatabase.getDatabase(this)
        val repository = BukuRepository(database.bukuDao())
        val factory = BukuViewModelFactory(repository)
        bukuViewModel = ViewModelProvider(this, factory)[BukuViewModel::class.java]

        setupActionListeners()
    }

    private fun setupActionListeners() {
        flCover.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        etTanggalTerbit.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, {
                _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                etTanggalTerbit.setText(selectedDate)
            }, year, month, day).show()
        }

        btnBatal.setOnClickListener {
            finish() // Tutup activity ini
        }

        btnSimpan.setOnClickListener {
            saveBuku()
        }
    }

    private fun saveBuku() {
        val judul = etJudul.text.toString()
        val penulis = etPenulis.text.toString()
        val selectedKategoriId = rgKategori.checkedRadioButtonId
        val kategori = if (selectedKategoriId != -1) findViewById<RadioButton>(selectedKategoriId).text.toString() else ""
        val tanggalTerbit = etTanggalTerbit.text.toString()

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