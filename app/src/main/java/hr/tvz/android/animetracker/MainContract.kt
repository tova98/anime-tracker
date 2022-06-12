package hr.tvz.android.animetracker

import hr.tvz.android.animetracker.presenter.BasePresenter
import hr.tvz.android.animetracker.view.BaseView

interface MainContract {

    interface Presenter : BasePresenter {
        fun onViewCreated()
    }

    interface View : BaseView<Presenter> {
    }
}