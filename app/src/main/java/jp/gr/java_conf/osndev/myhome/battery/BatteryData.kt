package jp.gr.java_conf.osndev.myhome.battery

import android.os.Parcel
import android.os.Parcelable

class BatteryData : Parcelable {
    var level: String
    var isCharging: Boolean

    constructor(level: String, isCharging: Boolean) {
        this.level = level
        this.isCharging = isCharging
    }

    constructor(parcel: Parcel): super() {
        this.level = parcel.readString() ?: ""
        this.isCharging = (parcel.readInt() == 1)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(level)
        parcel.writeInt(if (isCharging)  1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BatteryData> {
        override fun createFromParcel(parcel: Parcel): BatteryData {
            return BatteryData(parcel)
        }

        override fun newArray(size: Int): Array<BatteryData?> {
            return arrayOfNulls(size)
        }
    }
}