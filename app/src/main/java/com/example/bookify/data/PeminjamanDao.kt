package com.example.bookify.data

import androidx.room.*

@Dao
interface PeminjamanDao {

    @Insert
    suspend fun insertPeminjaman(peminjaman: PeminjamanEntity): Long

    @Query("SELECT * FROM peminjaman ORDER BY timestamp DESC")
    suspend fun getAllPeminjaman(): List<PeminjamanEntity>

    @Query("SELECT * FROM peminjaman WHERE id = :id")
    suspend fun getPeminjamanById(id: Int): PeminjamanEntity?

    @Delete
    suspend fun deletePeminjaman(peminjaman: PeminjamanEntity)

    @Query("DELETE FROM peminjaman")
    suspend fun deleteAllPeminjaman()
}