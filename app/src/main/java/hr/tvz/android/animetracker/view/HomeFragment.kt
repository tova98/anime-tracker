package hr.tvz.android.animetracker.view

import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.GridLayoutManager
import hr.tvz.android.animetracker.HomeContract
import hr.tvz.android.animetracker.adapter.AnimeRecyclerViewAdapter
import hr.tvz.android.animetracker.databinding.FragmentHomeBinding
import hr.tvz.android.animetracker.model.Anime
import hr.tvz.android.animetracker.model.CurrentUser
import hr.tvz.android.animetracker.presenter.HomePresenter

class HomeFragment : Fragment(), HomeContract.View {

    private var columnCount = 3

    private lateinit var presenter: HomeContract.Presenter
    private lateinit var binding: FragmentHomeBinding

    fun interface ItemClickListener {
        fun onClick(anime: Anime)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        return if(enter) {
            AnimationUtils.loadAnimation(context, androidx.appcompat.R.anim.abc_fade_in)
        } else {
            AnimationUtils.loadAnimation(context, androidx.appcompat.R.anim.abc_fade_out)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        binding = FragmentHomeBinding.inflate(inflater)

        if(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            columnCount = 5
        }

        binding.animeListRecyclerView.layoutManager = GridLayoutManager(context, columnCount)

        setPresenter(HomePresenter(this))
        presenter.onViewCreated()

        return binding.root
    }

    override fun setPresenter(presenter: HomeContract.Presenter) {
        this.presenter = presenter
    }

    override fun displayAnimeList(animeList: List<Anime>) {
        val listener = createOnClickListener()
        binding.animeListRecyclerView.adapter = AnimeRecyclerViewAdapter(animeList, listener)

        binding.filterEditText.addTextChangedListener {
            val filteredAnimeList = animeList.filter { anime -> containsStringInTitle(anime.title!!, it.toString()) }
            binding.animeListRecyclerView.adapter = AnimeRecyclerViewAdapter(filteredAnimeList, listener)
        }
    }

    override fun displayMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    private fun createOnClickListener(): ItemClickListener {
        return ItemClickListener {
            showDialog(it)
        }
    }

    private fun containsStringInTitle(string: String, title: String): Boolean {
        return string.lowercase().contains(title.lowercase())
    }

    private fun showDialog(anime: Anime) {
        val builder: AlertDialog.Builder = this.let {
            AlertDialog.Builder(requireContext())
        }
        builder.setTitle(anime.title)
            .setMessage("Add ${anime.title} to library?")
            .apply {
                setPositiveButton("Add") { _, _ ->
                    presenter.saveToLibrary(CurrentUser.user, anime)
                }
                setNegativeButton("Cancel", null)
            }
        builder.create().show()
    }

}