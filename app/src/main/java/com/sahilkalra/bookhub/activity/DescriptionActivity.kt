package com.sahilkalra.bookhub.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.sahilkalra.bookhub.R
import com.sahilkalra.bookhub.database.BookDatabase
import com.sahilkalra.bookhub.database.BookEntity
import com.sahilkalra.bookhub.util.ConnectionManager
import com.squareup.picasso.Picasso
import com.squareup.picasso.Request
import org.json.JSONObject
import java.lang.Exception
import java.sql.Connection

class DescriptionActivity : AppCompatActivity() {
    lateinit var txtBookName:TextView
    lateinit var txtBookAuthor:TextView
    lateinit var txtBookPrice:TextView
    lateinit var txtBookRating:TextView
    lateinit var imgBookImage:ImageView
    lateinit var txtBookDesc:TextView
    lateinit var btnAddToFav:Button
     lateinit var progressBar:ProgressBar
    lateinit var progressLayout:RelativeLayout
    lateinit var toolbar: Toolbar
    var bookId: String?="100"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)

        txtBookName = findViewById(R.id.txtBookName)
        txtBookAuthor = findViewById(R.id.txtBookAuthor)
        txtBookPrice = findViewById(R.id.txtBookPrice)
        txtBookRating = findViewById(R.id.txtBookRating)
        imgBookImage = findViewById(R.id.imgBookImage)
        txtBookDesc = findViewById(R.id.txtBookDesc)
        btnAddToFav = findViewById(R.id.btnAddToFav)
        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.VISIBLE
        progressLayout = findViewById(R.id.progressLayout)
        progressLayout.visibility = View.VISIBLE
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Book Details"
        if (intent != null) {
            bookId = intent.getStringExtra("book_id")
        } else {
            finish()
            Toast.makeText(
                this@DescriptionActivity,
                "Some Unexpected error occurred",
                Toast.LENGTH_LONG
            ).show()
        }
        if (bookId == "100") {
            finish()
            Toast.makeText(
                this@DescriptionActivity,
                "Some unexpected error occurred",
                Toast.LENGTH_LONG
            ).show()
        }
        val queue = Volley.newRequestQueue(this@DescriptionActivity)
        val url = "http://13.235.250.119/v1/book/get_book/"
        val jsonParams = JSONObject()
        jsonParams.put("book_id", bookId)
        if (ConnectionManager().checkConnectivity(this@DescriptionActivity)){
        val jsonRequest =
            object : JsonObjectRequest(Method.POST, url, jsonParams, Response.Listener {
                try {
                    val success = it.getBoolean("success")
                    if (success) {
                        val bookJsonObject = it.getJSONObject("book_data")
                        progressLayout.visibility = View.GONE
                        val bookImageUrl=bookJsonObject.getString("image")
                        Picasso.get().load(bookJsonObject.getString("image"))
                            .error(R.drawable.default_book_cover).into(imgBookImage)
                        txtBookName.text = bookJsonObject.getString("name")
                        txtBookAuthor.text = bookJsonObject.getString("author")
                        txtBookPrice.text = bookJsonObject.getString("price")
                        txtBookRating.text = bookJsonObject.getString("rating")
                        txtBookDesc.text = bookJsonObject.getString("description")
                        val bookEntity=BookEntity(
                            bookId?.toInt() as Int,
                            txtBookName.text.toString(),
                            txtBookAuthor.text.toString(),
                            txtBookPrice.text.toString(),
                            txtBookRating.text.toString(),
                            txtBookDesc.text.toString(),
                            bookImageUrl
                        )
                        val checkFav=DBAsyncTask(applicationContext,bookEntity,1).execute()
                        val isFav=checkFav.get()
                        if (isFav){
                            btnAddToFav.text="Remove from Favourites"
                            val favColor=ContextCompat.getColor(applicationContext,R.color.colorFavourite)
                            btnAddToFav.setBackgroundColor(favColor)
                        }else{
                            btnAddToFav.text="Add to Favourites"
                            val noFavColor=ContextCompat.getColor(applicationContext,R.color.colorPrimary)
                            btnAddToFav.setBackgroundColor(noFavColor)
                        }
                        btnAddToFav.setOnClickListener {
                            if (!DBAsyncTask(applicationContext,bookEntity,1).execute().get()){
                                val async=DBAsyncTask(applicationContext,bookEntity,2).execute()
                                val result=async.get()
                                if (result){
                                    Toast.makeText(
                                        this@DescriptionActivity,"Book added to favourites",Toast.LENGTH_SHORT
                                    ).show()
                                    btnAddToFav.text="Remove from favourites"
                                    val favColor=ContextCompat.getColor(applicationContext,R.color.colorFavourite)
                                    btnAddToFav.setBackgroundColor(favColor)
                                }else{
                                    Toast.makeText(this@DescriptionActivity,"Some error occurred!!",Toast.LENGTH_SHORT).show()
                                }
                            }else{
                                val async=DBAsyncTask(applicationContext,bookEntity,3).execute()
                                val result=async.get()
                                if (result) {
                                    Toast.makeText(
                                        this@DescriptionActivity,
                                        "Book removed from favourites",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    btnAddToFav.text = "Add to favourites"
                                    val noFavColor = ContextCompat.getColor(
                                        applicationContext,
                                        R.color.colorPrimary
                                    )
                                    btnAddToFav.setBackgroundColor(noFavColor)
                                }else{
                                    Toast.makeText(this@DescriptionActivity,"Some error occurred!!",Toast.LENGTH_SHORT).show()
                                }
                            }
                        }

                    } else {
                        Toast.makeText(
                            this@DescriptionActivity,
                            "Some error has occurred",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        this@DescriptionActivity,
                        "Some error occurred",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }, Response.ErrorListener {
                Toast.makeText(this@DescriptionActivity, "Volley Error $it", Toast.LENGTH_SHORT)
                    .show()

            }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "0e49b3ccb148ea"
                    return headers
                }
            }
        queue.add(jsonRequest)
    }else{
            val dialog= AlertDialog.Builder(this@DescriptionActivity)
            dialog.setTitle("error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Settings"){
                    text, listener ->
                val settingsIntent= Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                finish()
                // do nothing
            }
            dialog.setNegativeButton("Exit"){text, listener->
                //do nothing
                ActivityCompat.finishAffinity(this@DescriptionActivity)
            }
            dialog.create()
            dialog.show()
        }

    }
    class DBAsyncTask(val context: Context, val bookEntity: BookEntity, val mode:Int):AsyncTask<Void, Void, Boolean>() {
       /* mode 1-> check the book is added to the favourites or not
          mode 2-> Add the book to the favourites
          mode 3-> Remove the book from favourites
        */
        val db= Room.databaseBuilder(context,BookDatabase::class.java,"books-db").build()
        override fun doInBackground(vararg params: Void?): Boolean {
           when(mode){
               1->{
                   //Check DB if the book is favourite or not
                   val book: BookEntity?=db.bookDao().getBookById(bookEntity.book_id.toString())
                   db.close()
                   return book!=null
               }
               2->{
                   //save the book into DB as favourite
                   db.bookDao().insertBook(bookEntity)
                   db.close()
                   return true
               }
               3->{
                   //Remove the favourite book
                   db.bookDao().deleteBook(bookEntity)
                   db.close()
                   return true
               }
           }
            return false
        }

    }
}