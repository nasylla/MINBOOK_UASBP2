package com.example.minbook.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PeminjamanDao {

    // Query baru untuk menggabungkan Peminjaman dan Buku
    @Query("""
        SELECT 
            p.pinjamId, p.namaPeminjam, p.tanggalPinjam, p.tanggalKembali, p.status, p.petugas, p.bukuId,
            b.judul, b.cover 
        FROM peminjaman p
        INNER JOIN buku b ON p.bukuId = b.id
        ORDER BY p.tanggalPinjam DESC
    """)
    fun getAllPeminjamanDetail(): Flow<List<PeminjamanDetail>>

    @Query("SELECT * FROM peminjaman ORDER BY tanggalPinjam DESC")
    fun getAllPeminjaman(): Flow<List<Peminjaman>>

    @Insert
    suspend fun insertPeminjaman(peminjaman: Peminjaman)

    @Update
    suspend fun updatePeminjaman(peminjaman: Peminjaman)

    @Delete
    suspend fun deletePeminjaman(peminjaman: Peminjaman)

}