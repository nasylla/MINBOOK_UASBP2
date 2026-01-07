package com.example.minbook

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.minbook.db.Buku
import com.example.minbook.db.BukuRepository
import com.example.minbook.db.MinbookDatabase

class BukuFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var bukuAdapter: BukuAdapter
    private lateinit var bukuViewModel: BukuViewModel
    private lateinit var btnAdd: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_buku, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.rvBuku)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val database = MinbookDatabase.getDatabase(requireContext())
        val repository = BukuRepository(database.bukuDao())
        val factory = BukuViewModelFactory(repository)

        bukuViewModel = ViewModelProvider(this, factory)[BukuViewModel::class.java]

        // Mendefinisikan aksi hapus secara eksplisit
        val deleteAction = { buku: Buku ->
            bukuViewModel.delete(buku)
        }

        // Inisialisasi adapter dengan cara yang lebih aman
        bukuAdapter = BukuAdapter(emptyList(), deleteAction)
        recyclerView.adapter = bukuAdapter

        bukuViewModel.semuaBuku.observe(viewLifecycleOwner) { bukuList ->
            bukuAdapter.updateData(bukuList)
        }

        // Saat tombol tambah ditekan, buka TambahBukuActivity
        btnAdd = view.findViewById(R.id.btnAdd)
        btnAdd.setOnClickListener {
            val intent = Intent(activity, TambahBukuActivity::class.java)
            startActivity(intent)
        }
    }
}