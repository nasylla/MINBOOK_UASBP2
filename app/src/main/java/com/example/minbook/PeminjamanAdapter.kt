package com.example.minbook

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.minbook.db.PeminjamanDetail

class PeminjamanAdapter(
    private var peminjamanList: List<PeminjamanDetail>,
    private val onEditClick: (PeminjamanDetail) -> Unit,
    private val onDeleteClick: (PeminjamanDetail) -> Unit
) : RecyclerView.Adapter<PeminjamanAdapter.PeminjamanViewHolder>() {

    class PeminjamanViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgBuku: ImageView = view.findViewById(R.id.imgBuku)
        val tvJudulBuku: TextView = view.findViewById(R.id.tvJudulBuku)
        val tvNamaPeminjam: TextView = view.findViewById(R.id.tvNamaPeminjam)
        val tvTanggal: TextView = view.findViewById(R.id.tvTanggal)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val btnEdit: ImageView = view.findViewById(R.id.btn_edit)
        val btnDelete: ImageView = view.findViewById(R.id.btn_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeminjamanViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_peminjaman, parent, false)
        return PeminjamanViewHolder(view)
    }

    override fun onBindViewHolder(holder: PeminjamanViewHolder, position: Int) {
        val peminjaman = peminjamanList[position]
        val context = holder.itemView.context

        holder.tvJudulBuku.text = peminjaman.judul
        holder.tvNamaPeminjam.text = "Dipinjam oleh: ${peminjaman.namaPeminjam}"
        holder.tvTanggal.text = "Tgl Pinjam: ${peminjaman.tanggalPinjam}"
        holder.tvStatus.text = peminjaman.status

        Glide.with(context)
            .load(peminjaman.cover)
            .placeholder(R.drawable.bg_card)
            .into(holder.imgBuku)

        if (peminjaman.status.equals("Kembali", ignoreCase = true)) {
            holder.tvStatus.background = ContextCompat.getDrawable(context, R.drawable.bg_button_red)
        } else {
            holder.tvStatus.background = ContextCompat.getDrawable(context, R.drawable.bg_button_green)
        }

        holder.btnEdit.setOnClickListener { onEditClick(peminjaman) }
        holder.btnDelete.setOnClickListener { onDeleteClick(peminjaman) }
    }

    override fun getItemCount(): Int {
        return peminjamanList.size
    }

    fun updateData(newList: List<PeminjamanDetail>) {
        this.peminjamanList = newList
        notifyDataSetChanged()
    }
}