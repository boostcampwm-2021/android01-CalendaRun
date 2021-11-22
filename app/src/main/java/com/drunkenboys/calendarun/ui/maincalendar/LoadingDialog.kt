package com.drunkenboys.calendarun.ui.maincalendar

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.airbnb.lottie.LottieDrawable
import com.drunkenboys.calendarun.databinding.DialogLoadingBinding

/*
* @params isWaiting
* ture : 따로 Dismiss()를 호출 할 때까지 무한 반복
* false : 60frame 실행 후 자동 종료
* */
class LoadingDialog(private var isWaiting: Boolean = false) : DialogFragment() {

    private var _binding: DialogLoadingBinding? = null
    private val binding get() = _binding!!


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window?.setDimAmount(0.3f)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogLoadingBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lottieLoading.setAnimation(LOTTIE_ANIMATION_PATH)
        binding.lottieLoading.speed = 3.5f
        if (!isWaiting) {
            binding.lottieLoading.setMaxFrame(60)
        } else {
            binding.lottieLoading.repeatCount = LottieDrawable.INFINITE
        }
        binding.lottieLoading.playAnimation()
        binding.lottieLoading.addAnimatorListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                if (!isWaiting) {
                    dismiss()
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val LOTTIE_ANIMATION_PATH = "switching_calendar.json"
    }
}
