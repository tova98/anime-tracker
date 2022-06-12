package hr.tvz.android.animetracker.presenter

import hr.tvz.android.animetracker.MainContract

class MainPresenter(view: MainContract.View): MainContract.Presenter {

    private var view: MainContract.View? = view

    override fun onViewCreated() {
    }

    override fun onDestroy() {
        this.view = null
    }

}