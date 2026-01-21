package com.example.minbook

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.minbook.databinding.FragmentHomeBinding
import com.example.minbook.db.MinbookDatabase

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var username: String? = null

    private lateinit var db: MinbookDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            username = it.getString(ARG_USERNAME)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = MinbookDatabase.getDatabase(requireContext())

        binding.tvUsername.text = "Hi, ${username ?: "Admin"}"

        db.bukuDao().getBukuCount().observe(viewLifecycleOwner, {
            bukuCount -> binding.tvJumlahBuku.text = bukuCount.toString()
        })

        db.peminjamanDao().getActivePeminjamanCount().observe(viewLifecycleOwner, {
            peminjamanCount -> binding.tvPeminjamanAktif.text = peminjamanCount.toString()
        })

        binding.profileImage.setOnClickListener {
            val intent = Intent(activity, ProfileActivity::class.java)
            intent.putExtra("LOGGED_IN_USER", username)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_USERNAME = "username"

        @JvmStatic
        fun newInstance(username: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_USERNAME, username)
                }
            }
    }
}