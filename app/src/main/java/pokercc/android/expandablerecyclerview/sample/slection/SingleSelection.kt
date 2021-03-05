package pokercc.android.expandablerecyclerview.sample.slection

internal data class SingleSelection<T>(
    val name: String,
    val options: List<T>,
    var selectedIndex: Int = -1
) : Selection

internal interface Selection