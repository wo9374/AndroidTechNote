package com.example.androidtechnote.recycler.epoxy

import androidx.annotation.StringRes
import com.example.androidtechnote.R
import com.example.androidtechnote.recycler.epoxy.model.Food
import java.util.*

object FoodDataFactory{

    //랜덤 생성
    private val random = Random()
    private val titles = arrayListOf<String>("Nachos", "Fries", "Cheese Balls", "Pizza")

    private fun randomTitle() : String {
        val title = random.nextInt(4)
        return titles[title]
    }

    private fun randomPicture() : Int{
        val grid = random.nextInt(7)

        return when(grid) {
            0 -> R.drawable.ic_baseline_android_24
            1 -> R.drawable.ic_baseline_android_24
            2 -> R.drawable.ic_baseline_android_24
            3 -> R.drawable.ic_baseline_android_24
            4 -> R.drawable.ic_baseline_android_24
            5 -> R.drawable.ic_baseline_android_24
            6 -> R.drawable.ic_baseline_android_24
            else -> R.drawable.ic_baseline_android_24
        }
    }

    fun getFoodItems(count:Int) : List<Food>{
        val foodItems = mutableListOf<Food>()
        repeat(count){
            val image = randomPicture()
            val title = randomTitle()
            val desc = "test,,,," //@StringRes 지움
            foodItems.add( Food(image,title,desc) )
        }
        return foodItems
    }
}