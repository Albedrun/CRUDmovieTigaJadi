package com.example.crudmovietiga

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.crudmovietiga.R
import com.example.crudmovietiga.room.Constant
import com.example.crudmovietiga.room.Movie
import com.example.crudmovietiga.room.MovieDb
import kotlinx.android.synthetic.main.activity_add.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddActivity : AppCompatActivity() {

    val db by lazy {MovieDb(this) }
    private var movieId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)
        setupView()
        setupListener()

    }

    private fun setupView(){
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        when (intentType()) {
            Constant.TYPE_CREATE -> {
                supportActionBar!!.title = "BUAT BARU"
                btn_save.visibility = View.VISIBLE
                button_update.visibility = View.GONE
            }
            Constant.TYPE_READ -> {
                supportActionBar!!.title = "BACA"
                btn_save.visibility = View.GONE
                button_update.visibility = View.GONE
                getMovie()
            }
            Constant.TYPE_UPDATE -> {
                supportActionBar!!.title = "EDIT"
                btn_save.visibility = View.GONE
                button_update.visibility = View.VISIBLE
                getMovie()
            }
        }
    }

    fun setupListener() {
        btn_save.setOnClickListener{
            CoroutineScope(Dispatchers.IO).launch{
                db.movieDao().addMovie(
                    Movie( 0, et_title.text.toString(),
                        et_description.text.toString())
                )

                finish()
            }
        }
        button_update.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                db.movieDao().updateMovie(
                    Movie(
                        movieId,
                        et_title.text.toString(),
                        et_description.text.toString()
                    )
                )
                finish()
            }
        }
    }

    private fun getMovie(){
        movieId = intent.getIntExtra("movie_id", 0)
        CoroutineScope(Dispatchers.IO).launch {
            val movies = db.movieDao().getMovie(movieId).get(0)
            et_title.setText(movies.title)
            et_description.setText(movies.desc)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    private fun intentType(): Int {
        return intent.getIntExtra("intent_type", 0)
    }
}