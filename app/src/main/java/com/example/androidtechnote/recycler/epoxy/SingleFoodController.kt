package com.example.androidtechnote.recycler.epoxy

import com.airbnb.epoxy.EpoxyController
import com.example.androidtechnote.recycler.epoxy.model.Food

class SingleFoodController : EpoxyController(){

    var foodItems : List<Food>

    init {
        foodItems = FoodDataFactory.getFoodItems(50)
    }

    override fun buildModels() {
        var i:Long =0

        /*foodItems.forEach {food ->
            SingleFoodModel_()
                .id(i++)
                .image(food.image)
                .title(food.title)
                .desc(food.description)
                .addTo(this)
        }*/
    }
}