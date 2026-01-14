package com.example.minbook.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BukuDao {

    @Query("SELECT * FROM buku ORDER BY judul ASC")
    fun getAllBuku(): Flow<List<Buku>>

    @Query("SELECT COUNT(*) FROM buku")
    fun getBukuCount(): LiveData<Int>

    @Insert
    suspend fun insertBuku(buku: Buku)

    @Update
    suspend fun updateBuku(buku: Buku)

    @Delete
    suspend fun deleteBuku(buku: Buku)

}