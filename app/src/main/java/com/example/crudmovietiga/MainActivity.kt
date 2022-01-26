package com.example.crudmovietiga

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.crudmovietiga.room.Constant
import com.example.crudmovietiga.room.Movie
import com.example.crudmovietiga.room.MovieDb
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.android.synthetic.main.list_movie.view.*
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    val db by lazy { MovieDb(this)}
    lateinit var movieAdapter: MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupView()
        setupListener()
        setupRecyclerview()
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun setupRecyclerview() {
        movieAdapter = MovieAdapter(
            arrayListOf(),
            object : MovieAdapter.OnAdapterListener {
                override fun onClick(movie: Movie) {
                    intentEdit(Constant.TYPE_READ, movie.id)
                }

                override fun onUpdate(movie: Movie) {
                    intentEdit(Constant.TYPE_UPDATE, movie.id)
                }

                override fun onDelete(movie: Movie) {
                    deleteAlert(movie)
                }

            })
        rv_movie.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = movieAdapter
        }
    }

    override fun onStart(){
        super.onStart()
        CoroutineScope(Dispatchers.IO).launch {
            val movies = db.movieDao().getMovies()
            Log.d("MainActivity", "dbresponse: $movies")
            withContext(Dispatchers.Main){
                movieAdapter.setData(movies)
            }

        }
    }

    private fun loadData(){
        CoroutineScope(Dispatchers.IO).launch {
            movieAdapter.setData(db.movieDao().getMovies())
            withContext(Dispatchers.Main) {
                movieAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun setupView (){
        supportActionBar!!.apply {
            title = "Movie"
        }
    }

    fun setupListener() {
        add_movie.setOnClickListener{
            intentEdit(Constant.TYPE_CREATE, 0)
        }
    }

    private fun intentEdit(intent_type: Int, movie_id: Int) {
        startActivity(
            Intent(this, AddActivity::class.java)
                .putExtra("intent_type", intent_type)
                .putExtra("movie_id", movie_id)
        )

    }

    private fun deleteAlert(movie: Movie){
        val dialog = AlertDialog.Builder(this)
        dialog.apply {
            setTitle("Konfirmasi Hapus")
            setMessage("Yakin hapus ${movie.title}?")
            setNegativeButton("Batal") { dialogInterface, i ->
                dialogInterface.dismiss()
            }
            setPositiveButton("Hapus") { dialogInterface, i ->
                CoroutineScope(Dispatchers.IO).launch {
                    db.movieDao().deleteMovie(movie)
                    dialogInterface.dismiss()
                    loadData()
                }
            }
        }

        dialog.show()
    }
}