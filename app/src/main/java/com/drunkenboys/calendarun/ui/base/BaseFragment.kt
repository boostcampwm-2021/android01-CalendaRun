package com.drunkenboys.calendarun.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding

// for view binding
open class BaseViewFragment<T : ViewBinding>(private val inflate: ((LayoutInflater, ViewGroup?, Boolean) -> T)? = null) : Fragment() {

    var _binding: T? = null
    val binding get() = _binding ?: throw IllegalStateException(ERROR_BINDING_INITIALIZE)

    val navController by lazy { findNavController() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflate?.let {
            _binding = it.invoke(inflater, container, false)
            binding.root
        } ?: super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        private const val ERROR_BINDING_INITIALIZE = "binding 초기화 실패"
    }
}

// for data binding
open class BaseFragment<T : ViewDataBinding>(private val layoutId: Int) : BaseViewFragment<T>() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.lifecycleOwner = viewLifecycleOwner
    }
}
