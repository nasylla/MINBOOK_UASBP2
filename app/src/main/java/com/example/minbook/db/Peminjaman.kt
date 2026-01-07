package com.example.minbook.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "peminjaman")
data class Peminjaman(
    @PrimaryKey(autoGenerate = true)
    val pinjamId: Int = 0,
    val namaPeminjam: String,
    val bukuId: Int,
    val tanggalPinjam: String,
    val tanggalKembali: String,
    val petugas: String,
    val status: String
)