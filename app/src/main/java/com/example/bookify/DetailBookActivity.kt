package com.example.bookify

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class DetailBookActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_book)

        val imgBook = findViewById<ImageView>(R.id.imgBook)
        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        val tvAuthor = findViewById<TextView>(R.id.tvAuthor)
        val tvDesc = findViewById<TextView>(R.id.tvDesc)
        val tvDate = findViewById<TextView>(R.id.tvDate)
        val tvPublisher = findViewById<TextView>(R.id.tvPublisher)
        val tvPage = findViewById<TextView>(R.id.tvPage)
        val tvLang = findViewById<TextView>(R.id.tvLang)
        val btnBorrow = findViewById<Button>(R.id.btnBorrow)

        // Ambil data dari intent
        val bookTitle = intent.getStringExtra("BOOK_TITLE")
        val bookAuthor = intent.getStringExtra("BOOK_AUTHOR")
        val bookDesc = intent.getStringExtra("BOOK_DESC")
        val bookDate = intent.getStringExtra("BOOK_DATE")
        val bookPublisher = intent.getStringExtra("BOOK_PUBLISHER")
        val bookPage = intent.getIntExtra("BOOK_PAGE", 0)
        val bookLang = intent.getStringExtra("BOOK_LANG")
        val imageUrl = intent.getStringExtra("BOOK_IMAGE")

        // Set ke view
        tvTitle.text = bookTitle
        tvAuthor.text = bookAuthor
        tvDesc.text = bookDesc
        tvDate.text = bookDate
        tvPublisher.text = bookPublisher
        tvPage.text = "Jumlah halaman: $bookPage"
        tvLang.text = "Bahasa: $bookLang"

        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.ic_book_placeholder)
            .error(R.drawable.ic_book_placeholder)
            .into(imgBook)

        // Tombol Pinjam
        btnBorrow.setOnClickListener {
            val borrowIntent = Intent(this, BorrowActivity::class.java)
            borrowIntent.putExtra("BOOK_TITLE", bookTitle)
            startActivity(borrowIntent)
        }
    }
}
