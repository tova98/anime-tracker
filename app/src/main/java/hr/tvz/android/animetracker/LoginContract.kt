package hr.tvz.android.animetracker

import hr.tvz.android.animetracker.dto.UserDto
import hr.tvz.android.animetracker.model.User
import hr.tvz.android.animetracker.presenter.BasePresenter
import hr.tvz.android.animetracker.view.BaseView

interface LoginContract {
    interface Presenter : BasePresenter {
        fun onViewCreated()
        fun registerUser(userDto: UserDto)
        fun loginUser(userDto: UserDto)
    }

    interface View : BaseView<Presenter> {
        fun navigateOnLoginSuccess(user: User, userDto: UserDto)
        fun displayRegistrationSuccess(message:String)
        fun displayErrorMessage(errorMessage: String)
        fun showProgress()
        fun hideProgress()
        fun setUsernameError(errorMessage: String)
        fun setPasswordError(errorMessage: String)
    }
}