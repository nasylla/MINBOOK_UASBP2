package com.example.minbook

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.minbook.db.Peminjaman
import com.example.minbook.db.PeminjamanDetail
import com.example.minbook.db.PeminjamanRepository
import kotlinx.coroutines.launch

class PeminjamanViewModel(private val repository: PeminjamanRepository) : ViewModel() {

    // LiveData untuk mengambil detail peminjaman (hasil JOIN)
    val semuaPeminjamanDetail: LiveData<List<PeminjamanDetail>> = repository.semuaPeminjamanDetail.asLiveData()

    val semuaPeminjaman: LiveData<List<Peminjaman>> = repository.semuaPeminjaman.asLiveData()

    fun insert(peminjaman: Peminjaman) {
        viewModelScope.launch {
            repository.insert(peminjaman)
        }
    }

    fun update(peminjaman: Peminjaman) {
        viewModelScope.launch {
            repository.update(peminjaman)
        }
    }

    fun delete(peminjaman: Peminjaman) {
        viewModelScope.launch {
            repository.delete(peminjaman)
        }
    }
}

class PeminjamanViewModelFactory(private val repository: PeminjamanRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PeminjamanViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PeminjamanViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}