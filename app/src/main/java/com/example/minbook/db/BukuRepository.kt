package com.example.minbook.db

import kotlinx.coroutines.flow.Flow

class BukuRepository(private val bukuDao: BukuDao) {

    val semuaBuku: Flow<List<Buku>> = bukuDao.getAllBuku()

    suspend fun insert(buku: Buku) {
        bukuDao.insertBuku(buku)
    }

    suspend fun update(buku: Buku) {
        bukuDao.updateBuku(buku)
    }

    suspend fun delete(buku: Buku) {
        bukuDao.deleteBuku(buku)
    }

    fun searchBuku(query: String): Flow<List<Buku>> {
        return bukuDao.searchBuku(query)
    }
}
