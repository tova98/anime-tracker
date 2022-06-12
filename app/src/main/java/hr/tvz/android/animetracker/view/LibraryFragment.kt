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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import hr.tvz.android.animetracker.LibraryContract
import hr.tvz.android.animetracker.adapter.LibraryRecyclerViewAdapter
import hr.tvz.android.animetracker.databinding.FragmentLibraryBinding
import hr.tvz.android.animetracker.model.Anime
import hr.tvz.android.animetracker.model.CurrentUser
import hr.tvz.android.animetracker.presenter.LibraryPresenter

class LibraryFragment : Fragment(), LibraryContract.View {

    private lateinit var presenter: LibraryContract.Presenter
    private lateinit var binding: FragmentLibraryBinding

    fun interface EpisodesWatchedListener {
        fun onClick(anime: Anime, count: Int, fail: Boolean)
    }

    fun interface RemoveFromLibraryListener {
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentLibraryBinding.inflate(inflater)

        if(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.libraryRecyclerView.layoutManager = GridLayoutManager(context, 2)
        } else {
            binding.libraryRecyclerView.layoutManager = LinearLayoutManager(context)
        }

        setPresenter(LibraryPresenter(this))
        presenter.onViewCreated()

        return binding.root
    }

    override fun setPresenter(presenter: LibraryContract.Presenter) {
       this.presenter = presenter
    }

    override fun displayLibraryList(animeList: List<Pair<Anime, Int>>) {
        val episodesWatchedListener = createEpisodesWatchedListener()
        val removeFromLibraryListener = createRemoveFromLibraryListener()
        binding.libraryRecyclerView.adapter = LibraryRecyclerViewAdapter(animeList, episodesWatchedListener, removeFromLibraryListener)
    }

    override fun displayMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun createEpisodesWatchedListener(): EpisodesWatchedListener {
        return EpisodesWatchedListener {
            anime, count, failed ->
            run {
                if (failed) {
                    displayMessage("Can't update episode count!")
                } else {
                    presenter.updateEpisodesWatched(CurrentUser.user.id, anime.id, count)
                }
            }
        }
    }

    private fun createRemoveFromLibraryListener(): RemoveFromLibraryListener {
        return RemoveFromLibraryListener {
            showRemoveFromLibraryDialog(it)
        }
    }

    private fun showRemoveFromLibraryDialog(anime: Anime) {
        val builder: AlertDialog.Builder = this.let {
            AlertDialog.Builder(requireContext())
        }
        builder.setTitle(anime.title)
            .setMessage("Remove ${anime.title} from library?")
            .apply {
                setPositiveButton("Remove") { _, _ ->
                    presenter.removeFromLibrary(CurrentUser.user.id, anime.id)
                    refreshFragment()
                }
                setNegativeButton("Cancel", null)
            }
        builder.create().show()
    }

    private fun refreshFragment() {
        parentFragmentManager.beginTransaction().detach(this).commitNow()
        parentFragmentManager.beginTransaction().attach(this).commitNow()
    }
}