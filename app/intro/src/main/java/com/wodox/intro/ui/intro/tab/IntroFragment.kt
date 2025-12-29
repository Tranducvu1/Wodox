package com.wodox.intro.ui.intro.tab

import android.os.Bundle
import com.bumptech.glide.Glide
import com.wodox.core.base.fragment.BaseFragment
import com.wodox.core.extension.parcelable
import com.wodox.intro.BR
import com.wodox.intro.R
import com.wodox.intro.databinding.FragmentIntroBinding
import com.wodox.intro.model.IntroData
import com.wodox.intro.ui.intro.IntroViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IntroFragment : BaseFragment<FragmentIntroBinding, IntroViewModel>(
    IntroViewModel::class
) {

    private val intro by lazy {
        arguments?.parcelable<IntroData>(KEY_INTRO)
    }

    override fun layoutId(): Int = R.layout.fragment_intro

    override fun initialize() {
        binding.setVariable(BR.intro, intro)
        intro?.bgResId?.let { resId ->
            Glide.with(requireContext())
                .load(resId)
                .into(binding.ivImage)
        }
    }

    companion object {
        private const val KEY_INTRO = "key_intro"

        @JvmStatic
        fun newInstance(intro: IntroData?) = IntroFragment().apply {
            arguments = Bundle().apply {
                putParcelable(KEY_INTRO, intro)
            }
        }
    }
}
