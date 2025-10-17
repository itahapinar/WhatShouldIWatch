package com.example.neizlesem.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.neizlesem.R
import com.example.neizlesem.models.Movie

class MovieCardAdapter(
    private var movies: List<Movie>
) : RecyclerView.Adapter<MovieCardAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivPoster: ImageView = itemView.findViewById(R.id.ivPoster)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvRating: TextView = itemView.findViewById(R.id.tvRating)
        val tvOverview: TextView = itemView.findViewById(R.id.tvOverview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card_movie, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = movies[position]

        // 🔹 Başlık ve açıklama
        holder.tvTitle.text = movie.title ?: "Başlıksız"
        holder.tvRating.text = "★ ${(movie.vote_average ?: 0.0)}"
        holder.tvOverview.text = movie.overview ?: ""

        // 🔹 Poster resmi (The Movie DB API formatı)
        val imageUrl = "https://image.tmdb.org/t/p/w500" + (movie.poster_path ?: "")
        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .placeholder(R.drawable.ic_placeholder) // yoksa varsayılan resim
            .into(holder.ivPoster)
    }

    override fun getItemCount(): Int = movies.size

    fun updateList(newMovies: List<Movie>) {
        movies = newMovies
        notifyDataSetChanged()
    }
}
