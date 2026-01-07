package com.example.minbook.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Buku::class, Peminjaman::class, User::class], version = 2, exportSchema = false)
abstract class MinbookDatabase : RoomDatabase() {

    abstract fun bukuDao(): BukuDao
    abstract fun peminjamanDao(): PeminjamanDao // Diaktifkan
    // abstract fun userDao(): UserDao             // Akan ditambahkan nanti

    companion object {
        @Volatile
        private var INSTANCE: MinbookDatabase? = null

        fun getDatabase(context: Context): MinbookDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MinbookDatabase::class.java,
                    "minbook_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
