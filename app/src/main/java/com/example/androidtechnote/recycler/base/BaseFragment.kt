package com.example.androidtechnote.recycler.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation


enum class FocusDirection {
    PREV, NEXT, OK
}

interface CallBack {
    fun onClick(focusDirection: FocusDirection)
}

abstract class BaseFragment<T: ViewDataBinding>(private val layoutId: Int) : Fragment(){
    private var _binding: T? = null
    val binding: T get() = _binding!!

    lateinit var mContext: Context
    lateinit var navController: NavController

    val mCallBack = object : CallBack {
        override fun onClick(focusDirection: FocusDirection) {
            when (focusDirection) {
                FocusDirection.PREV -> {
                    prevFocus()
                }
                FocusDirection.NEXT ->{
                    nextFocus()
                }
                FocusDirection.OK -> {
                    okFocus()
                }
            }
        }
    }

    //return layout resource id
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(binding.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.unbind()
    }

    abstract fun prevFocus()
    abstract fun nextFocus()
    abstract fun okFocus()
}