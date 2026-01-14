package com.example.minbook

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.minbook.databinding.FragmentBukuBinding
import com.example.minbook.db.Buku
import com.example.minbook.db.BukuRepository
import com.example.minbook.db.MinbookDatabase

class BukuFragment : Fragment() {

    private var _binding: FragmentBukuBinding? = null
    private val binding get() = _binding!!

    private lateinit var bukuAdapter: BukuAdapter
    private lateinit var bukuViewModel: BukuViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBukuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvBuku.layoutManager = LinearLayoutManager(context)

        val database = MinbookDatabase.getDatabase(requireContext())
        val repository = BukuRepository(database.bukuDao())
        val factory = BukuViewModelFactory(repository)

        bukuViewModel = ViewModelProvider(this, factory)[BukuViewModel::class.java]

        val deleteAction = { buku: Buku ->
            bukuViewModel.delete(buku)
        }

        bukuAdapter = BukuAdapter(emptyList(), deleteAction)
        binding.rvBuku.adapter = bukuAdapter

        bukuViewModel.semuaBuku.observe(viewLifecycleOwner) { bukuList ->
            bukuAdapter.updateData(bukuList)
        }

        binding.btnAdd.setOnClickListener {
            val intent = Intent(activity, TambahBukuActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}