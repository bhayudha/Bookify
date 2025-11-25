package com.example.bookify

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class HomeActivity : AppCompatActivity() {

    private lateinit var rvBooks: RecyclerView
    private lateinit var edtSearch: EditText
    private lateinit var btnSearch: ImageView

    private val books = mutableListOf<Book>()
    private lateinit var adapter: BookAdapter

    private var startIndex = 0
    private var query = "novel"
    private val maxResults = 20

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        rvBooks = findViewById(R.id.rvBooks)
        edtSearch = findViewById(R.id.edtSearch)
        btnSearch = findViewById(R.id.btnSearch)

        adapter = BookAdapter(books)
        rvBooks.layoutManager = GridLayoutManager(this, 2)
        rvBooks.adapter = adapter

        btnSearch.setOnClickListener {
            query = edtSearch.text.toString().ifEmpty { "novel" }
            startIndex = 0
            books.clear()
            loadBooks()
        }

        loadBooks()
        setupPagination()
    }

    private fun loadBooks() {
        val url =
            "https://www.googleapis.com/books/v1/volumes?q=$query&startIndex=$startIndex&maxResults=$maxResults"

        val request = Request.Builder().url(url).build()
        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string() ?: return
                val json = JSONObject(body)

                val items = json.optJSONArray("items") ?: return
                for (i in 0 until items.length()) {
                    val item = items.getJSONObject(i)
                    val volume = item.getJSONObject("volumeInfo")

                    val book = Book(
                        title = volume.optString("title"),
                        author = volume.optJSONArray("authors")?.optString(0) ?: "Unknown",
                        rating = volume.optDouble("averageRating", 4.5),
                        imageUrl = volume.optJSONObject("imageLinks")?.optString("thumbnail") ?: ""
                    )
                    books.add(book)
                }

                runOnUiThread { adapter.notifyDataSetChanged() }
            }
        })
    }

    private fun setupPagination() {
        rvBooks.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                if (!rv.canScrollVertically(1)) {
                    startIndex += maxResults
                    loadBooks()
                }
            }
        })
    }
}
