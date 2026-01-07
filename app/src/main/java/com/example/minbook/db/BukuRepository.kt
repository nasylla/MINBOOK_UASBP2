package com.example.minbook.db

import kotlinx.coroutines.flow.Flow

class BukuRepository(private val bukuDao: BukuDao) {

    // Mengambil semua buku dari DAO
    val semuaBuku: Flow<List<Buku>> = bukuDao.getAllBuku()

    // Memanggil fungsi suspend dari DAO untuk menyisipkan buku
    suspend fun insert(buku: Buku) {
        bukuDao.insertBuku(buku)
    }

    // Memanggil fungsi suspend dari DAO untuk memperbarui buku
    suspend fun update(buku: Buku) {
        bukuDao.updateBuku(buku)
    }

    // Memanggil fungsi suspend dari DAO untuk menghapus buku
    suspend fun delete(buku: Buku) {
        bukuDao.deleteBuku(buku)
    }
}
