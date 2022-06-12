package hr.tvz.android.animetracker

import hr.tvz.android.animetracker.model.Anime
import hr.tvz.android.animetracker.presenter.BasePresenter
import hr.tvz.android.animetracker.view.BaseView

interface LibraryContract {

    interface Presenter : BasePresenter {
        fun onViewCreated()
        fun updateEpisodesWatched(userId: Int, animeId: Int, count: Int)
        fun removeFromLibrary(userId: Int, animeId: Int)
    }

    interface View : BaseView<Presenter> {
        fun displayLibraryList(animeList: List<Pair<Anime, Int>>)
        fun displayMessage(message: String)
    }
}