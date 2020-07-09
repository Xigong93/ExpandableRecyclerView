/**
 * Created by pokercc on 19-12-10.
 */
@Suppress("MemberVisibilityCanBePrivate")
data class AppVersion(val major: Int, val minor: Int, val revision: Int) {
    val versionCode: Int
    val versionName: String

    init {
        require(major in 0..999) { "major must in [0,999],but is $major" }
        require(minor in 0..99) { "minor must in [0,99],but is $minor" }
        require(revision in 0..99) { "revision must in [0,99],but is $revision" }
        versionCode = major * 10000 + minor * 100 + revision
        versionName = "${major}.${minor}.${revision}"
    }

    override fun toString(): String {
        return "AppVersion(major=$major, minor=$minor, revision=$revision, versionCode=$versionCode, versionName='$versionName')"
    }

}