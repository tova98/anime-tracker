package hr.tvz.android.animetracker.presenter

import android.util.Log
import hr.tvz.android.animetracker.LibraryContract
import hr.tvz.android.animetracker.model.Anime
import hr.tvz.android.animetracker.model.CurrentUser
import hr.tvz.android.animetracker.model.EResponse
import hr.tvz.android.animetracker.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LibraryPresenter(view: LibraryContract.View): LibraryContract.Presenter {

    private var view: LibraryContract.View? = view

    override fun onViewCreated() {
        loadData(CurrentUser.user)
    }

    override fun onDestroy() {
        this.view = null
    }

    override fun updateEpisodesWatched(userId: Int, animeId: Int, count: Int) {
        val api = Api.create().updateEpisodesWatched(userId, animeId, count)
        api.enqueue(object: Callback<EResponse> {
            override fun onResponse(call: Call<EResponse>?, response: Response<EResponse>?) {
                if(EResponse.SUCCESS == response?.body()) {
                    Log.i("SUCCESS", "Episodes watched successfully updated!")
                }
            }

            override fun onFailure(call: Call<EResponse>, t: Throwable) {
                Log.e("ERROR!", "Error while calling API.", t)
            }
        })
    }

    override fun removeFromLibrary(userId: Int, animeId: Int) {
        val api = Api.create().removeFromLibrary(userId, animeId)
        api.enqueue(object: Callback<EResponse> {
            override fun onResponse(call: Call<EResponse>?, response: Response<EResponse>?) {
                if(EResponse.SUCCESS == response?.body()) {
                    view?.displayMessage("Removed from library!")
                }
            }

            override fun onFailure(call: Call<EResponse>, t: Throwable) {
                Log.e("ERROR!", "Error while calling API.", t)
            }
        })
    }

    private fun loadData(user: User) {
        val api = Api.create().getAllAnimeByUser(user.id)
        api.enqueue(object: Callback<List<Pair<Anime, Int>>> {
            override fun onResponse(call: Call<List<Pair<Anime, Int>>>?, response: Response<List<Pair<Anime, Int>>>?) {
                if(response?.body() != null) {
                    view?.displayLibraryList(response.body()!!)
                }
            }

            override fun onFailure(call: Call<List<Pair<Anime, Int>>>, t: Throwable) {
                Log.e("ERROR!", "Error while calling API.", t)
            }
        })
    }

}