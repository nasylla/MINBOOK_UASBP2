package com.example.minbook.db

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PeminjamanDetail(
    // Kolom dari tabel Peminjaman
    val pinjamId: Int,
    val namaPeminjam: String,
    val tanggalPinjam: String,
    val tanggalKembali: String?,
    val status: String,
    val petugas: String,
    val bukuId: Int,

    // Kolom dari tabel Buku
    val judul: String,
    val cover: String
) : Parcelable
