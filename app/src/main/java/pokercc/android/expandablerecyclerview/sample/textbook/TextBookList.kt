package pokercc.android.expandablerecyclerview.sample.textbook

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize


@Parcelize
@JsonClass(generateAdapter = true)
data class TextBookList(
    val courseId: Int, // 54
    val filters: List<Filter>,
    val id: Int, // 63
    val name: String // 华师大旧版
) : Parcelable {

    @Parcelize
    @JsonClass(generateAdapter = true)
    data class Filter(
        val id: Int, // 461
        val name: String // 七年级上册
    ) : Parcelable
}
