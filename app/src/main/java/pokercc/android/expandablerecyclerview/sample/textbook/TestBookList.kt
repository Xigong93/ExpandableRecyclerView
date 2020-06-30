package pokercc.android.expandablerecyclerview.sample.textbook

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class TestBookList(
    val courseId: Int, // 54
    val filters: List<Filter>,
    val id: Int, // 63
    val name: String // 华师大旧版
) {
    @JsonClass(generateAdapter = true)
    data class Filter(
        val id: Int, // 461
        val name: String // 七年级上册
    )
}
