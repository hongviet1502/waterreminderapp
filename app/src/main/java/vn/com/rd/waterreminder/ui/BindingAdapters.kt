package vn.com.rd.waterreminder.ui

import android.view.View
import androidx.databinding.BindingAdapter


object BindingAdapters {
    @JvmStatic
    @BindingAdapter("visibleGone")
    fun showHide(view: View, show: Boolean) {
        view.visibility = if (show) View.VISIBLE else View.GONE
    }
}