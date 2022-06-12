package hr.tvz.android.animetracker.presenter

import hr.tvz.android.animetracker.LoginContract
import hr.tvz.android.animetracker.dto.UserDto
import hr.tvz.android.animetracker.model.EResponse
import hr.tvz.android.animetracker.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginPresenter(view: LoginContract.View): LoginContract.Presenter {

    private var view: LoginContract.View? = view

    override fun onViewCreated() {
    }

    override fun onDestroy() {
        this.view = null
    }

    override fun registerUser(userDto: UserDto) {
        if(!validateUserDetails(userDto)) return
        view?.showProgress()
        val api = Api.create().register(userDto)
        api.enqueue(object: Callback<EResponse> {
            override fun onResponse(call: Call<EResponse>, response: Response<EResponse>) {
                when {
                    EResponse.SUCCESS == response.body()!! -> {
                        view?.displayRegistrationSuccess("Registration successful!")
                    }
                    EResponse.USERNAME_TAKEN == response.body()!! -> {
                        view?.displayErrorMessage("Username already taken!")
                    }
                    else -> {
                        view?.displayErrorMessage("Registration failed!")
                    }
                }
            }

            override fun onFailure(call: Call<EResponse>, t: Throwable) {
                view?.displayErrorMessage( "Error while calling API.")
            }
        })
    }

    override fun loginUser(userDto: UserDto) {
        view?.showProgress()
        val api = Api.create().login(userDto)
        api.enqueue(object: Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                val user: User = response.body()!!
                if(user.username != null) {
                    view?.navigateOnLoginSuccess(user, userDto)
                } else {
                    view?.displayErrorMessage("Wrong credentials!")
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                view?.displayErrorMessage( "Error while calling API.")
            }
        })
    }

    private fun validateUserDetails(userDto: UserDto): Boolean {
        var valid = true
        if(userDto.username == null || userDto.username == "") {
            view?.setUsernameError("Username can't be empty!")
            valid = false
        }

        if(userDto.password == null || userDto.password == "") {
            view?.setPasswordError("Password can't be empty!")
            valid = false
        } else if(userDto.password.length < 5) {
            view?.setPasswordError("Password must be at least 5 characters long!")
            valid = false
        }

        return valid
    }
}