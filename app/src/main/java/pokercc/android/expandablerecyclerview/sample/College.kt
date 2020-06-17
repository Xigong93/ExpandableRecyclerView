package pokercc.android.expandablerecyclerview.sample

import androidx.annotation.Keep
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class College(
    val id: Int,
    val order: Int,
    val name: String,
    val short: String?,
    val zone: String
)

@JsonClass(generateAdapter = true)
data class CollegeZone(
    val id: String,
    val sort: Int,
    val name: String
) {
    @Transient
    val colleges = ArrayList<College>()
}

@JsonClass(generateAdapter = true)
data class CollegeWrapper(
    val zone: List<CollegeZone>,
    val university: List<College>
)