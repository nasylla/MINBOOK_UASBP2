package com.example.minbook

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.minbook.db.MinbookDatabase
import com.example.minbook.db.Peminjaman
import com.example.minbook.db.PeminjamanRepository
import java.util.Calendar

class TambahPeminjamanActivity : AppCompatActivity() {

    private lateinit var etNamaPeminjam: EditText
    private lateinit var tvPilihBuku: TextView
    private lateinit var etTanggalPinjam: EditText
    private lateinit var etTanggalKembali: EditText
    private lateinit var etPetugas: EditText
    private lateinit var spinnerStatus: Spinner
    private lateinit var btnSimpan: Button
    private lateinit var btnBatal: Button

    private lateinit var peminjamanViewModel: PeminjamanViewModel

    private var selectedBukuId: Int? = null

    // Launcher untuk menerima hasil dari PilihBukuActivity
    private val pilihBukuLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            selectedBukuId = data?.getIntExtra("SELECTED_BOOK_ID", -1)
            val selectedBukuTitle = data?.getStringExtra("SELECTED_BOOK_TITLE")

            if (selectedBukuId != -1 && selectedBukuTitle != null) {
                tvPilihBuku.text = selectedBukuTitle
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_peminjaman)

        initViews()
        initViewModel()
        setupStatusSpinner()
        setupActionListeners()
    }

    private fun initViews() {
        etNamaPeminjam = findViewById(R.id.etNamaPeminjam)
        tvPilihBuku = findViewById(R.id.tvPilihBuku)
        etTanggalPinjam = findViewById(R.id.etTanggalPinjam)
        etTanggalKembali = findViewById(R.id.etTanggalKembali)
        etPetugas = findViewById(R.id.etPetugas)
        spinnerStatus = findViewById(R.id.spinnerStatus)
        btnSimpan = findViewById(R.id.btnSimpan)
        btnBatal = findViewById(R.id.btnBatal)
    }

    private fun initViewModel() {
        val database = MinbookDatabase.getDatabase(this)
        val peminjamanRepository = PeminjamanRepository(database.peminjamanDao())
        val peminjamanFactory = PeminjamanViewModelFactory(peminjamanRepository)
        peminjamanViewModel = ViewModelProvider(this, peminjamanFactory)[PeminjamanViewModel::class.java]
    }

    private fun setupStatusSpinner() {
        val statusAdapter = ArrayAdapter.createFromResource(
            this, R.array.status_peminjaman, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinnerStatus.adapter = statusAdapter
    }

    private fun setupActionListeners() {
        btnBatal.setOnClickListener { finish() }
        btnSimpan.setOnClickListener { savePeminjaman() }

        tvPilihBuku.setOnClickListener {
            val intent = Intent(this, PilihBukuActivity::class.java)
            pilihBukuLauncher.launch(intent)
        }

        etTanggalPinjam.setOnClickListener { showDatePickerDialog(etTanggalPinjam) }
        etTanggalKembali.setOnClickListener { showDatePickerDialog(etTanggalKembali) }
    }

    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this, { _, year, month, dayOfMonth -> editText.setText("$dayOfMonth/${month + 1}/$year") },
            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun savePeminjaman() {
        val namaPeminjam = etNamaPeminjam.text.toString().trim()
        val tanggalPinjam = etTanggalPinjam.text.toString().trim()
        val tanggalKembali = etTanggalKembali.text.toString().trim()
        val petugas = etPetugas.text.toString().trim()

        if (namaPeminjam.isEmpty() || tanggalPinjam.isEmpty() || selectedBukuId == null) {
            Toast.makeText(this, "Nama, Judul Buku, dan Tanggal Pinjam harus diisi", Toast.LENGTH_LONG).show()
            return
        }

        val status = spinnerStatus.selectedItem.toString()

        val peminjamanBaru = Peminjaman(
            namaPeminjam = namaPeminjam,
            bukuId = selectedBukuId!!,
            tanggalPinjam = tanggalPinjam,
            tanggalKembali = tanggalKembali,
            petugas = petugas,
            status = status
        )

        peminjamanViewModel.insert(peminjamanBaru)
        Toast.makeText(this, "Data peminjaman berhasil ditambahkan", Toast.LENGTH_SHORT).show()
        finish()
    }
}