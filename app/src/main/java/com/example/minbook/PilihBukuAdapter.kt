package com.example.minbook

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.minbook.db.Buku

class PilihBukuAdapter(
    private var bukuList: List<Buku>,
    private val onBukuSelected: (Buku) -> Unit
) : RecyclerView.Adapter<PilihBukuAdapter.PilihBukuViewHolder>() {

    class PilihBukuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvJudulBuku: TextView = view.findViewById(R.id.tvJudulBuku)
        val tvPenulis: TextView = view.findViewById(R.id.tvPenulis)
        val tvKategori: TextView = view.findViewById(R.id.tvKategori)
        val imgBuku: ImageView = view.findViewById(R.id.imgBuku)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PilihBukuViewHolder {
        // Menggunakan layout item_buku yang sudah ada
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_buku, parent, false)
        // Menghilangkan tombol edit/delete agar tidak muncul di halaman pemilihan
        view.findViewById<View>(R.id.btn_edit).visibility = View.GONE
        view.findViewById<View>(R.id.btn_delete).visibility = View.GONE
        return PilihBukuViewHolder(view)
    }

    override fun onBindViewHolder(holder: PilihBukuViewHolder, position: Int) {
        val buku = bukuList[position]

        holder.tvJudulBuku.text = buku.judul
        holder.tvPenulis.text = buku.penulis
        holder.tvKategori.text = "Kategori: ${buku.kategori}"

        Glide.with(holder.itemView.context)
            .load(buku.cover)
            .placeholder(R.drawable.bg_card)
            .into(holder.imgBuku)

        holder.itemView.setOnClickListener {
            onBukuSelected(buku)
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