package com.example.minbook

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.minbook.db.Buku
import com.example.minbook.db.BukuRepository
import com.example.minbook.db.MinbookDatabase
import java.util.Calendar

class EditBukuActivity : AppCompatActivity() {

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
    private var currentBuku: Buku? = null

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
        setContentView(R.layout.activity_edit_buku)

        // Initialize Views
        initViews()

        // Initialize ViewModel
        initViewModel()

        // Get data from intent and populate the form
        currentBuku = intent.getParcelableExtra("buku")
        currentBuku?.let { populateForm(it) }

        setupActionListeners()
    }

    private fun initViews() {
        etJudul = findViewById(R.id.etJudulBuku)
        etPenulis = findViewById(R.id.etPenulisBuku)
        rgKategori = findViewById(R.id.rgKategori)
        etTanggalTerbit = findViewById(R.id.etTanggalTerbit)
        flCover = findViewById(R.id.flCover)
        imgCoverPreview = findViewById(R.id.imgCoverPreview)
        tvUploadHint = findViewById(R.id.tvUploadHint)
        btnSimpan = findViewById(R.id.btnSimpan)
        btnBatal = findViewById(R.id.btnBatal)
    }

    private fun initViewModel() {
        val database = MinbookDatabase.getDatabase(this)
        val repository = BukuRepository(database.bukuDao())
        val factory = BukuViewModelFactory(repository)
        bukuViewModel = ViewModelProvider(this, factory)[BukuViewModel::class.java]
    }

    private fun populateForm(buku: Buku) {
        etJudul.setText(buku.judul)
        etPenulis.setText(buku.penulis)
        etTanggalTerbit.setText(buku.tanggalTerbit)

        if (buku.kategori == "Fiksi") {
            rgKategori.check(R.id.rbFiksi)
        } else {
            rgKategori.check(R.id.rbNonFiksi)
        }

        if (buku.cover.isNotBlank()) {
            selectedImageUri = buku.cover.toUri()
            Glide.with(this).load(selectedImageUri).into(imgCoverPreview)
            tvUploadHint.visibility = View.GONE
        }
    }

    private fun setupActionListeners() {
        flCover.setOnClickListener { pickImageLauncher.launch("image/*") }

        etTanggalTerbit.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, { _, y, m, d ->
                etTanggalTerbit.setText("$d/${m + 1}/$y")
            }, year, month, day).show()
        }

        btnBatal.setOnClickListener { finish() }

        btnSimpan.setOnClickListener { updateBuku() }
    }

    private fun updateBuku() {
        val judul = etJudul.text.toString().trim()
        val penulis = etPenulis.text.toString().trim()
        val selectedKategoriId = rgKategori.checkedRadioButtonId
        val kategori = if (selectedKategoriId != -1) findViewById<RadioButton>(selectedKategoriId).text.toString() else ""
        val tanggalTerbit = etTanggalTerbit.text.toString().trim()

        if (judul.isBlank() || penulis.isBlank() || kategori.isBlank() || selectedImageUri == null) {
            Toast.makeText(this, "Semua kolom harus diisi dan cover dipilih", Toast.LENGTH_LONG).show()
            return
        }

        val updatedBuku = Buku(
            id = currentBuku!!.id, // Penting: Gunakan ID yang sama
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