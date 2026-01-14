package com.example.minbook

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.minbook.databinding.ItemBukuBinding
import com.example.minbook.db.Buku

class PilihBukuAdapter(
    private var bukuList: List<Buku>,
    private val onBukuSelected: (Buku) -> Unit
) : RecyclerView.Adapter<PilihBukuAdapter.PilihBukuViewHolder>() {

    inner class PilihBukuViewHolder(val binding: ItemBukuBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PilihBukuViewHolder {
        val binding = ItemBukuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.btnEdit.visibility = View.GONE
        binding.btnDelete.visibility = View.GONE
        return PilihBukuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PilihBukuViewHolder, position: Int) {
        val buku = bukuList[position]

        with(holder.binding) {
            tvJudulBuku.text = buku.judul
            tvPenulis.text = buku.penulis
            tvKategori.text = "Kategori: ${buku.kategori}"

            Glide.with(holder.itemView.context)
                .load(buku.cover)
                .placeholder(R.drawable.bg_card)
                .into(imgBuku)

            holder.itemView.setOnClickListener {
                onBukuSelected(buku)
            }
        }
    }

    override fun getItemCount(): Int {
        return bukuList.size
    }

    fun updateData(newBukuList: List<Buku>) {
        this.bukuList = newBukuList
        notifyDataSetChanged()
    }
}