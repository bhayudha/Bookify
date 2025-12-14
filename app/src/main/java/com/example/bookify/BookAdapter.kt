package com.example.bookify

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookify.model.Book

class BookAdapter(private val books: List<Book>) :
    RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    inner class BookViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgBook: ImageView = view.findViewById(R.id.imgBook)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvAuthor: TextView = view.findViewById(R.id.tvAuthor)
        val tvRating: TextView = view.findViewById(R.id.tvRating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_book, parent, false)
        return BookViewHolder(view)
    }

    override fun getItemCount() = books.size

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]

        holder.tvTitle.text = book.title
        holder.tvAuthor.text = book.author
        holder.tvRating.text = "${book.rating} ★"

        Glide.with(holder.itemView.context)
            .load(book.imageUrl)
            .centerCrop()
            .into(holder.imgBook)
    }
}
