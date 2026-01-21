package com.example.minbook

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.minbook.databinding.ItemBukuBinding
import com.example.minbook.db.Buku

class BukuAdapter(
    private var bukuList: List<Buku>,
    private val onDeleteClick: (Buku) -> Unit
) :
    RecyclerView.Adapter<BukuAdapter.BukuViewHolder>() {

    inner class BukuViewHolder(val binding: ItemBukuBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BukuViewHolder {
        val binding = ItemBukuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BukuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BukuViewHolder, position: Int) {
        val buku = bukuList[position]
        val context = holder.itemView.context

        with(holder.binding) {
            tvJudulBuku.text = buku.judul
            tvPenulis.text = buku.penulis
            tvKategori.text = "Kategori: ${buku.kategori}"

            Glide.with(context)
                .load(buku.cover)
                .placeholder(R.drawable.bg_card)
                .error(R.drawable.bg_card)
                .into(imgBuku)

            btnEdit.setOnClickListener {
                val intent = Intent(context, EditBukuActivity::class.java)
                intent.putExtra("buku", buku) // Mengirim objek Buku
                context.startActivity(intent)
            }

            btnDelete.setOnClickListener {
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
    }

    override fun getItemCount(): Int {
        return bukuList.size
    }

    fun updateData(newBukuList: List<Buku>) {
        this.bukuList = newBukuList
        notifyDataSetChanged()
    }
}