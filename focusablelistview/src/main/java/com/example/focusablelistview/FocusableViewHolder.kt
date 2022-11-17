package com.example.focusablelistview

import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.focusablelistview.databinding.ItemFocusableBinding

class FocusableViewHolder(val binding: ItemFocusableBinding) : RecyclerView.ViewHolder(binding.root){

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
            .setDuration(150)
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

    fun reduceAnim(){
        binding.apply {
            root.animate().scaleX(1.0f).scaleY(1.0f).setDuration(350).setStartDelay(300).translationZ(0f).start()
            title.animate().alpha(0.0f).duration = 300
        }
    }
}