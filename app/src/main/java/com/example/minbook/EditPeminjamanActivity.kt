package com.example.minbook

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.minbook.db.MinbookDatabase
import com.example.minbook.db.Peminjaman
import com.example.minbook.db.PeminjamanDetail
import com.example.minbook.db.PeminjamanRepository
import java.util.Calendar

class EditPeminjamanActivity : AppCompatActivity() {

    private lateinit var etNamaPeminjam: EditText
    private lateinit var tvJudulBuku: TextView
    private lateinit var etTanggalPinjam: EditText
    private lateinit var etTanggalKembali: EditText
    private lateinit var etPetugas: EditText
    private lateinit var spinnerStatus: Spinner
    private lateinit var btnSimpan: Button
    private lateinit var btnBatal: Button

    private lateinit var peminjamanViewModel: PeminjamanViewModel
    private var currentPeminjaman: PeminjamanDetail? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_peminjaman)

        initViews()
        initViewModel()

        currentPeminjaman = intent.getParcelableExtra("peminjaman")
        currentPeminjaman?.let { populateForm(it) }

        setupActionListeners()
    }

    private fun initViews() {
        etNamaPeminjam = findViewById(R.id.etNamaPeminjam)
        tvJudulBuku = findViewById(R.id.tvJudulBukuValue)
        etTanggalPinjam = findViewById(R.id.etTanggalPinjam)
        etTanggalKembali = findViewById(R.id.etTanggalKembali)
        etPetugas = findViewById(R.id.etPetugas)
        spinnerStatus = findViewById(R.id.spinnerStatus)
        btnSimpan = findViewById(R.id.btnSimpan)
        btnBatal = findViewById(R.id.btnBatal)
    }

    private fun initViewModel() {
        val database = MinbookDatabase.getDatabase(this)
        val repository = PeminjamanRepository(database.peminjamanDao())
        val factory = PeminjamanViewModelFactory(repository)
        peminjamanViewModel = ViewModelProvider(this, factory)[PeminjamanViewModel::class.java]
    }

    private fun populateForm(peminjaman: PeminjamanDetail) {
        etNamaPeminjam.setText(peminjaman.namaPeminjam)
        tvJudulBuku.text = peminjaman.judul
        etTanggalPinjam.setText(peminjaman.tanggalPinjam)
        etTanggalKembali.setText(peminjaman.tanggalKembali ?: "")
        etPetugas.setText(peminjaman.petugas)

        val statusAdapter = ArrayAdapter.createFromResource(
            this, R.array.status_peminjaman, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerStatus.adapter = adapter
        }

        val statusPosition = statusAdapter.getPosition(peminjaman.status)
        spinnerStatus.setSelection(statusPosition)
    }

    private fun setupActionListeners() {
        btnBatal.setOnClickListener { finish() }
        btnSimpan.setOnClickListener { updatePeminjaman() }

        etTanggalPinjam.setOnClickListener { showDatePickerDialog(etTanggalPinjam) }
        etTanggalKembali.setOnClickListener { showDatePickerDialog(etTanggalKembali) }
    }

    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(this, { _, y, m, d -> editText.setText("$d/${m + 1}/$y") },
            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updatePeminjaman() {
        val namaPeminjam = etNamaPeminjam.text.toString().trim()
        val tanggalPinjam = etTanggalPinjam.text.toString().trim()
        val tanggalKembali = etTanggalKembali.text.toString().trim()
        val petugas = etPetugas.text.toString().trim()
        val status = spinnerStatus.selectedItem.toString()

        if (namaPeminjam.isEmpty() || tanggalPinjam.isEmpty()) {
            Toast.makeText(this, "Nama Peminjam dan Tanggal Pinjam tidak boleh kosong", Toast.LENGTH_LONG).show()
            return
        }

        val updatedPeminjaman = Peminjaman(
            pinjamId = currentPeminjaman!!.pinjamId,
            bukuId = currentPeminjaman!!.bukuId,
            namaPeminjam = namaPeminjam,
            tanggalPinjam = tanggalPinjam,
            tanggalKembali = tanggalKembali,
            petugas = petugas,
            status = status
        )

        peminjamanViewModel.update(updatedPeminjaman)
        Toast.makeText(this, "Data peminjaman berhasil diperbarui", Toast.LENGTH_SHORT).show()
        finish()
    }
}