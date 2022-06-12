package hr.tvz.android.animetracker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import hr.tvz.android.animetracker.databinding.FragmentLibraryItemBinding
import hr.tvz.android.animetracker.model.Anime
import hr.tvz.android.animetracker.presenter.Api
import hr.tvz.android.animetracker.view.LibraryFragment

class LibraryRecyclerViewAdapter(private val animeList: List<Pair<Anime, Int>>,
                                 private val episodesWatchedListener: LibraryFragment.EpisodesWatchedListener,
                                 private val removeFromLibraryListener: LibraryFragment.RemoveFromLibraryListener
                                 ) : RecyclerView.Adapter<LibraryRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryRecyclerViewAdapter.ViewHolder {
        return ViewHolder(FragmentLibraryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: LibraryRecyclerViewAdapter.ViewHolder, position: Int) {
        val item = animeList[position]
        holder.itemTitle.text = item.first.title
        holder.itemStudio.text = item.first.studio
        holder.itemGenres.text = item.first.genres
        holder.itemAiredDate.text = item.first.airedDate
        holder.itemEpisodes.text = item.first.episodes.toString()
        holder.itemEpisodesWatched.text = item.second.toString()

        val url = Api.BASE_URL + "pictures/" + item.first.image + ".png"
        Picasso.get().load(url).into(holder.itemImage)

        holder.itemAddButton.setOnClickListener {
            val count = holder.itemEpisodesWatched.text.toString().toInt() + 1
            if(count <= item.first.episodes!!) {
                episodesWatchedListener.onClick(animeList[position].first, count, false)
                holder.itemEpisodesWatched.text = count.toString()
            } else {
                episodesWatchedListener.onClick(animeList[position].first, count, true)
            }
        }

        holder.itemRemoveButton.setOnClickListener {
            val count = holder.itemEpisodesWatched.text.toString().toInt() - 1
            if(count >= 0) {
                episodesWatchedListener.onClick(animeList[position].first, count, false)
                holder.itemEpisodesWatched.text = count.toString()
            } else {
                episodesWatchedListener.onClick(animeList[position].first, count, true)
            }
        }

        holder.itemView.setOnClickListener {
            removeFromLibraryListener.onClick(item.first)
        }
    }

    override fun getItemCount(): Int = animeList.size

    inner class ViewHolder(binding: FragmentLibraryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val itemImage: ImageView = binding.libraryItemImage
        val itemTitle: TextView = binding.libraryItemTitle
        val itemStudio: TextView = binding.libraryItemStudio
        val itemGenres: TextView = binding.libraryItemGenres
        val itemAiredDate: TextView = binding.libraryItemAiredDate
        val itemEpisodes: TextView = binding.libraryItemEpisodes
        val itemEpisodesWatched: TextView = binding.libraryItemEpisodesWatched
        val itemAddButton: Button = binding.addToEpisodesWatched
        val itemRemoveButton: Button = binding.removeFromEpisodesWatched

        override fun toString(): String {
            return super.toString() + " '" + itemTitle.text + "'"
        }
    }
}