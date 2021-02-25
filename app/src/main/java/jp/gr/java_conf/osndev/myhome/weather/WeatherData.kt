package jp.gr.java_conf.osndev.myhome.weather

import android.os.Parcel
import android.os.Parcelable

class WeatherData : Parcelable {
    var weather: String
    var rainyP1: String
    var rainyP2: String
    var rainyP3: String
    var rainyP4: String

    constructor(weather: String, rainyP1: String, rainyP2: String, rainyP3: String, rainyP4: String) {
        this.weather = weather
        this.rainyP1 = rainyP1
        this.rainyP2 = rainyP2
        this.rainyP3 = rainyP3
        this.rainyP4 = rainyP4
    }

    constructor(parcel: Parcel): super() {
        this.weather = parcel.readString() ?: ""
        this.rainyP1 =  parcel.readString() ?: ""
        this.rainyP2 =  parcel.readString() ?: ""
        this.rainyP3 =  parcel.readString() ?: ""
        this.rainyP4 =  parcel.readString() ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(weather)
        parcel.writeString(rainyP1)
        parcel.writeString(rainyP2)
        parcel.writeString(rainyP3)
        parcel.writeString(rainyP4)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<WeatherData> {
        override fun createFromParcel(parcel: Parcel): WeatherData {
            return WeatherData(parcel)
        }

        override fun newArray(size: Int): Array<WeatherData?> {
            return arrayOfNulls(size)
        }
    }
}