package hr.tvz.android.animetracker.model

import android.os.Parcel
import android.os.Parcelable

class Anime (
    val id: Int,
    val title: String?,
    var image: String?,
    val studio: String?,
    val episodes: Int?,
    val genres: String?,
    var airedDate: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(image)
        parcel.writeString(studio)
        if (episodes != null) {
            parcel.writeInt(episodes)
        }
        parcel.writeString(genres)
        parcel.writeString(airedDate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Anime> {
        override fun createFromParcel(parcel: Parcel): Anime {
            return Anime(parcel)
        }

        override fun newArray(size: Int): Array<Anime?> {
            return arrayOfNulls(size)
        }
    }
}