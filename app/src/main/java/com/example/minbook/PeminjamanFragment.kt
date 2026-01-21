package com.example.minbook

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.minbook.databinding.FragmentPeminjamanBinding
import com.example.minbook.db.MinbookDatabase
import com.example.minbook.db.Peminjaman
import com.example.minbook.db.PeminjamanDetail
import com.example.minbook.db.PeminjamanRepository

class PeminjamanFragment : Fragment() {

    private var _binding: FragmentPeminjamanBinding? = null
    private val binding get() = _binding!!

    private lateinit var peminjamanAdapter: PeminjamanAdapter
    private lateinit var peminjamanViewModel: PeminjamanViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPeminjamanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvPeminjaman.layoutManager = LinearLayoutManager(context)

        val editAction = { peminjaman: PeminjamanDetail ->
            val intent = Intent(activity, EditPeminjamanActivity::class.java).apply {
                putExtra("peminjaman", peminjaman)
            }
            startActivity(intent)
        }

        val deleteAction = { peminjamanDetail: PeminjamanDetail ->
            showDeleteConfirmationDialog(peminjamanDetail)
        }

        peminjamanAdapter = PeminjamanAdapter(emptyList(), editAction, deleteAction)
        binding.rvPeminjaman.adapter = peminjamanAdapter

        val database = MinbookDatabase.getDatabase(requireContext())
        val repository = PeminjamanRepository(database.peminjamanDao())
        val factory = PeminjamanViewModelFactory(repository)
        peminjamanViewModel = ViewModelProvider(this, factory)[PeminjamanViewModel::class.java]

        peminjamanViewModel.semuaPeminjamanDetail.observe(viewLifecycleOwner) { peminjamanList ->
            peminjamanAdapter.updateData(peminjamanList)
        }

        binding.btnAddPeminjaman.setOnClickListener {
            val intent = Intent(activity, TambahPeminjamanActivity::class.java)
            startActivity(intent)
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchPeminjaman(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun searchPeminjaman(query: String) {
        val searchQuery = "%$query%"
        peminjamanViewModel.searchPeminjaman(searchQuery).observe(viewLifecycleOwner) { list ->
            list?.let {
                peminjamanAdapter.updateData(it)
            }
        }
    }

    private fun showDeleteConfirmationDialog(peminjamanDetail: PeminjamanDetail) {
        AlertDialog.Builder(requireContext())
            .setTitle("Konfirmasi Hapus")
            .setMessage("Yakin ingin menghapus peminjaman buku '${peminjamanDetail.judul}' oleh ${peminjamanDetail.namaPeminjam}?")
            .setPositiveButton("Hapus") { _, _ ->
                deletePeminjaman(peminjamanDetail)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun deletePeminjaman(peminjamanDetail: PeminjamanDetail) {
        val peminjaman = Peminjaman(
            pinjamId = peminjamanDetail.pinjamId,
            bukuId = peminjamanDetail.bukuId,
            namaPeminjam = peminjamanDetail.namaPeminjam,
            tanggalPinjam = peminjamanDetail.tanggalPinjam,
            tanggalKembali = peminjamanDetail.tanggalKembali ?: "",
            petugas = peminjamanDetail.petugas,
            status = peminjamanDetail.status
        )
        peminjamanViewModel.delete(peminjaman)
        Toast.makeText(context, "Peminjaman berhasil dihapus", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}