package com.drunkenboys.ckscalendar.yearcalendar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.drunkenboys.ckscalendar.data.CalendarDate
import com.drunkenboys.ckscalendar.data.CalendarSet
import com.drunkenboys.ckscalendar.data.DayType
import com.drunkenboys.ckscalendar.databinding.ItemYearHeaderBinding
import com.drunkenboys.ckscalendar.databinding.ItemYearPageBinding
import com.drunkenboys.ckscalendar.utils.TimeUtils
import java.time.DayOfWeek

class YearPageAdapter : ListAdapter<CalendarSet, YearPageViewHolder>(DateDiffUtilCallback()) {

    private val calendar = mutableListOf<YearViewType>()

    fun setItems(year: List<CalendarSet>) {
        // 1년 item 추가
        calendar.add(YearViewType.YearEntity(year[0].startDate.year))

        year.forEach { month ->
            // 1개월 item 추가
            calendar.add(YearViewType.MonthEntity(month.startDate.monthValue))

            // n주
            val weekList = mutableListOf<MutableList<CalendarDate>>()
            var oneDay = month.startDate
            var paddingPrev = month.startDate
            var paddingNext = month.endDate

            // 앞쪽 패딩
            if (paddingPrev.dayOfWeek != DayOfWeek.SUNDAY) weekList.add(mutableListOf())
            while (paddingPrev.dayOfWeek != DayOfWeek.SUNDAY) {
                // FIXME: 없는 날짜로 수정해야 스케줄 탐색 가능
                weekList.last().add(CalendarDate(paddingPrev, DayType.PADDING))
                paddingPrev = paddingPrev.minusDays(1)
            }

            // n주일 추가
            repeat(month.startDate.lengthOfMonth()) {
                // 일요일에는 1주일 갱신
                if (oneDay.dayOfWeek == DayOfWeek.SUNDAY) weekList.add(mutableListOf())

                // 1주일 추가
                weekList.last().add(CalendarDate(oneDay, TimeUtils.parseDayWeekToDayType(oneDay.dayOfWeek)))

                oneDay = oneDay.plusDays(1L)
            }

            // 뒤쪽 패딩
            while (paddingNext.dayOfWeek != DayOfWeek.SATURDAY) {
                // FIXME: 없는 날짜로 수정해야 스케줄 탐색 가능
                weekList.last().add(CalendarDate(paddingNext, DayType.PADDING))
                paddingNext = paddingNext.plusDays(1)
            }

            // n주 item 추가
            weekList.forEach { week ->
                calendar.add(YearViewType.WeekEntity(week))
            }
        }

        notifyItemChanged(calendar.size - 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YearPageViewHolder = when(viewType) {
        YEAR_TYPE -> YearPageViewHolder.YearPageYearViewHolder(ItemYearHeaderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))

        MONTH_TYPE -> YearPageViewHolder.YearPageMonthViewHolder(ItemYearHeaderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))

        else -> YearPageViewHolder.PageViewHolder(ItemYearPageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: YearPageViewHolder, position: Int) {
        when(holder) {
            is YearPageViewHolder.YearPageYearViewHolder ->
                holder.bind((calendar[position] as YearViewType.YearEntity).year)

            is YearPageViewHolder.YearPageMonthViewHolder ->
                holder.bind((calendar[position] as YearViewType.MonthEntity).month)

            is YearPageViewHolder.PageViewHolder ->
                holder.bind((calendar[position] as YearViewType.WeekEntity).week)
        }
    }

    override fun getItemCount() = calendar.size

    override fun getItemViewType(position: Int) = when (calendar[position]) {
        is YearViewType.YearEntity -> YEAR_TYPE

        is YearViewType.MonthEntity -> MONTH_TYPE

        is YearViewType.WeekEntity -> WEEK_TYPE
    }

    class DateDiffUtilCallback : DiffUtil.ItemCallback<CalendarSet>() {

        override fun areItemsTheSame(oldItem: CalendarSet, newItem: CalendarSet) = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: CalendarSet, newItem: CalendarSet) = oldItem == newItem
    }

    companion object {
        const val YEAR_TYPE = 0
        const val MONTH_TYPE = 1
        const val WEEK_TYPE = 2
        const val TAG = "YearPageAdapter"
    }
}