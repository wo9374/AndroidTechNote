package com.example.focusablelistview

import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.focusablelistview.databinding.ItemFocusableBinding

class ListViewHolder(val binding: ItemFocusableBinding) : RecyclerView.ViewHolder(binding.root){

    fun setGlide(glideUrl: String){
        Glide
            .with(binding.thumbnailImg)
            .load(glideUrl)
            .thumbnail(0.1f)
            .error(R.drawable.ic_launcher_foreground)
            .into(binding.thumbnailImg)
    }

    fun setTitle(titleText:String){
        binding.title.text = titleText
    }

    private fun expansionView() : ViewPropertyAnimatorCompat {
        return ViewCompat
            .animate(binding.root)
            .scaleX(1.14f)
            .scaleY(1.14f)
            .setDuration(130)
            .translationZ(1f)
    }

    fun expansionAnim(){
        expansionView().start()

        binding.title.animate().alpha(1.0f)
    }

    fun nextItemAnim(){
        expansionView().apply {
            translationX(14f)
            withEndAction {
                translationX(0f).setDuration(350).setStartDelay(100).start()
            }
        }.start()

        binding.title.animate().alpha(1.0f)
    }

    fun prevItemAnim(){
        expansionView().apply {
            translationX(-14f)
            withEndAction {
                translationX(0f).setDuration(350).setStartDelay(100).start()
            }
        }.start()

        binding.title.animate().alpha(1.0f)
    }

    fun setBgHighLight(boolean: Boolean){
        if (boolean)
            binding.bgHighlight.visibility = View.VISIBLE
        else
            binding.bgHighlight.visibility = View.INVISIBLE
    }

    fun appearHighLight(){
        binding.bgHighlight.animate().alpha(1.0f)
    }

    fun disappearHighLight(){
        binding.bgHighlight.animate().alpha(0.0f).duration = 300
    }

    fun reduceAnim(){
        binding.apply {
            root.animate().scaleX(1.0f).scaleY(1.0f).setDuration(350).setStartDelay(300).translationZ(0f).start()
            title.animate().alpha(0.0f).duration = 300
        }
    }
}