package pokercc.android.expandablerecyclerview.sample.college

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize


@JsonClass(generateAdapter = true)
data class College(
    val id: Int,
    val order: Int,
    val name: String,
    val short: String?,
    val zone: String,
    val famous: Boolean = false
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString(),
        parcel.readString()!!,
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(order)
        parcel.writeString(name)
        parcel.writeString(short)
        parcel.writeString(zone)
        parcel.writeByte(if (famous) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<College> {
        override fun createFromParcel(parcel: Parcel): College {
            return College(parcel)
        }

        override fun newArray(size: Int): Array<College?> {
            return arrayOfNulls(size)
        }
    }
}

@JsonClass(generateAdapter = true)
data class CollegeZone(
    val id: String,
    val sort: Int,
    val name: String,
    val city: Boolean = false

) : Parcelable {
    @Transient
    val colleges: MutableList<College> = ArrayList()

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readByte() != 0.toByte()
    ) {
        parcel.readTypedList(colleges, College.CREATOR)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeInt(sort)
        parcel.writeString(name)
        parcel.writeByte(if (city) 1 else 0)
        parcel.writeTypedList(colleges)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CollegeZone> {
        override fun createFromParcel(parcel: Parcel): CollegeZone {
            return CollegeZone(parcel)
        }

        override fun newArray(size: Int): Array<CollegeZone?> {
            return arrayOfNulls(size)
        }
    }

}

@Parcelize
@JsonClass(generateAdapter = true)
data class CollegeWrapper(
    val zone: List<CollegeZone>,
    val university: List<College>
) : Parcelable