package hr.tvz.android.animetracker

import hr.tvz.android.animetracker.model.Anime
import hr.tvz.android.animetracker.model.User
import hr.tvz.android.animetracker.presenter.BasePresenter
import hr.tvz.android.animetracker.view.BaseView

interface HomeContract {

    interface Presenter : BasePresenter {
        fun onViewCreated()
        fun saveToLibrary(user: User, anime: Anime)
    }

    interface View : BaseView<Presenter> {
        fun displayAnimeList(animeList: List<Anime>)
        fun displayMessage(message: String)
    }
}