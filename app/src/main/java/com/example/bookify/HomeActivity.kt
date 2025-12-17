package com.example.bookify

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
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
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat

class HomeActivity : AppCompatActivity() {

    private lateinit var rvBooks: RecyclerView
    private lateinit var edtSearch: EditText
    private lateinit var btnSearch: ImageView
    private lateinit var btnFilter: ImageView
    private lateinit var btnPrevious: ImageView
    private lateinit var btnNext: ImageView
    private lateinit var tvPageInfo: TextView
    private lateinit var tvPage1: TextView
    private lateinit var tvPage2: TextView
    private lateinit var tvPage3: TextView
    private lateinit var tvPageDots: TextView

    private val allBooks = mutableListOf<Book>()
    private val displayBooks = mutableListOf<Book>()
    private lateinit var adapter: BookAdapter

    private var currentPage = 1
    private val booksPerPage = 15 // 3 kolom x 5 baris
    private var totalPages = 1

    private var query = "novel"
    private val maxResults = 40

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Inisialisasi views
        rvBooks = findViewById(R.id.rvBooks)
        edtSearch = findViewById(R.id.edtSearch)
        btnSearch = findViewById(R.id.btnSearch)
        btnFilter = findViewById(R.id.btnFilter)
        btnPrevious = findViewById(R.id.btnPrevious)
        btnNext = findViewById(R.id.btnNext)
        tvPageInfo = findViewById(R.id.tvPageInfo)
        tvPage1 = findViewById(R.id.tvPage1)
        tvPage2 = findViewById(R.id.tvPage2)
        tvPage3 = findViewById(R.id.tvPage3)
        tvPageDots = findViewById(R.id.tvPageDots)

        // Setup window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        // Setup adapter
        adapter = BookAdapter(displayBooks) { book ->
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

        rvBooks.layoutManager = GridLayoutManager(this, 3)
        rvBooks.adapter = adapter
        rvBooks.setHasFixedSize(true)

        // Search listener
        btnSearch.setOnClickListener {
            query = edtSearch.text.toString().ifEmpty { "novel" }
            currentPage = 1
            allBooks.clear()
            displayBooks.clear()
            loadBooks()
        }

        // Filter listener
        btnFilter.setOnClickListener {
            showFilterDialog()
        }

        // Pagination listeners
        btnPrevious.setOnClickListener {
            if (currentPage > 1) {
                currentPage--
                updateDisplayedBooks()
                rvBooks.smoothScrollToPosition(0)
            }
        }

        btnNext.setOnClickListener {
            if (currentPage < totalPages) {
                currentPage++
                updateDisplayedBooks()
                rvBooks.smoothScrollToPosition(0)
            } else {
                loadBooks()
            }
        }

        // Page number click listeners
        tvPage1.setOnClickListener { goToPage(1) }
        tvPage2.setOnClickListener { goToPage(2) }
        tvPage3.setOnClickListener { goToPage(3) }

        loadBooks()
    }

    private fun goToPage(page: Int) {
        if (page <= totalPages) {
            currentPage = page
            updateDisplayedBooks()
            rvBooks.smoothScrollToPosition(0)
        }
    }

    private fun loadBooks() {
        val startIndex = allBooks.size
        val url = "https://www.googleapis.com/books/v1/volumes?q=$query&startIndex=$startIndex&maxResults=$maxResults"

        val request = Request.Builder().url(url).build()
        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("HomeActivity", "Failed to load books", e)
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string() ?: return
                val json = JSONObject(body)

                val items = json.optJSONArray("items") ?: return
                for (i in 0 until items.length()) {
                    val item = items.getJSONObject(i)
                    val volume = item.getJSONObject("volumeInfo")

                    val imageUrlRaw = volume.optJSONObject("imageLinks")?.optString("thumbnail") ?: ""
                    val imageUrl = imageUrlRaw.replace("http://", "https://")

                    val book = Book(
                        title = volume.optString("title"),
                        author = volume.optJSONArray("authors")?.optString(0) ?: "Unknown",
                        rating = volume.optDouble("averageRating", 4.5),
                        imageUrl = imageUrl,
                        description = volume.optString("description", "Deskripsi tidak tersedia"),
                        publishedDate = volume.optString("publishedDate", "-"),
                        publisher = volume.optString("publisher", "-"),
                        pageCount = volume.optInt("pageCount", 0),
                        language = volume.optString("language", "-")
                    )

                    allBooks.add(book)
                }

                runOnUiThread {
                    updateDisplayedBooks()
                }
            }
        })
    }

    private fun updateDisplayedBooks() {
        totalPages = (allBooks.size + booksPerPage - 1) / booksPerPage

        val startIndex = (currentPage - 1) * booksPerPage
        val endIndex = minOf(startIndex + booksPerPage, allBooks.size)

        displayBooks.clear()
        if (startIndex < allBooks.size) {
            displayBooks.addAll(allBooks.subList(startIndex, endIndex))
        }

        adapter.notifyDataSetChanged()

        // Update page info
        tvPageInfo.text = "Hal $currentPage dari $totalPages"

        // Update page numbers
        updatePageNumbers()

        // Update button states
        btnPrevious.isEnabled = currentPage > 1
        btnPrevious.alpha = if (currentPage > 1) 1.0f else 0.3f

        btnNext.isEnabled = currentPage < totalPages || allBooks.size % maxResults == 0
        btnNext.alpha = if (btnNext.isEnabled) 1.0f else 0.3f
    }

    private fun updatePageNumbers() {
        val bgActive = ContextCompat.getDrawable(this, R.drawable.bg_page_active)
        val bgInactive = ContextCompat.getDrawable(this, R.drawable.bg_page_inactive)

        // Reset all pages
        tvPage1.background = bgInactive
        tvPage1.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray))
        tvPage2.background = bgInactive
        tvPage2.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray))
        tvPage3.background = bgInactive
        tvPage3.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray))

        // Update based on current page
        when (currentPage) {
            1 -> {
                tvPage1.background = bgActive
                tvPage1.setTextColor(ContextCompat.getColor(this, android.R.color.white))
                tvPage1.text = "1"
                tvPage2.text = "2"
                tvPage3.text = "3"
            }
            2 -> {
                tvPage2.background = bgActive
                tvPage2.setTextColor(ContextCompat.getColor(this, android.R.color.white))
                tvPage1.text = "1"
                tvPage2.text = "2"
                tvPage3.text = "3"
            }
            3 -> {
                tvPage3.background = bgActive
                tvPage3.setTextColor(ContextCompat.getColor(this, android.R.color.white))
                tvPage1.text = "1"
                tvPage2.text = "2"
                tvPage3.text = "3"
            }
            else -> {
                tvPage2.background = bgActive
                tvPage2.setTextColor(ContextCompat.getColor(this, android.R.color.white))
                tvPage1.text = (currentPage - 1).toString()
                tvPage2.text = currentPage.toString()
                tvPage3.text = (currentPage + 1).toString()
            }
        }

        // Show dots if more pages
        tvPageDots.visibility = if (totalPages > 3) android.view.View.VISIBLE else android.view.View.GONE
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
                rbRating.isChecked -> allBooks.sortByDescending { it.rating }
                rbTitle.isChecked -> allBooks.sortBy { it.title }
            }
            currentPage = 1
            updateDisplayedBooks()
            dialog.dismiss()
        }

        dialog.show()
    }
}