package com.example.androidtechnote.recycler.epoxy

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.example.androidtechnote.R

@EpoxyModelClass(layout = R.layout.item_test_epoxy)
abstract class SingleFoodModel
    : EpoxyModelWithHolder<SingleFoodModel.FoodHolder>(){

    @EpoxyAttribute
    var id : Long = 0

    @EpoxyAttribute
    @DrawableRes
    var image : Int = 0

    @EpoxyAttribute
    var title:String? = ""

    @EpoxyAttribute
    var desc:String = ""

    override fun bind(holder: FoodHolder) {
        holder.imageView.setImageResource(image)
        holder.titleView.text = title
    }

    inner class FoodHolder : EpoxyHolder(){
        lateinit var imageView:ImageView
        lateinit var titleView: TextView
        lateinit var descView:TextView

        override fun bindView(itemView: View) {
            imageView = itemView.findViewById(R.id.epoxy_image)
            titleView = itemView.findViewById(R.id.epoxy_title)
            descView = itemView.findViewById(R.id.epoxy_desc)
        }
    }
}