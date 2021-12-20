# Year Calendar View Hierarchy

```
|-- YearCalendarView(LinearLayout)
|   |-- WeekHeader(요일 표시)
|   |-- CalendarLazyColumn(전체 달력)
|   |   |-- MonthHeader(월 표시)
|   |   |-- YearHeader(년 표시)
|   |   |-- WeekCalendar(1주일 달력)
|   |   |   |-- DayText(날짜 표시)
|   |   |   |-- ScheduleText(일정 표시)
```