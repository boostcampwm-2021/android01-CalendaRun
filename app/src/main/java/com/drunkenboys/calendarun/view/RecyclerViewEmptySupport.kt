package com.drunkenboys.calendarun.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView


class RecyclerViewEmptySupport @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    var emptyView: View? = null

    private val _emptyObserver = object : AdapterDataObserver() {
        override fun onChanged() {
            println(adapter?.itemCount)
            emptyView?.isVisible = adapter?.itemCount == 0
            this@RecyclerViewEmptySupport.isVisible = adapter?.itemCount != 0
        }
    }
    val emptyObserver get() = _emptyObserver

    override fun setAdapter(adapter: Adapter<*>?) {
        super.setAdapter(adapter)

        adapter?.registerAdapterDataObserver(emptyObserver)

        emptyObserver.onChanged()
    }
}
