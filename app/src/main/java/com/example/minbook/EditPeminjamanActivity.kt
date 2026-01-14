package com.example.minbook

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.minbook.databinding.ActivityEditPeminjamanBinding
import com.example.minbook.db.MinbookDatabase
import com.example.minbook.db.Peminjaman
import com.example.minbook.db.PeminjamanDetail
import com.example.minbook.db.PeminjamanRepository
import java.util.Calendar

class EditPeminjamanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditPeminjamanBinding
    private lateinit var peminjamanViewModel: PeminjamanViewModel
    private var currentPeminjaman: PeminjamanDetail? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPeminjamanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViewModel()

        currentPeminjaman = intent.getParcelableExtra("peminjaman")
        currentPeminjaman?.let { populateForm(it) }

        setupActionListeners()
    }

    private fun initViewModel() {
        val database = MinbookDatabase.getDatabase(this)
        val repository = PeminjamanRepository(database.peminjamanDao())
        val factory = PeminjamanViewModelFactory(repository)
        peminjamanViewModel = ViewModelProvider(this, factory)[PeminjamanViewModel::class.java]
    }

    private fun populateForm(peminjaman: PeminjamanDetail) {
        binding.etNamaPeminjam.setText(peminjaman.namaPeminjam)
        binding.tvJudulBukuValue.text = peminjaman.judul
        binding.etTanggalPinjam.setText(peminjaman.tanggalPinjam)
        binding.etTanggalKembali.setText(peminjaman.tanggalKembali ?: "")
        binding.etPetugas.setText(peminjaman.petugas)

        val statusAdapter = ArrayAdapter.createFromResource(
            this, R.array.status_peminjaman, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerStatus.adapter = adapter
        }

        val statusPosition = statusAdapter.getPosition(peminjaman.status)
        binding.spinnerStatus.setSelection(statusPosition)
    }

    private fun setupActionListeners() {
        binding.btnBatal.setOnClickListener { finish() }
        binding.btnSimpan.setOnClickListener { updatePeminjaman() }

        binding.etTanggalPinjam.setOnClickListener { showDatePickerDialog(binding.etTanggalPinjam) }
        binding.etTanggalKembali.setOnClickListener { showDatePickerDialog(binding.etTanggalKembali) }
    }

    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(this, { _, y, m, d -> editText.setText("$d/${m + 1}/$y") },
            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updatePeminjaman() {
        val namaPeminjam = binding.etNamaPeminjam.text.toString().trim()
        val tanggalPinjam = binding.etTanggalPinjam.text.toString().trim()
        val tanggalKembali = binding.etTanggalKembali.text.toString().trim()
        val petugas = binding.etPetugas.text.toString().trim()
        val status = binding.spinnerStatus.selectedItem.toString()

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