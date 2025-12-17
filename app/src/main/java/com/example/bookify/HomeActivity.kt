package com.example.bookify

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import com.example.bookify.model.Book
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.widget.Button
import android.widget.RadioButton
import android.content.Intent



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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            // padding kanan kiri manual 16dp
            val horizontalPadding = 16 * v.resources.displayMetrics.density
            val side = horizontalPadding.toInt()

            v.setPadding(
                systemBars.left + side,
                systemBars.top,
                systemBars.right + side,
                0
            )

            insets
        }
        val btnFilter = findViewById<ImageView>(R.id.btnFilter)

        btnFilter.setOnClickListener {
            showFilterDialog()
        }


        rvBooks = findViewById(R.id.rvBooks)
        edtSearch = findViewById(R.id.edtSearch)
        btnSearch = findViewById(R.id.btnSearch)

        adapter = BookAdapter(books) { book ->
            val intent = Intent(this, DetailBookActivity::class.java)
            intent.putExtra("BOOK_TITLE", book.title)
            intent.putExtra("BOOK_AUTHOR", book.author)
            intent.putExtra("BOOK_IMAGE", book.imageUrl)
            intent.putExtra("BOOK_DESC", book.description)
            intent.putExtra("BOOK_DATE", book.publishedDate)
            intent.putExtra("BOOK_PUBLISHER", book.publisher)
            intent.putExtra("BOOK_PAGE", book.pageCount)
            intent.putExtra("BOOK_LANG", book.language)
            startActivity(intent)
        }



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

                    // 🔥 FIX URL IMAGE (HTTP → HTTPS)
                    val imageUrlRaw =
                        volume.optJSONObject("imageLinks")?.optString("thumbnail") ?: ""

                    val imageUrl = imageUrlRaw.replace("http://", "https://")

                    val book = Book(
                        title = volume.optString("title"),
                        author = volume.optJSONArray("authors")?.optString(0) ?: "Unknown",
                        rating = volume.optDouble("averageRating", 4.5),
                        imageUrl = imageUrl, // ⬅️ PAKAI YANG SUDAH DIFIX
                        description = volume.optString("description", "Deskripsi tidak tersedia"),
                        publishedDate = volume.optString("publishedDate", "-"),
                        publisher = volume.optString("publisher", "-"),
                        pageCount = volume.optInt("pageCount", 0),
                        language = volume.optString("language", "-")
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

    private fun showFilterDialog() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_filter, null)
        dialog.setContentView(view)

        val rbRating = view.findViewById<RadioButton>(R.id.rbRating)
        val rbTitle = view.findViewById<RadioButton>(R.id.rbTitle)
        val btnApply = view.findViewById<Button>(R.id.btnApply)

        btnApply.setOnClickListener {
            when {
                rbRating.isChecked -> {
                    books.sortBy { book: Book ->
                        book.title
                    }
                }
                rbTitle.isChecked -> {
                    books.sortBy { book: Book ->
                        book.title
                    }
                }
            }
            adapter.notifyDataSetChanged()
            dialog.dismiss()
        }

        dialog.show()
    }

}
