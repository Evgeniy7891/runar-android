package com.tnco.runar.ui.component.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.util.TypedValue
import android.view.KeyEvent
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.tnco.runar.R
import com.tnco.runar.analytics.AnalyticsHelper
import com.tnco.runar.enums.AnalyticsEvent
import com.tnco.runar.ui.Navigator
import com.tnco.runar.util.AnalyticsConstants

class CancelDialog(
    private val context: Context,
    private val fontSize: Float,
    private val page: String
) {

    fun showDialog() {
        val dialog = Dialog(context, android.R.style.ThemeOverlay)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.cancel_dialog_layout)
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        dialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        dialog.window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        dialog.window?.statusBarColor = context.getColor(R.color.library_top_bar)
        dialog.window?.navigationBarColor = context.getColor(R.color.library_top_bar)
        dialog.show()
        val buttonsFontSize = (fontSize * 0.85f)
        dialog.findViewById<TextView>(R.id.dialog_text)
            .setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize)
        dialog.findViewById<TextView>(R.id.button_yes)
            .setTextSize(TypedValue.COMPLEX_UNIT_PX, buttonsFontSize)
        dialog.findViewById<TextView>(R.id.button_no)
            .setTextSize(TypedValue.COMPLEX_UNIT_PX, buttonsFontSize)
        dialog.setOnKeyListener { _, keyCode, event ->
            var consumed = false
            if (event.action == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    consumed = true
                    dialog.dismiss()
                }
            }
            consumed
        }
        dialog.findViewById<ConstraintLayout>(R.id.dialog_element_left).setOnClickListener {
            dialog.dismiss()
        }
        dialog.findViewById<ConstraintLayout>(R.id.dialog_element_right).setOnClickListener {
            dialog.dismiss()
            AnalyticsHelper.sendEvent(
                AnalyticsEvent.SCRIPT_INTERRUPTION,
                Pair(AnalyticsConstants.PAGE, page)
            )
            (context as Navigator).agreeWithDialog()
        }
    }
}