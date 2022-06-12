package hr.tvz.android.animetracker.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

import hr.tvz.android.animetracker.databinding.FragmentAnimeItemBinding
import hr.tvz.android.animetracker.model.Anime
import hr.tvz.android.animetracker.presenter.Api
import hr.tvz.android.animetracker.view.HomeFragment

class AnimeRecyclerViewAdapter(private val animeList: List<Anime>, private val listener: HomeFragment.ItemClickListener) : RecyclerView.Adapter<AnimeRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(FragmentAnimeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = animeList[position]
        holder.itemTitle.text = item.title

        val url = Api.BASE_URL + "pictures/" + item.image + ".png"
        Picasso.get().load(url).into(holder.itemImage)

        holder.itemView.setOnClickListener {
            listener.onClick(animeList[position])
        }
    }

    override fun getItemCount(): Int = animeList.size

    inner class ViewHolder(binding: FragmentAnimeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val itemImage: ImageView = binding.animeItemImage
        val itemTitle: TextView = binding.animeItemTitle

        override fun toString(): String {
            return super.toString() + " '" + itemTitle.text + "'"
        }
    }
}