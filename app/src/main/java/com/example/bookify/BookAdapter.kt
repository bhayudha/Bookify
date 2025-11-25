package com.example.bookify

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookify.model.Book


class BookAdapter(
    private val books: List<Book>
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    inner class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgBook = itemView.findViewById<ImageView>(R.id.imgBook)
        val tvTitle = itemView.findViewById<TextView>(R.id.tvTitle)
        val tvAuthor = itemView.findViewById<TextView>(R.id.tvAuthor)
        val tvRating = itemView.findViewById<TextView>(R.id.tvRating)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_book, parent, false)
        return BookViewHolder(v)
    }

    override fun getItemCount(): Int = books.size

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val b = books[position]

        Glide.with(holder.itemView.context)
            .load(b.imageUrl)
            .placeholder(R.drawable.ic_book_placeholder)
            .into(holder.imgBook)

        holder.tvTitle.text = b.title
        holder.tvAuthor.text = b.author
        holder.tvRating.text = "${b.rating} ★"
    }
}
