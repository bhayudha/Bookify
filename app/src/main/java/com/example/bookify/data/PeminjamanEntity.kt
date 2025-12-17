package com.example.bookify.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "peminjaman")
data class PeminjamanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val nama: String,
    val judulBuku: String,
    val tanggalPinjam: String,
    val email: String,
    val nomorHp: String,
    val timestamp: Long = System.currentTimeMillis()
)