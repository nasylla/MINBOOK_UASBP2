package com.example.minbook

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.minbook.databinding.ItemPeminjamanBinding
import com.example.minbook.db.PeminjamanDetail

class PeminjamanAdapter(
    private var peminjamanList: List<PeminjamanDetail>,
    private val onEditClick: (PeminjamanDetail) -> Unit,
    private val onDeleteClick: (PeminjamanDetail) -> Unit
) : RecyclerView.Adapter<PeminjamanAdapter.PeminjamanViewHolder>() {

    inner class PeminjamanViewHolder(val binding: ItemPeminjamanBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeminjamanViewHolder {
        val binding = ItemPeminjamanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PeminjamanViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PeminjamanViewHolder, position: Int) {
        val peminjaman = peminjamanList[position]
        val context = holder.itemView.context

        with(holder.binding) {
            tvJudulBuku.text = peminjaman.judul
            tvNamaPeminjam.text = "Dipinjam oleh: ${peminjaman.namaPeminjam}"
            tvTanggal.text = "Tgl Pinjam: ${peminjaman.tanggalPinjam}"
            tvStatus.text = peminjaman.status

            Glide.with(context)
                .load(peminjaman.cover)
                .placeholder(R.drawable.bg_card)
                .into(imgBuku)

            if (peminjaman.status.equals("Kembali", ignoreCase = true)) {
                tvStatus.background = ContextCompat.getDrawable(context, R.drawable.bg_button_red)
            } else {
                tvStatus.background = ContextCompat.getDrawable(context, R.drawable.bg_button_green)
            }

            btnEdit.setOnClickListener { onEditClick(peminjaman) }
            btnDelete.setOnClickListener { onDeleteClick(peminjaman) }
        }
    }

    override fun getItemCount(): Int {
        return peminjamanList.size
    }

    fun updateData(newList: List<PeminjamanDetail>) {
        this.peminjamanList = newList
        notifyDataSetChanged()
    }
}