package com.example.androidtechnote.recycler.epoxy

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import com.airbnb.epoxy.*
import com.example.androidtechnote.R
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

@EpoxyModelClass(layout = R.layout.singlefood_layout)
abstract class SingleFoodModel : EpoxyModelWithHolder<SingleFoodModel.FoodHolder>(){

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

    //KotlinHolder 사용
    inner class FoodHolder : KotlinHolder() {
        val imageView by bind<ImageView>(R.id.epoxy_image)
        val titleView by bind<TextView>(R.id.epoxy_title)
        val descView by bind<TextView>(R.id.epoxy_desc)
    }
}


//EpoxyHolder 를 상속받고있으며 Model 의 bind 를 도와줌
abstract class KotlinHolder : EpoxyHolder() {
    private lateinit var view: View

    override fun bindView(itemView: View) {
        view = itemView
    }

    protected fun <V : View> bind(id: Int): ReadOnlyProperty<KotlinHolder, V> =
        Lazy { holder: KotlinHolder, prop ->
            holder.view.findViewById(id) as V?
                ?: throw IllegalStateException("View ID $id for '${prop.name}' not found.")
        }

    private class Lazy<V>(private val initializer: (KotlinHolder, KProperty<*>) -> V) :
        ReadOnlyProperty<KotlinHolder, V> {

        private object EMPTY

        private var value: Any? = EMPTY

        override fun getValue(thisRef: KotlinHolder, property: KProperty<*>): V {
            if (value == EMPTY) {
                value = initializer(thisRef, property)
            }
            @Suppress("UNCHECKED_CAST")
            return value as V
        }
    }
}