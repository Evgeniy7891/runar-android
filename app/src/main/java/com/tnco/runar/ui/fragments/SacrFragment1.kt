package com.tnco.runar.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.tnco.runar.R
import com.tnco.runar.databinding.FragmentLayoutInitBinding
import com.tnco.runar.databinding.FragmentLayoutSacr1Binding
import com.tnco.runar.presentation.viewmodel.InitViewModel
import com.tnco.runar.presentation.viewmodel.Sacr1ViewModel
import com.tnco.runar.ui.Navigator

class SacrFragment1  : Fragment(R.layout.fragment_layout_sacr_1), View.OnClickListener {

    private val viewModel: Sacr1ViewModel by viewModels()

    private var navigator: Navigator? = null

    private var fontSize: Float = 0f

    private var _binding: FragmentLayoutSacr1Binding? = null
    private val binding
        get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        navigator = context as Navigator
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLayoutSacr1Binding.bind(view)
        viewModel.fontSize.observe(viewLifecycleOwner) { textSize ->
            fontSize = textSize
            val headerTextSize = (textSize * 3).toFloat()
            val luckTextSize =(textSize*0.8*1.2).toFloat()
            val firstTextSize =(textSize*1.2).toFloat()
            val priceTextSize = (textSize*0.7*1.2).toFloat()
            binding.descriptionHeaderFrame.setTextSize(TypedValue.COMPLEX_UNIT_PX, headerTextSize)
            binding.sacrLuckTw.setTextSize(TypedValue.COMPLEX_UNIT_PX, luckTextSize)
            binding.sacrFirstTw.setTextSize(TypedValue.COMPLEX_UNIT_PX, firstTextSize)
            binding.mainText.setTextSize(TypedValue.COMPLEX_UNIT_PX, luckTextSize)
            binding.itemHeader1Tw.setTextSize(TypedValue.COMPLEX_UNIT_PX, luckTextSize)
            binding.itemHeader2Tw.setTextSize(TypedValue.COMPLEX_UNIT_PX, luckTextSize)
            binding.itemHeader3Tw.setTextSize(TypedValue.COMPLEX_UNIT_PX, luckTextSize)
            binding.sacrB2Price.setTextSize(TypedValue.COMPLEX_UNIT_PX, priceTextSize)
        }
    }


    override fun onDetach() {
        navigator = null
        super.onDetach()
    }



    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }
}