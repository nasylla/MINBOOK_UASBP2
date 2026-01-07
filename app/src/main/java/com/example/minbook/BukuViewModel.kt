package com.example.minbook

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.minbook.db.Buku
import com.example.minbook.db.BukuRepository
import kotlinx.coroutines.launch

class BukuViewModel(private val repository: BukuRepository) : ViewModel() {

    val semuaBuku: LiveData<List<Buku>> = repository.semuaBuku.asLiveData()

    // Diperbaiki: Fungsi sekarang secara eksplisit mengembalikan Unit
    fun insert(buku: Buku) {
        viewModelScope.launch {
            repository.insert(buku)
        }
    }

    // Diperbaiki: Fungsi sekarang secara eksplisit mengembalikan Unit
    fun update(buku: Buku) {
        viewModelScope.launch {
            repository.update(buku)
        }
    }

    // Diperbaiki: Fungsi sekarang secara eksplisit mengembalikan Unit
    fun delete(buku: Buku) {
        viewModelScope.launch {
            repository.delete(buku)
        }
    }
}

class BukuViewModelFactory(private val repository: BukuRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BukuViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BukuViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}