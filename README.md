# CKS Calendar

[![jitpack-shield]][jitpack-url] [![gmail-shield]][gmail-url] [![google-shield]][배포링크]

<!-- TABLE OF CONTENTS -->
<details open="open">
    <summary>CKS Calendar 라이브러리</summary>
        <ol>
            <li><a href="#설치법">설치법</a></li>
            <li>
                <a href="#사용법">사용법</a>
                <ul>
                    <li><a href="#제공-함수">제공 함수</a></li>
                    <li><a href="#리스너">리스너</a></li>
                </ul>
            </li>
            <li><a href="#커스텀-테마">커스텀</a></li>
            <li><a href="#관련-링크">관련 링크</a></li>
            <li><a href="#기여">기여</a></li>
        </ol>
</details>

<!-- TABLE OF CONTENTS -->
<details open="open">
  <summary>달려달력 앱</summary>
  <ol>
    <li><a href="#설치">설치</a></li>
    <li><a href="#Preview">Preview</a></li>
    <li><a href="#용어-정리">용어 정리</a></li>
    <li><a href="#기술-스택">기술 스택</a></li>
  </ol>
</details>

**CalendarSet**
```
달력의 한 페이지로, 한 화면에 보여줄 기간
```
**MonthCalendarView**
```
가로 스와이프로 달력을 보여줍니다.
```
**YearCalendarView**
```
세로 스크롤로 달력을 보여줍니다.
```
<img src="https://i.imgur.com/ajouDWe.png" width=350 alt="월달력"/> <img src="https://i.imgur.com/SxdTJro.png" width=350 alt="연달력"/>

## 설치법

Step 1. JitPack repository를 prject  build.gradle에 추가합니다.

```groovy
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```

Step 2. dependency를 추가합니다.

```groovy
dependencies {
    implementation 'com.github.boostcampwm-2021:android01-CalendaRun:${version}'
}
```

## 사용법

### 제공 함수

```kotlin
fun setOnDateClickListener(onDateClickListener: OnDayClickListener)
fun setOnDaySecondClickListener(onDateSecondClickListener: OnDaySecondClickListener)
fun setSchedule(schedule: CalendarScheduleObject)
fun setSchedules(schedules: List<CalendarScheduleObject>)
fun setTheme(designObject: CalendarDesignObject)
fun resetTheme()
fun getDaySchedules(day: LocalDateTime): List<CalendarScheduleObject>
fun setCalendarSetList(calendarSetList: List<CalendarSet>)
fun setupDefaultCalendarSet()
```

### 리스너
1. `YearCalendarView` 또는 `MonthCalendarView`를 레이아웃 또는 뷰 계층에 추가합니다.
2. 필요하다면 `OnDayClickListener` 또는 `onDaySecondClickListener` 를 추가할 수 있습니다.
- `onDayClickListner`는 날짜를 선택할 때 실행됩니다.
- `onDaySecondClickListner`는 클릭된 날짜를 선택할 때 실행됩니다.

Example:

```xml
<com.drunkenboys.ckscalendar.yearcalendar.YearCalendarView
    android:id="@+id/calendar_year"
    app:onDayClick="@{(date, position) -> dayClickEvent(date)}"
    app:onDaySecondClick="@{(date, position) -> daySecondClickEvent(date)}" />

<com.drunkenboys.ckscalendar.monthcalendar.MonthCalendarView
    android:id="@+id/calendar_month"
    app:onDayClick="@{(date, position) -> dayClickEvent(date)}"
    app:onDaySecondClick="@{(date, position) -> daySecondClickEvent(date)}" />
```

### 일정 추가

```kotlin
data class CalendarScheduleObject(
    val id: Int,
    val color: Int,
    val text: String,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val isHoliday: Boolean = false
)
```

```kotlin
val scheduleList = listOf(
    CalendarScheduleObject(
        0,
        ScheduleColorType.ORANGE.color,
        "Schedule01",
        LocalDateTime.now(),
        LocalDateTime.now()
    ),
    CalendarScheduleObject(
        1,
        ScheduleColorType.BLUE.color,
        "Schedule02",
        LocalDateTime.now(),
        LocalDateTime.now()
    ),
    CalendarScheduleObject(
        2,
        ScheduleColorType.GRAY.color,
        "Schedule03",
        LocalDateTime.now(),
        LocalDateTime.now()
    ),
)
binding.calendarMonth.setSchedules(scheduleList)
binding.calendarYear.setSchedules(scheduleList)
```
### 적용 사진

<img src="https://i.imgur.com/7yqLL0G.jpg" width=350 alt="월달력"/> <img src="https://i.imgur.com/28WvNjs.jpg" width=350 alt="월달력"/>

### 커스텀 테마

`XML`에서는 아래의 속성들을, 프로그래밍적 수정으로는 `CalendarDesignObject`를 통해 달력의 커스텀 요소를 지원합니다

#### XML
| Attribute  | Type |description|
|---|---|---|
|  weekDayTextColor | Color  | 평일 색상|
| holidayTextColor  |  Color |공휴일 색상|
|  saturdayTextColor | Color  |토요일 색상|
|  sundayTextColor |  Color | 일요일 색상|
|  selectedFrameColor | Color  | 날짜 선택 테두리 색상 Stroke 색상을 변경|
|  backgroundColor | Color  |달력 배경 색상|
|  selectedFrameDrawable | Shape Drawable |날짜 선택 테두리|
|  visibleScheduleCount | Integer  | 보여질 일정 개수(5개 이하)|

#### CalendarDesignObject
```kotlin 
data class CalendarDesignObject(
    @ColorInt var weekDayTextColor: Int = Color.BLACK,
    @ColorInt var holidayTextColor: Int = ScheduleColorType.RED.color,
    @ColorInt var saturdayTextColor: Int = ScheduleColorType.BLUE.color,
    @ColorInt var sundayTextColor: Int = ScheduleColorType.RED.color,
    @ColorInt var selectedFrameColor: Int = ScheduleColorType.GRAY.color,
    @ColorInt var backgroundColor: Int = Color.WHITE,
    @DrawableRes var selectedFrameDrawable: Int = R.drawable.bg_month_date_selected,
    var textSize: Float = 10f,
    var textAlign: Int = Gravity.CENTER,
    val weekSimpleStringSet: List<String> = listOf("일", "월", "화", "수", "목", "금", "토"),
    val weekFullStringSet: List<String> = listOf("일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"),
    var visibleScheduleCount: Int = 3
) 
```

## 관련 링크

모든 문서는 [위키][위키]에 저장되어 있습니다.
- [백로그]
- [화이트보드]
- [프로토타입]
<!-- CONTRIBUTING -->
## 기여

1. Fork Project
2. 이슈 등록
3. Feature Branch 생성 (`git checkout -b feature/#IssueNumber`)
4. Commit (`git commit -m 'Issue-#{Number} feat: AmazingFeature'`)
5. Push Branch (`git push origin feature/#IssueNumber`)
6. Pull Request 요청

<!-- # 개발 가이드 샘플

**네이버 클로바 개발 가이드**
(범주) 클로바는 네이버가 개발 및 서비스하고 있는 인공지는 플랫폼입니다.

(용도) 클로바는 사용자의 음성이나 이미지를 인식하고 이를 분석하여 사용자가 원하는 정보나 서비스를 제공합니다.

(특징) 서드파티 개발자는 클로바가 가진 기술을 활용하여 인공 지능 서비스를 제공하는 기기 또는 가전제품을 만들거나 보유하고 있는 콘텐츠나 서비스를 클로바를 통해 사용자에게 제공할 수 있습니다.

**아마존 S3**
Amazon Sinple Storage Service(Amazon S3)는 인터넷 스토리지 서비스입니다.

Amazon S3를 사용하면 인터넷을 통해 언제 어디서든 원하는 양의 데이터를 저장하고 검색할 수 있습니다.

AWS Management Console의 간단하고 직관적인 웹 인터페이스를 통해 이러한 작업을 수행할 수 있습니다. -->


# 달려달력

<img src="https://i.imgur.com/UlaKxI9.jpg" width=100 />


달려달력은 부스트캠프 웹•모바일 6기 Andrunken 팀이 개발 및 서비스하고 있는 달력 애플리케이션입니다.

달려달력은 사용자가 원하는 대로 달력을 구성하고 일정을 관리할 수 있습니다.

**달력 관리**

![untitled](https://user-images.githubusercontent.com/50517813/144752870-52695fb2-f516-4ea4-9684-e6e9aed5f3f9.gif)
사용자는 새 달력을 추가할 수 있고, 달력들을 관리할 수 있으며, 수정, 삭제도 가능합니다.

** 커스텀 달력

![untitled (1)](https://user-images.githubusercontent.com/50517813/144752958-39701b65-269f-454b-963e-169fb7c0e931.gif)
사용자는 달력의 한 페이지를 월 단위(30일)가 아닌 임의의 기간을 설정할 수 있습니다.
또한 스와이프를 통해 달력의 일부 기간을 따로 확인할 수 있습니다.

**일정 관리**

![ezgif com-gif-maker](https://user-images.githubusercontent.com/50517813/144752927-e9a4cc36-5861-47ee-b103-4e570d9cdeab.gif)
달력에 일정을 추가하여 관리할 수 있습니다.
일정은 시간, 메모를 기록할 수 있고, 알림을 설정하여 선택된 시간에 알림을 받을 수 있습니다.
또한 달력에 표시될 일정의 색을 선택할 수 있습니다.

**일정 검색**

![ezgif com-gif-maker_(1)](https://user-images.githubusercontent.com/50517813/144752990-2f28620b-2951-4856-9465-1a5ce14dbb71.gif)
일정에 알림을 설정하여 선택된 시간에 알림을 받을 수 있습니다.

**홈 화면 위젯**

![홈 화면 위젯](https://user-images.githubusercontent.com/50517813/144752995-371236b8-78dd-425b-882d-8a8858f28e0b.PNG)
홈 화면에 위젯을 추가하여 오늘 일정을 확인할 수 있습니다.

**테마 변경**

다크 모드를 지원합니다.
달력의 디자인(달력, 글자 색 등)을 변경할 수 있습니다.

**공휴일 지원**

달력에 공휴일을 일정처럼 표시해서 보여줍니다.


## 설치

<a href='https://play.google.com/store/apps/details?id=com.drunkenboys.calendarun'><img src='https://simplemobiletools.com/images/button-google-play.svg' alt='Get it on Google Play' height='45' /></a>

## Preview
[데모 영상](https://www.youtube.com/watch?v=GQ5P8aAdSJs&feature=youtu.be)

## 용어 정리

**달력**

사용자의 최종 일정 또는 목표 (수능, 프로젝트 등)

**슬라이스**

달력의 한 페이지로 한 화면에 보여줄 기간

**일정**
단기간에 이룰 목표

## 기술 스택
- Android Jetpack
    - Lifecycle
    - Databinding
    - Navigation
    - Room
    - Hilt
- Coroutine / Flow
- MVVM
- Retrofit2

<!-- url 변수-->
[forks-shield]: https://img.shields.io/github/forks/boostcampwm-2021/android01-CalendaRun.svg?style=for-the-badge
[forks-url]: https://github.com/boostcampwm-2021/android01-CalendaRun/network/members
[stars-shield]: https://img.shields.io/github/stars/boostcampwm-2021/android01-CalendaRun.svg?style=for-the-badge
[stars-url]: https://github.com/boostcampwm-2021/android01-CalendaRun/stargazers
[issues-shield]: https://img.shields.io/github/issues/boostcampwm-2021/android01-CalendaRun.svg?style=for-the-badge
[issues-url]: https://github.com/boostcampwm-2021/android01-CalendaRun/issues
[gmail-shield]: https://img.shields.io/badge/gmail-EA4335?style=flat-square&logo=gmail&logoColor=white
[gmail-url]: mailto:andrunken1107@gmail.com
[google-shield]: https://img.shields.io/badge/Google-Play-414141?style=flat-square&logo=GooglePlay&logoColor=white
[product-screenshot]: images/screenshot.png
[위키]: https://github.com/boostcampwm-2021/android01-CalendaRun/wiki
[사용법]: .
[커스텀 가능 요소]: .
[배포링크]: https://play.google.com/store/apps/details?id=com.drunkenboys.calendarun
[플레이스토어 이미지]: https://simplemobiletools.com/images/button-google-play.svg?
[앱 아이콘]: https://i.imgur.com/UlaKxI9.jpg
[프로토타입]: https://www.figma.com/file/f1V4JJinMyF0ZPv5Krqa39/Untitled?node-id=0%3A1
[화이트보드]: https://www.figma.com/file/vAAmBNwDsIiC9YzSEiSxCv/Untitled?node-id=0%3A1
[백로그]: https://docs.google.com/spreadsheets/d/11td2bnmG7gzeYL5YFO96Hj5Sy3nQfgub6IWgPe18LpA/edit#gid=2081874115
[월달력]: https://i.imgur.com/ajouDWe.png
[연달력]: https://i.imgur.com/SxdTJro.png
[jitpack-shield]: https://jitpack.io/v/boostcampwm-2021/android01-CalendaRun.svg
[jitpack-url]: https://jitpack.io/#boostcampwm-2021/android01-CalendaRun
