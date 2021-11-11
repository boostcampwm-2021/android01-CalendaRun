package com.drunkenboys.calendarun.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.viewbinding.ViewBinding

// for view binding
open class BaseViewActivity<T : ViewBinding>(private val inflate: ((LayoutInflater) -> T)?) : AppCompatActivity() {

    lateinit var binding: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflate?.let {
            binding = it.invoke(layoutInflater)
            setContentView(binding.root)
        }
    }
}

// for data binding
open class BaseActivity<T : ViewDataBinding>(private val layoutId: Int) : BaseViewActivity<T>(null) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutId)
        binding.lifecycleOwner = this
    }
}
