package com.tnco.runar.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import coil.load
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tnco.runar.R
import com.tnco.runar.databinding.BottomSheetLayoutBinding

class BottomSheetFragment(
    private val tHeading: String?,
    private val tDescription: String?,
    private val tImageUrl: String?
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetLayoutBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return BottomSheetLayoutBinding.inflate(inflater, container, false)
            .also { binding ->
                _binding = binding
            }
            .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            heading.text = tHeading
            description.text = tDescription
            image.load(tImageUrl)

            crossButton.setOnClickListener {
                onDestroyView()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "BottomSheetFragment"
    }
}