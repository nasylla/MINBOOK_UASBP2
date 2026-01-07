package com.example.minbook.db

import kotlinx.coroutines.flow.Flow

class PeminjamanRepository(private val peminjamanDao: PeminjamanDao) {

    // Flow untuk mengambil detail peminjaman (hasil JOIN)
    val semuaPeminjamanDetail: Flow<List<PeminjamanDetail>> = peminjamanDao.getAllPeminjamanDetail()

    val semuaPeminjaman: Flow<List<Peminjaman>> = peminjamanDao.getAllPeminjaman()

    suspend fun insert(peminjaman: Peminjaman) {
        peminjamanDao.insertPeminjaman(peminjaman)
    }

    suspend fun update(peminjaman: Peminjaman) {
        peminjamanDao.updatePeminjaman(peminjaman)
    }

    suspend fun delete(peminjaman: Peminjaman) {
        peminjamanDao.deletePeminjaman(peminjaman)
    }
}