package hr.tvz.android.animetracker

import hr.tvz.android.animetracker.model.User
import hr.tvz.android.animetracker.presenter.BasePresenter
import hr.tvz.android.animetracker.view.BaseView
import okhttp3.MultipartBody

interface ProfileContract {

    interface Presenter : BasePresenter {
        fun onViewCreated()
        fun changeUserPassword(passwordPair: Pair<String, String>)
        fun changeUserProfilePicture(userId: Int, picture: MultipartBody.Part)
    }

    interface View : BaseView<Presenter> {
        fun displayMessage(message: String)
        fun displayEpisodesWatched(count: Int)
        fun displayShowsWatched(count: Int)
        fun updateCurrentUserParams(user: User)
    }
}