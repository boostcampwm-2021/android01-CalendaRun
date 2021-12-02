package com.drunkenboys.ckscalendar.monthcalendar

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.drunkenboys.ckscalendar.R
import com.drunkenboys.ckscalendar.data.CalendarDate
import com.drunkenboys.ckscalendar.data.CalendarDesignObject
import com.drunkenboys.ckscalendar.data.CalendarScheduleObject
import com.drunkenboys.ckscalendar.data.DayType
import com.drunkenboys.ckscalendar.databinding.ItemMonthCellBinding
import com.drunkenboys.ckscalendar.listener.OnDayClickListener
import com.drunkenboys.ckscalendar.listener.OnDaySecondClickListener
import com.drunkenboys.ckscalendar.utils.context
import com.drunkenboys.ckscalendar.utils.dp2px
import com.drunkenboys.ckscalendar.utils.tintStroke
import java.time.LocalDate

class MonthAdapter(val onDaySelectStateListener: OnDaySelectStateListener) : RecyclerView.Adapter<MonthAdapter.Holder>() {

    private var schedules = listOf<CalendarScheduleObject>()

    private val currentList = mutableListOf<CalendarDate>()

    private val startDayWithMonthFlags = HashMap<String, String>()
    val startDayWithYearFlags = HashMap<Int, Int>()

    private lateinit var calendarDesign: CalendarDesignObject

    var selectedPosition = -1
    var currentPagePosition = -1

    var onDayClickListener: OnDayClickListener? = null
    var onDaySecondClickListener: OnDaySecondClickListener? = null

    private val lineIndex = HashMap<String, MonthCellPositionStore>()

    fun setItems(
        list: List<CalendarDate>,
        schedules: List<CalendarScheduleObject>,
        calendarDesign: CalendarDesignObject,
        currentPagePosition: Int
    ) {
        startDayWithMonthFlags.clear()
        startDayWithYearFlags.clear()
        list.forEachIndexed { index, calendarDate ->
            if (calendarDate.isSelected) {
                selectedPosition = index
            }
            // find FirstDay of Month
            val startDayWithDayKey = "${calendarDate.date.year}${calendarDate.date.monthValue}"
            if (calendarDate.dayType != DayType.PADDING && startDayWithMonthFlags[startDayWithDayKey] == null) {
                startDayWithMonthFlags[startDayWithDayKey] = "${calendarDate.date}"
            }

            // find FirstDay of Year
            if (list.size > 42 && calendarDate.date.year != 1970 && startDayWithYearFlags[calendarDate.date.year] == null) {
                startDayWithYearFlags[calendarDate.date.year] = index
            }
        }

        this.lineIndex.clear()
        this.calendarDesign = calendarDesign
        this.currentPagePosition = currentPagePosition
        this.schedules = schedules
        this.currentList.clear()
        this.currentList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val calculateHeight = parent.height / CALENDAR_COLUMN_SIZE

        return Holder(ItemMonthCellBinding.inflate(LayoutInflater.from(parent.context), parent, false), calculateHeight)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(currentList[position])
    }

    override fun getItemCount(): Int = currentList.size


    inner class Holder(private val binding: ItemMonthCellBinding, private val calculateHeight: Int) :
        RecyclerView.ViewHolder(binding.root) {

        private val weekDayColor = calendarDesign.weekDayTextColor
        private val holidayColor = calendarDesign.holidayTextColor
        private val saturdayColor = calendarDesign.saturdayTextColor

        init {
            itemView.setOnClickListener {
                if (adapterPosition != -1 && currentList[adapterPosition].dayType != DayType.PADDING) {
                    notifyClickEventType()
                    notifyChangedSelectPosition(adapterPosition)
                    onDaySelectStateListener.onDaySelectChange(currentPagePosition, selectedPosition)
                }
            }
            binding.layoutMonthCell.layoutParams.height = calculateHeight
            binding.viewMonthSelectFrame.setBackgroundResource(calendarDesign.selectedFrameDrawable)
            binding.viewMonthSelectFrame.tintStroke(calendarDesign.selectedFrameColor, 2.0f)
        }

        @SuppressLint("SetTextI18n")
        fun bind(item: CalendarDate) {
            binding.layoutMonthCell.isSelected = item.isSelected
            binding.tvMonthDay.text = ""
            if (item.dayType != DayType.PADDING) {
                val startDayWithDayKey = "${item.date.year}${item.date.monthValue}"
                if (startDayWithMonthFlags[startDayWithDayKey] == item.date.toString()) {
                    binding.tvMonthDay.text = "${item.date.monthValue}. ${item.date.dayOfMonth}"
                    binding.tvMonthDay.typeface = Typeface.DEFAULT_BOLD
                } else {
                    binding.tvMonthDay.text = "${item.date.dayOfMonth}"
                    binding.tvMonthDay.typeface = Typeface.DEFAULT
                }
            }
            setDateCellTextDesign(item)

            val scheduleContainer = makePaddingScheduleList(item, schedules)
            val hasAnySchedule = scheduleContainer.any { it != null }
            binding.layoutMonthSchedule.removeAllViews()
            if (hasAnySchedule) {
                val lastIndex = scheduleContainer.indexOfLast { it != null }
                scheduleContainer.take(lastIndex + 1)
                    .map { it ?: makeDefaultScheduleTextView() }
                    .forEach {
                        binding.layoutMonthSchedule.addView(it)
                    }
            }
        }

        private fun setDateCellTextDesign(item: CalendarDate) {
            val textColor = when (item.dayType) {
                DayType.HOLIDAY, DayType.SUNDAY -> holidayColor
                DayType.SATURDAY -> saturdayColor
                else -> weekDayColor
            }
            if (item.date == LocalDate.now()) {
                binding.tvMonthDay.setBackgroundResource(R.drawable.bg_calendar_today)
                binding.tvMonthDay.tintStroke(weekDayColor, context().dp2px(0.3f))
            } else {
                binding.tvMonthDay.setBackgroundResource(0)
            }
            binding.tvMonthDay.setTextColor(textColor)
            (binding.tvMonthDay.layoutParams as FrameLayout.LayoutParams).gravity = calendarDesign.textAlign
            binding.tvMonthDay.setTextSize(TypedValue.COMPLEX_UNIT_DIP, calendarDesign.textSize)
        }

        // make sorted schedule list with white padding
        private fun makePaddingScheduleList(item: CalendarDate, schedules: List<CalendarScheduleObject>): Array<TextView?> {
            val filteredScheduleList = schedules.filter {
                item.dayType != DayType.PADDING &&
                        it.startDate.toLocalDate() <= item.date &&
                        item.date <= it.endDate.toLocalDate()
            }

            val scheduleListContainer = arrayOfNulls<TextView>(calendarDesign.visibleScheduleCount)
            filteredScheduleList.take(calendarDesign.visibleScheduleCount)
                .forEach {
                    val paddingKey = "${adapterPosition / CALENDAR_COLUMN_SIZE}:${it.id}"
                    val paddingLineIndex = lineIndex[paddingKey]?.savedLineIndex
                    val lastSelectedPosition = lineIndex[paddingKey]?.lastSelectedPosition
                    val isFirstShowSchedule = item.date == it.startDate.toLocalDate() ||
                            item.dayType == DayType.SUNDAY ||
                            adapterPosition == lastSelectedPosition

                    if (paddingLineIndex != null) {
                        scheduleListContainer[paddingLineIndex] = mappingScheduleTextView(it, isFirstShowSchedule)
                    } else {
                        for (i in scheduleListContainer.indices) {
                            if (scheduleListContainer[i] == null) {
                                scheduleListContainer[i] = mappingScheduleTextView(it, true)
                                lineIndex[paddingKey] = MonthCellPositionStore(i, adapterPosition)
                                break
                            }
                        }
                    }
                }
            // 보여줄 갯수만 뽑아서 반환 SCHEDULE_CONTAINER_SIZE 보다 크면 안됨
            return scheduleListContainer.sliceArray(0 until calendarDesign.visibleScheduleCount)
        }

        private fun mappingScheduleTextView(it: CalendarScheduleObject, isFirstShowSchedule: Boolean): TextView {
            val startMargin = if (isFirstShowSchedule) 2f else 0f
            val textView = makeDefaultScheduleTextView(startMargin)
            textView.layoutParams
            textView.text = if (isFirstShowSchedule) it.text else ""
            textView.setTextColor(Color.WHITE)
            textView.setBackgroundColor(it.color)
            return textView
        }

        private fun makeDefaultScheduleTextView(startMargin: Float = 0f): TextView {
            val textView = TextView(itemView.context)
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(context().dp2px(startMargin).toInt(), 0, 0, context().dp2px(2.0f).toInt())
            textView.includeFontPadding = false
            textView.isSingleLine = true
            textView.layoutParams = layoutParams
            textView.gravity = Gravity.CENTER_VERTICAL
            textView.maxLines = 1
            textView.ellipsize = TextUtils.TruncateAt.END
            val textSize = calculateHeight / SCHEDULE_HEIGHT_DIVIDE_RATIO
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
            textView.setPadding(context().dp2px(2.0f).toInt(), 0, 0, context().dp2px(2.0f).toInt())
            return textView
        }

        private fun notifyChangedSelectPosition(position: Int) {
            val selectedTemp = selectedPosition
            selectedPosition = position

            if (selectedTemp != -1) {
                currentList[selectedTemp].isSelected = false
                notifyItemChanged(selectedTemp)
            }

            currentList[position].isSelected = true
            notifyItemChanged(position)
        }

        private fun notifyClickEventType() {
            if (selectedPosition == adapterPosition) {
                onDaySecondClickListener?.onDayClick(currentList[adapterPosition].date, adapterPosition)
            } else {
                onDayClickListener?.onDayClick(currentList[adapterPosition].date, adapterPosition)
            }
        }
    }

    companion object {

        private const val CALENDAR_COLUMN_SIZE = 7

        private const val SCHEDULE_HEIGHT_DIVIDE_RATIO = 10
    }
}
