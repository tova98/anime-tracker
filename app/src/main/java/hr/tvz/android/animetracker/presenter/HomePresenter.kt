package hr.tvz.android.animetracker.presenter

import android.util.Log
import hr.tvz.android.animetracker.HomeContract
import hr.tvz.android.animetracker.model.Anime
import hr.tvz.android.animetracker.model.EResponse
import hr.tvz.android.animetracker.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomePresenter(view: HomeContract.View): HomeContract.Presenter {

    private var view: HomeContract.View? = view

    override fun onViewCreated() {
        loadData()
    }

    override fun onDestroy() {
        this.view = null
    }

    override fun saveToLibrary(user: User, anime: Anime) {
        val api = Api.create().saveToLibrary(user.id, anime)
        api.enqueue(object: Callback<EResponse> {
            override fun onResponse(call: Call<EResponse>, response: Response<EResponse>) {
                if(response.body() != null) {
                    if(EResponse.SUCCESS == response.body()!!) {
                        view?.displayMessage("Added to library!")
                    } else {
                        view?.displayMessage("Could not add to library...!")
                    }
                }
            }

            override fun onFailure(call: Call<EResponse>, t: Throwable) {
                view?.displayMessage( "Error while calling API.")
            }
        })
    }

    private fun loadData() {
        val api = Api.create().getAllAnime()
        api.enqueue(object: Callback<List<Anime>> {
            override fun onResponse(call: Call<List<Anime>>?, response: Response<List<Anime>>?) {
                if(response?.body() != null) {
                    view?.displayAnimeList(response.body()!!)
                }
            }

            override fun onFailure(call: Call<List<Anime>>, t: Throwable) {
                Log.e("ERROR!", "Error while calling API.", t)
            }
        })
    }
}