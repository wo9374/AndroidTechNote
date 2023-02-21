package com.example.doubleaddress

import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.example.doubleaddress.databinding.LayoutDoubleAddressSpinnerBinding

class DoubleAddressSpinner : ConstraintLayout {
    var spinnerBinding: LayoutDoubleAddressSpinnerBinding = LayoutDoubleAddressSpinnerBinding.inflate(LayoutInflater.from(context), this, true)

    val searchBtn : ImageButton get() = spinnerBinding.searchIcon

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.DoubleAddressSpinner)
        setTypedArray(typedArray, context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.DoubleAddressSpinner, defStyleAttr, 0)
        setTypedArray(typedArray, context)
    }

    private fun setTypedArray(typedArray: TypedArray, context: Context) {

        spinnerBinding.apply {

            val sidoWidth = typedArray.getDimension(R.styleable.DoubleAddressSpinner_spinner1_width, 0F).toInt()
            val sigunguWidth = typedArray.getDimension(R.styleable.DoubleAddressSpinner_spinner2_width, 0F).toInt()
            val height = typedArray.getDimension(R.styleable.DoubleAddressSpinner_spinner_height, 0F).toInt()
            sidoSpinner.layoutParams = LayoutParams(sidoWidth, height).apply {
                startToStart = ConstraintSet.PARENT_ID
            }
            sigunguSpinner.layoutParams = LayoutParams(sigunguWidth, height).apply {
                startToEnd = sidoSpinner.id
            }

            val iconWidth = typedArray.getDimension(R.styleable.DoubleAddressSpinner_icon_width, 0F).toInt()
            val iconHeight = typedArray.getDimension(R.styleable.DoubleAddressSpinner_icon_height, 0F).toInt()
            searchIcon.layoutParams = LayoutParams(iconWidth, iconHeight).apply {
                startToEnd = sigunguSpinner.id
            }


            val regionAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, region)
            sidoSpinner.apply {
                adapter = regionAdapter
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        Log.d("DoubleAddressSpinner", "position : $position")
                        spinnerBinding.sigunguSpinner.adapter = when (position) {
                            1 -> ArrayAdapter(context, android.R.layout.simple_spinner_item, incheon)
                            2 -> ArrayAdapter(context, android.R.layout.simple_spinner_item, busan)
                            3 -> ArrayAdapter(context, android.R.layout.simple_spinner_item, daejeon)
                            4 -> ArrayAdapter(context, android.R.layout.simple_spinner_item, daegu)
                            5 -> ArrayAdapter(context, android.R.layout.simple_spinner_item, ulsan)
                            6 -> ArrayAdapter(context, android.R.layout.simple_spinner_item, gwangju)
                            7 -> ArrayAdapter(context, android.R.layout.simple_spinner_item, jeju)
                            8 -> ArrayAdapter(context, android.R.layout.simple_spinner_item, sejong)
                            9 -> ArrayAdapter(context, android.R.layout.simple_spinner_item, gyeonggi)
                            10 -> ArrayAdapter(context, android.R.layout.simple_spinner_item, gangwon)
                            11 -> ArrayAdapter(context, android.R.layout.simple_spinner_item, chungbuk)
                            12 -> ArrayAdapter(context, android.R.layout.simple_spinner_item, chungnam)
                            13 -> ArrayAdapter(context, android.R.layout.simple_spinner_item, gyeongbuk)
                            14 -> ArrayAdapter(context, android.R.layout.simple_spinner_item, gyeongnam)
                            15 -> ArrayAdapter(context, android.R.layout.simple_spinner_item, jeonbuk)
                            16 -> ArrayAdapter(context, android.R.layout.simple_spinner_item, jeonnam)
                            else -> ArrayAdapter(context, android.R.layout.simple_spinner_item, seoul)
                        }
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
            }

            //Preview 보기를 위한 Default Seoul
            sigunguSpinner.adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, seoul)
        }
        typedArray.recycle()
    }

    fun getSelectedItemPair() : Pair<String, String>{
        return Pair(
            spinnerBinding.sidoSpinner.selectedItem.toString(),
            spinnerBinding.sigunguSpinner.selectedItem.toString()
        )
    }
}

val Float.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()


val region = listOf(
    "서울", "인천", "부산", "대전", "대구", "울산", "광주",
    "제주", "세종",
    "경기", "강원",
    "충북", "충남", "경북", "경남", "전북", "전남"
)

val seoul = listOf(
    "전체",
    "강남구", "강동구", "강북구", "강서구", "관악구", "광진구", "구로구", "금천구", "노원구",
    "도봉구", "동대문구", "동작구", "마포구", "서대문구", "서초구", "성동구", "성북구", "송파구",
    "양천구", "영등포구", "용산구", "은평구", "종로구", "중구", "중랑구"
)

val incheon = listOf("전체", "계양구", "남동구", "동구", "미추홀구", "부평구", "서구", "연수구", "중구")

val busan = listOf(
    "전체",
    "강서구", "금정구", "남구", "동구", "동래구", "부산진구", "북구", "사상구",
    "사하구", "서구", "수영구", "연제구", "영도구", "중구", "해운대구"
)

val daejeon = listOf("전체", "대덕구", "동구", "서구", "유성구", "중구")

val daegu = listOf("전체", "남구", "달서구", "달서군", "동구", "북구", "서구", "수성구", "중구")

val ulsan = listOf("전체", "남구", "동구", "북구", "중구", "울주군")

val gwangju = listOf("전체", "광산구", "남구", "동구", "북구", "서구")

val jeju = listOf("전체", "서귀포시", "제주시")

val sejong = listOf("전체")

val gyeonggi = listOf(
    "고양시", "과천시", "광명시", "광주시", "구리시", "군포시", "김포시", "남양주시", "동두천시",
    "부천시", "성남시", "수원시", "시흥시", "안산시", "안성시", "안양시", "양주시", "여주시",
    "오산시", "용인시", "의왕시", "의정부시", "이천시", "파주시", "평택시", "포천시", "하남시",
    "화성시", "가평군", "양평군", "연천군"
)

val gangwon = listOf(
    "강릉시", "동해시", "삼척시", "속초시", "원주시", "춘천시", "태백시", "고성군", "양구군",
    "양양군", "영월군", "인제군", "정선군", "철원군", "평창군", "홍천군", "화천군", "횡성군"
)

val chungbuk = listOf(
    "제천시", "청주시", "충주시", "괴산군", "단양군", "보은군",
    "영동군", "옥천군", "음성군", "증평군", "진천군"
)

val chungnam = listOf(
    "계룡시", "공주시", "논산시", "당진시", "보령시", "서산시", "아산시",
    "천안시", "금산군", "부여군", "서천군", "예산군", "청양군", "태안군", "홍성군"
)

val gyeongbuk = listOf(
    "경산시", "경주시", "구미시", "김천시", "문경시", "상주시", "안동시", "영주시", "영천시", "포항시",
    "고령군", "군위군", "봉화군", "성주군", "영덕군", "영양군", "예천군", "울릉군", "울진군", "의성군",
    "청도군", "청송군", "칠곡군"
)

val gyeongnam = listOf(
    "거제시", "김해시", "밀양시", "사천시", "양산시", "진주시", "창원시", "통영시", "거창군", "고성군",
    "남해군", "산청군", "의령군", "창녕군", "하동군", "함안군", "함양군", "합천군"
)

val jeonbuk = listOf(
    "군산시", "김제시", "남원시", "익산시", "전주시", "정읍시", "고창군",
    "무주군", "부안군", "순창군", "완주군", "임실군", "장수군", "진안군"
)

val jeonnam = listOf(
    "광양시", "나주시", "목포시", "순천시", "여수시", "강진군", "고흥군", "곡성군", "구례군",
    "담양군", "무안군", "보성군", "신안군", "영광군", "영암군", "완도군", "장성군", "장흥군",
    "진도군", "함평군", "해남군", "화순군"
)