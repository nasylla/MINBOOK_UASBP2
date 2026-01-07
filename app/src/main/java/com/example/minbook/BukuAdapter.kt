package com.example.minbook

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.minbook.db.Buku

class BukuAdapter(
    private var bukuList: List<Buku>,
    private val onDeleteClick: (Buku) -> Unit // Lambda untuk aksi hapus
) :
    RecyclerView.Adapter<BukuAdapter.BukuViewHolder>() {

    class BukuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvJudulBuku: TextView = view.findViewById(R.id.tvJudulBuku)
        val tvPenulis: TextView = view.findViewById(R.id.tvPenulis)
        val tvKategori: TextView = view.findViewById(R.id.tvKategori)
        val imgBuku: ImageView = view.findViewById(R.id.imgBuku)
        val btnEdit: ImageView = view.findViewById(R.id.btn_edit)
        val btnDelete: ImageView = view.findViewById(R.id.btn_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BukuViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_buku, parent, false)
        return BukuViewHolder(view)
    }

    override fun onBindViewHolder(holder: BukuViewHolder, position: Int) {
        val buku = bukuList[position]
        val context = holder.itemView.context

        holder.tvJudulBuku.text = buku.judul
        holder.tvPenulis.text = buku.penulis
        holder.tvKategori.text = "Kategori: ${buku.kategori}"

        Glide.with(context)
            .load(buku.cover)
            .placeholder(R.drawable.bg_card)
            .error(R.drawable.bg_card)
            .into(holder.imgBuku)

        // Listener untuk tombol edit
        holder.btnEdit.setOnClickListener {
            val intent = Intent(context, EditBukuActivity::class.java)
            intent.putExtra("buku", buku) // Mengirim objek Buku
            context.startActivity(intent)
        }

        // Listener untuk tombol hapus
        holder.btnDelete.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Konfirmasi Hapus")
                .setMessage("Apakah Anda yakin ingin menghapus buku '${buku.judul}'?")
                .setPositiveButton("Hapus") { _, _ ->
                    onDeleteClick(buku) // Memanggil lambda
                    Toast.makeText(context, "Buku berhasil dihapus", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Batal", null)
                .show()
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