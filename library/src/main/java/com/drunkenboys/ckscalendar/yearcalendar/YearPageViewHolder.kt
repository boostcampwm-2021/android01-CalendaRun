package com.drunkenboys.ckscalendar.yearcalendar

import android.graphics.Color
import android.text.TextUtils
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.drunkenboys.ckscalendar.FakeFactory
import com.drunkenboys.ckscalendar.data.CalendarDate
import com.drunkenboys.ckscalendar.databinding.ItemYearHeaderBinding
import com.drunkenboys.ckscalendar.databinding.ItemYearPageBinding

sealed class YearPageViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    class PageViewHolder(private val binding: ItemYearPageBinding) : YearPageViewHolder(binding.root) {

        private val root = (binding.root) as ConstraintLayout

        private val constraintSet = ConstraintSet()

        init {
            binding.design = FakeFactory.createFakeDesign()
            val tvSundayList = mutableListOf(binding.tvItemWeekSunday)

            // TODO 스케줄 탐색
            // TODO 스케줄 갱신에 대응
            tvSundayList.add(createChildTv())
            tvSundayList.add(createChildTv())
            tvSundayList.add(createChildTv())
            addScheduleView(tvSundayList[0], tvSundayList[1])
            addScheduleView(tvSundayList[1], tvSundayList[2])
            addScheduleView(tvSundayList[2], tvSundayList[3])
        }

        fun bind(week: List<CalendarDate>) {
            binding.week = week
        }

        // FIXME: 임시값 수정
        private fun createChildTv() = TextView((binding.root).context).apply {
            id = View.generateViewId()
            textSize = 10f
            ellipsize = TextUtils.TruncateAt.END
            maxLines = 1
            text = "멘토링토링"
            setBackgroundColor(Color.YELLOW)
        }

        private fun addScheduleView(parentView: View, childView: View) = constraintSet.apply {
            root.addView(childView)

            clone(root)
            connect(childView.id, ConstraintSet.TOP, parentView.id, ConstraintSet.BOTTOM)
            connect(childView.id, ConstraintSet.START, parentView.id, ConstraintSet.START)
            connect(childView.id, ConstraintSet.END, parentView.id, ConstraintSet.END)
            applyTo(root)
        }
    }

    class YearPageMonthViewHolder(private val binding: ItemYearHeaderBinding) : YearPageViewHolder(binding.root) {

        fun bind(month: Int) {
            binding.header = month
        }
    }

    class YearPageYearViewHolder(private val binding: ItemYearHeaderBinding) : YearPageViewHolder(binding.root) {

        fun bind(year: Int) {
            binding.header = year
        }
    }
}
