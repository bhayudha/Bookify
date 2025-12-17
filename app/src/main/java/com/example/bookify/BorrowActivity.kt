package com.example.bookify

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.bookify.data.AppDatabase
import com.example.bookify.data.PeminjamanEntity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BorrowActivity : AppCompatActivity() {

    private lateinit var edtNama: TextInputEditText
    private lateinit var edtJudul: TextInputEditText
    private lateinit var edtTanggal: TextInputEditText
    private lateinit var edtEmail: TextInputEditText
    private lateinit var edtHp: TextInputEditText
    private lateinit var btnPinjam: MaterialButton

    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_borrow)

        // Inisialisasi database
        database = AppDatabase.getDatabase(this)

        // Inisialisasi views
        edtNama = findViewById(R.id.edtNama)
        edtJudul = findViewById(R.id.edtJudul)
        edtTanggal = findViewById(R.id.edtTanggal)
        edtEmail = findViewById(R.id.edtEmail)
        edtHp = findViewById(R.id.edtHp)
        btnPinjam = findViewById(R.id.btnPinjam)

        // Ambil judul buku dari intent (dari DetailActivity)
        val bookTitle = intent.getStringExtra("BOOK_TITLE") ?: ""
        edtJudul.setText(bookTitle)

        // Setup DatePicker untuk tanggal peminjaman
        setupDatePicker()

        // Handle button pinjam click
        btnPinjam.setOnClickListener {
            if (validateInput()) {
                simpanKeDatabase()
            }
        }
    }

    private fun setupDatePicker() {
        val calendar = Calendar.getInstance()

        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                edtTanggal.setText(format.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        // Tidak bisa pilih tanggal sebelum hari ini
        datePicker.datePicker.minDate = System.currentTimeMillis()

        edtTanggal.setOnClickListener {
            datePicker.show()
        }
    }

    private fun validateInput(): Boolean {
        val nama = edtNama.text.toString().trim()
        val tanggal = edtTanggal.text.toString().trim()
        val email = edtEmail.text.toString().trim()
        val hp = edtHp.text.toString().trim()

        when {
            nama.isEmpty() -> {
                edtNama.error = "Nama wajib diisi"
                edtNama.requestFocus()
                return false
            }

            tanggal.isEmpty() -> {
                edtTanggal.error = "Tanggal wajib dipilih"
                edtTanggal.requestFocus()
                return false
            }

            email.isEmpty() -> {
                edtEmail.error = "Email wajib diisi"
                edtEmail.requestFocus()
                return false
            }

            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                edtEmail.error = "Email tidak valid"
                edtEmail.requestFocus()
                return false
            }

            hp.isEmpty() -> {
                edtHp.error = "Nomor HP wajib diisi"
                edtHp.requestFocus()
                return false
            }

            !hp.matches(Regex("^08[0-9]{8,11}$")) -> {
                edtHp.error = "Nomor HP tidak valid (harus diawali 08)"
                edtHp.requestFocus()
                return false
            }
        }

        return true
    }

    private fun simpanKeDatabase() {
        val peminjaman = PeminjamanEntity(
            nama = edtNama.text.toString().trim(),
            judulBuku = edtJudul.text.toString().trim(),
            tanggalPinjam = edtTanggal.text.toString().trim(),
            email = edtEmail.text.toString().trim(),
            nomorHp = edtHp.text.toString().trim()
        )

        // Simpan ke database menggunakan coroutine
        lifecycleScope.launch {
            try {
                val insertedId = database.peminjamanDao().insertPeminjaman(peminjaman)

                // Setelah berhasil disimpan, pindah ke Invoice
                runOnUiThread {
                    Toast.makeText(
                        this@BorrowActivity,
                        "Data berhasil disimpan!",
                        Toast.LENGTH_SHORT
                    ).show()

                    pindahKeInvoice()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(
                        this@BorrowActivity,
                        "Gagal menyimpan data: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun pindahKeInvoice() {
        // PENTING: Key harus sama dengan yang di InvoiceActivity
        val intent = Intent(this, InvoiceActivity::class.java).apply {
            putExtra("NAMA", edtNama.text.toString().trim())
            putExtra("JUDUL", edtJudul.text.toString().trim())
            putExtra("TANGGAL", edtTanggal.text.toString().trim())
            putExtra("EMAIL", edtEmail.text.toString().trim())
            putExtra("HP", edtHp.text.toString().trim())
        }

        startActivity(intent)
        finish() // Tutup BorrowActivity setelah pindah ke Invoice
    }
}