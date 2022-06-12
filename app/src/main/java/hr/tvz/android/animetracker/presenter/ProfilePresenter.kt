package hr.tvz.android.animetracker.presenter

import android.util.Log
import hr.tvz.android.animetracker.ProfileContract
import hr.tvz.android.animetracker.model.CurrentUser
import hr.tvz.android.animetracker.model.EResponse
import hr.tvz.android.animetracker.model.User
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfilePresenter(view: ProfileContract.View): ProfileContract.Presenter {

    private var view: ProfileContract.View? = view

    override fun onViewCreated() {
        getEpisodesWatchedByUser()
        getShowsWatchedByUser()
    }

    override fun onDestroy() {
        this.view = null
    }

    override fun changeUserPassword(passwordPair: Pair<String, String>) {
        val api = Api.create().changeUserPassword(CurrentUser.user.id, passwordPair)
        api.enqueue(object: Callback<EResponse> {
            override fun onResponse(call: Call<EResponse>, response: Response<EResponse>) {
                if(response.body() == EResponse.SUCCESS) {
                    view?.displayMessage("Password changed successfully!")
                } else {
                    view?.displayMessage("Password was not changed!")
                }
            }

            override fun onFailure(call: Call<EResponse>, t: Throwable) {
                Log.e("ERROR", "Error while calling API.")
            }
        })
    }

    override fun changeUserProfilePicture(userId: Int, picture: MultipartBody.Part) {
        val api = Api.create().changeUserProfilePicture(userId, picture)
        api.enqueue(object: Callback<User> {
            override fun onResponse(call: Call<User>?, response: Response<User>?) {
                if(response?.body() != null) {
                    val user: User = response.body()!!
                    view?.updateCurrentUserParams(user)
                    Log.i("SUCCESS", "Profile picture successfully updated!")
                } else {
                    Log.e("ERROR", "Could not update profile picture!")
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.e("ERROR!", "Error while calling API.", t)
            }
        })
    }

    private fun getEpisodesWatchedByUser() {
        val api = Api.create().getEpisodesWatchedByUser(CurrentUser.user.id)
        api.enqueue(object: Callback<Int> {
            override fun onResponse(call: Call<Int>?, response: Response<Int>?) {
                if(response?.body() != null) {
                    view?.displayEpisodesWatched(response.body()!!)
                }
            }

            override fun onFailure(call: Call<Int>, t: Throwable) {
                Log.e("ERROR!", "Error while calling API.", t)
            }
        })
    }

    private fun getShowsWatchedByUser() {
        val api = Api.create().getShowsWatchedByUser(CurrentUser.user.id)
        api.enqueue(object: Callback<Int> {
            override fun onResponse(call: Call<Int>?, response: Response<Int>?) {
                if(response?.body() != null) {
                    view?.displayShowsWatched(response.body()!!)
                }
            }

            override fun onFailure(call: Call<Int>, t: Throwable) {
                Log.e("ERROR!", "Error while calling API.", t)
            }
        })
    }
}