package com.example.minbook

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.minbook.db.MinbookDatabase
import com.example.minbook.db.Peminjaman
import com.example.minbook.db.PeminjamanDetail
import com.example.minbook.db.PeminjamanRepository

class PeminjamanFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var peminjamanAdapter: PeminjamanAdapter
    private lateinit var peminjamanViewModel: PeminjamanViewModel
    private lateinit var btnAddPeminjaman: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_peminjaman, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.rvPeminjaman)
        recyclerView.layoutManager = LinearLayoutManager(context)

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
        recyclerView.adapter = peminjamanAdapter

        val database = MinbookDatabase.getDatabase(requireContext())
        val repository = PeminjamanRepository(database.peminjamanDao())
        val factory = PeminjamanViewModelFactory(repository)
        peminjamanViewModel = ViewModelProvider(this, factory)[PeminjamanViewModel::class.java]

        peminjamanViewModel.semuaPeminjamanDetail.observe(viewLifecycleOwner) { peminjamanList ->
            peminjamanAdapter.updateData(peminjamanList)
        }

        btnAddPeminjaman = view.findViewById(R.id.btnAddPeminjaman)
        btnAddPeminjaman.setOnClickListener {
            val intent = Intent(activity, TambahPeminjamanActivity::class.java)
            startActivity(intent)
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
}