package vn.com.rd.waterreminder.ui.component

data class InfoItem(
    var titleText: String,
    var contentText: String?,
    private var id: Int? = null
) {
    fun getId() = id ?: hashCode()
}
