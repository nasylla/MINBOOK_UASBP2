package com.example.minbook

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.minbook.databinding.ActivityTambahPeminjamanBinding
import com.example.minbook.db.MinbookDatabase
import com.example.minbook.db.Peminjaman
import com.example.minbook.db.PeminjamanRepository
import java.util.Calendar

class TambahPeminjamanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTambahPeminjamanBinding
    private lateinit var peminjamanViewModel: PeminjamanViewModel

    private var selectedBukuId: Int? = null

    private val pilihBukuLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            selectedBukuId = data?.getIntExtra("SELECTED_BOOK_ID", -1)
            val selectedBukuTitle = data?.getStringExtra("SELECTED_BOOK_TITLE")

            if (selectedBukuId != -1 && selectedBukuTitle != null) {
                binding.tvPilihBuku.text = selectedBukuTitle
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTambahPeminjamanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViewModel()
        setupStatusSpinner()
        setupActionListeners()
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
        binding.spinnerStatus.adapter = statusAdapter
    }

    private fun setupActionListeners() {
        binding.btnBatal.setOnClickListener { finish() }
        binding.btnSimpan.setOnClickListener { savePeminjaman() }

        binding.tvPilihBuku.setOnClickListener {
            val intent = Intent(this, PilihBukuActivity::class.java)
            pilihBukuLauncher.launch(intent)
        }

        binding.etTanggalPinjam.setOnClickListener { showDatePickerDialog(binding.etTanggalPinjam) }
        binding.etTanggalKembali.setOnClickListener { showDatePickerDialog(binding.etTanggalKembali) }
    }

    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this, { _, year, month, dayOfMonth -> editText.setText("$dayOfMonth/${month + 1}/$year") },
            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun savePeminjaman() {
        val namaPeminjam = binding.etNamaPeminjam.text.toString().trim()
        val tanggalPinjam = binding.etTanggalPinjam.text.toString().trim()
        val tanggalKembali = binding.etTanggalKembali.text.toString().trim()
        val petugas = binding.etPetugas.text.toString().trim()

        if (namaPeminjam.isEmpty() || tanggalPinjam.isEmpty() || selectedBukuId == null) {
            Toast.makeText(this, "Nama, Judul Buku, dan Tanggal Pinjam harus diisi", Toast.LENGTH_LONG).show()
            return
        }

        val status = binding.spinnerStatus.selectedItem.toString()

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