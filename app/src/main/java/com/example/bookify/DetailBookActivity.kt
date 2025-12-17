package com.example.bookify

import android.os.Bundle
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
        val imageUrl = intent.getStringExtra("BOOK_IMAGE")

        tvTitle.text = intent.getStringExtra("BOOK_TITLE")
        tvAuthor.text = intent.getStringExtra("BOOK_AUTHOR")
        tvDesc.text = intent.getStringExtra("BOOK_DESC")
        tvDate.text = intent.getStringExtra("BOOK_DATE")
        tvPublisher.text = intent.getStringExtra("BOOK_PUBLISHER")
        tvPage.text = "Jumlah halaman: ${intent.getIntExtra("BOOK_PAGE", 0)}"
        tvLang.text = "Bahasa: ${intent.getStringExtra("BOOK_LANG")}"

        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.ic_book_placeholder) // optional
            .error(R.drawable.ic_book_placeholder)       // optional
            .into(imgBook)
    }
}



