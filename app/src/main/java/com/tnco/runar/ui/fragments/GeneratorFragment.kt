package com.tnco.runar.ui.fragments

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tnco.runar.R
import com.tnco.runar.databinding.FragmerntLayoutGeneratorBinding
import com.tnco.runar.extensions.observeOnce
import com.tnco.runar.presentation.viewmodel.GeneratorViewModel

class GeneratorFragment : Fragment() {
    val viewModel: GeneratorViewModel by viewModels()
    private var _binding: FragmerntLayoutGeneratorBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmerntLayoutGeneratorBinding.inflate(inflater, container, false)

        viewModel.fontSize.observeOnce(this, {
            binding.tvToolbar.setTextSize(TypedValue.COMPLEX_UNIT_PX, (it * 1.35).toFloat())
        })

        binding.generatorStav.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragmentContainer, GeneratorStartFragment())
                ?.commit()
        }
        return binding.root
    }

}

