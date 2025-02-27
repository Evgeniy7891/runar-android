package com.tnco.runar.ui.fragment

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tnco.runar.R
import com.tnco.runar.analytics.AnalyticsHelper
import com.tnco.runar.databinding.FragmentLayoutGeneratorBinding
import com.tnco.runar.enums.AnalyticsEvent
import com.tnco.runar.ui.activity.MainActivity
import com.tnco.runar.ui.viewmodel.MainViewModel
import com.tnco.runar.util.observeOnce


class GeneratorFragment : Fragment() {
    val viewModel: MainViewModel by viewModels()

    private var _binding: FragmentLayoutGeneratorBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLayoutGeneratorBinding.inflate(inflater, container, false)
        viewModel.fontSize.observeOnce(this) {
            binding.tvToolbar.setTextSize(TypedValue.COMPLEX_UNIT_PX, (it * 1.35f))
        }

        binding.generatorStav.setOnClickListener {
            checkInternetConnection()
        }

        return binding.root
    }

    private fun checkInternetConnection() {
        viewModel.isNetworkAvailable.observeOnce(viewLifecycleOwner) { status ->
            if (status) {
                moveToNextStep()
            } else {
                showInternetConnectionError()
            }
        }
    }

    private fun moveToNextStep() {
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.fragmentContainer, GeneratorStartFragment())
            ?.commit()
    }

    private fun showInternetConnectionError() {
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.fragmentContainer, ConnectivityErrorFragment())
            ?.commit()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AnalyticsHelper.sendEvent(AnalyticsEvent.GENERATOR_OPENED)
        (activity as MainActivity).showBottomBar()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

