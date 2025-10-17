package com.example.neizlesem

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.neizlesem.adapter.MovieCardAdapter
import com.example.neizlesem.models.Genre
import com.example.neizlesem.models.Movie
import com.example.neizlesem.models.MovieResponse
import com.example.neizlesem.network.RetrofitClient
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.Direction
import com.yuyakaido.android.cardstackview.CardStackView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    // TODO: Güvenli saklama daha sonra. Şimdilik hızlı demo.
    private val API_KEY = "b380ba2a2f1a356c0f43d6bff6efd6cd"

    private lateinit var cardStackView: CardStackView
    private lateinit var manager: CardStackLayoutManager
    private lateinit var adapter: MovieCardAdapter
    private lateinit var tvStatus: TextView
    private lateinit var genreSpinner: Spinner

    private var genreList: List<Genre> = listOf()
    private var movies: List<Movie> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvStatus = findViewById(R.id.tvStatus)
        genreSpinner = findViewById(R.id.genreSpinner)
        cardStackView = findViewById(R.id.cardStackView)

        manager = CardStackLayoutManager(this, object : CardStackListener {
            override fun onCardDragging(direction: Direction?, ratio: Float) {}
            override fun onCardSwiped(direction: Direction?) {
                // Sağ veya sol kaydırma
                if (direction == Direction.Right) {
                    // Favorilere ekle (örnek)
                    val top = manager.topPosition - 1
                    if (top >= 0 && top < movies.size) {
                        val m = movies[top]
                        saveFavorite(m)
                        tvStatus.text = "${m.title} favorilere eklendi"
                    }
                } else {
                    tvStatus.text = "Geçildi"
                }
            }
            override fun onCardRewound() {}
            override fun onCardCanceled() {}
            override fun onCardAppeared(view: View?, position: Int) {}
            override fun onCardDisappeared(view: View?, position: Int) {}
        })

        adapter = MovieCardAdapter(movies)
        cardStackView.layoutManager = manager
        cardStackView.adapter = adapter

        // Türleri çek ve spinner'a koy
        fetchGenres()
    }

    private fun fetchGenres() {
        tvStatus.text = "Türler yükleniyor..."
        RetrofitClient.api.getGenres(API_KEY).enqueue(object : Callback<com.example.neizlesem.models.GenreResponse> {
            override fun onResponse(call: Call<com.example.neizlesem.models.GenreResponse>, response: Response<com.example.neizlesem.models.GenreResponse>) {
                if (response.isSuccessful) {
                    genreList = response.body()?.genres ?: listOf()
                    val names = mutableListOf<String>()
                    names.add("Popüler (Tümü)")
                    genreList.forEach { names.add(it.name) }
                    val adapterSpinner = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, names)
                    adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    genreSpinner.adapter = adapterSpinner
                    genreSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            if (position == 0) {
                                fetchPopular()
                            } else {
                                val genre = genreList[position - 1]
                                fetchByGenre(genre.id)
                            }
                        }
                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                    }
                    tvStatus.text = "Tür seçin"
                } else {
                    tvStatus.text = "Türler yüklenemedi"
                }
            }
            override fun onFailure(call: Call<com.example.neizlesem.models.GenreResponse>, t: Throwable) {
                tvStatus.text = "Tür yükleme hatası: ${t.message}"
            }
        })
    }

    private fun fetchPopular() {
        tvStatus.text = "Popüler filmler yükleniyor..."
        RetrofitClient.api.getPopular(API_KEY).enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                if (response.isSuccessful) {
                    movies = response.body()?.results ?: listOf()
                    updateCards(movies)
                } else {
                    tvStatus.text = "Popüler verisi alınamadı"
                }
            }
            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                tvStatus.text = "Hata: ${t.message}"
            }
        })
    }

    private fun fetchByGenre(genreId: Int) {
        tvStatus.text = "Filmler yükleniyor..."
        RetrofitClient.api.discoverByGenre(API_KEY, genreId).enqueue(object : Callback<MovieResponse> {
            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
                if (response.isSuccessful) {
                    movies = response.body()?.results ?: listOf()
                    updateCards(movies)
                } else {
                    tvStatus.text = "Tür verisi alınamadı"
                }
            }
            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                tvStatus.text = "Hata: ${t.message}"
            }
        })
    }

    private fun updateCards(list: List<Movie>) {
        if (list.isEmpty()) {
            tvStatus.text = "Bu türde film bulunamadı"
        } else {
            tvStatus.text = "Filmler yüklendi: ${list.size}"
        }
        adapter.updateList(list)
        // CardStackView ile yeniden bağla
        cardStackView.adapter = adapter
    }

    private fun saveFavorite(movie: Movie) {
        // Basit örnek: SharedPreferences'ta id'leri sakla
        val prefs = getSharedPreferences("neizlesem_prefs", MODE_PRIVATE)
        val set = prefs.getStringSet("favorites", mutableSetOf()) ?: mutableSetOf()
        set.add(movie.id.toString())
        prefs.edit().putStringSet("favorites", set).apply()
    }
}
