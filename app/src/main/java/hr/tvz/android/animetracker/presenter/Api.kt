package hr.tvz.android.animetracker.presenter

import hr.tvz.android.animetracker.dto.UserDto
import hr.tvz.android.animetracker.model.Anime
import hr.tvz.android.animetracker.model.EResponse
import hr.tvz.android.animetracker.model.User
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface Api {

    @POST("api/user/register")
    fun register(@Body userDto: UserDto): Call<EResponse>

    @POST("api/user/login")
    fun login(@Body userDto: UserDto): Call<User>

    @POST("api/user/change-password/{userId}")
    fun changeUserPassword(@Path("userId") userId: Int, @Body passwordPair: Pair<String, String>): Call<EResponse>

    @Multipart
    @POST("api/user/change-profile-picture/{userId}")
    fun changeUserProfilePicture(@Path("userId") userId: Int, @Part picture: MultipartBody.Part): Call<User>

    @GET("api/anime")
    fun getAllAnime(): Call<List<Anime>>

    @GET("api/library/{userId}")
    fun getAllAnimeByUser(@Path("userId") userId: Int): Call<List<Pair<Anime, Int>>>

    @GET("api/library/episodes/{userId}")
    fun getEpisodesWatchedByUser(@Path("userId") userId: Int): Call<Int>

    @GET("api/library/shows/{userId}")
    fun getShowsWatchedByUser(@Path("userId") userId: Int): Call<Int>

    @POST("api/library/{userId}/{animeId}")
    fun updateEpisodesWatched(@Path("userId") userId: Int, @Path("animeId") animeId: Int, @Body count: Int): Call<EResponse>

    @POST("api/library/{userId}")
    fun saveToLibrary(@Path("userId") userId: Int, @Body anime: Anime): Call<EResponse>

    @DELETE("api/library/{userId}/{animeId}")
    fun removeFromLibrary(@Path("userId") userId: Int, @Path("animeId") animeId: Int): Call<EResponse>

    companion object {
        var BASE_URL = "http://192.168.0.100:8080/"

        fun create(): Api {
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(Api::class.java)
        }
    }
}