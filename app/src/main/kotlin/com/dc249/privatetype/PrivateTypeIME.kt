package com.dc249.privatetype

import android.inputmethodservice.InputMethodService
import android.view.*
import android.widget.*
import android.graphics.Color
import androidx.core.view.inputmethod.InputConnectionCompat
import androidx.core.view.inputmethod.InputContentInfoCompat
import androidx.core.content.FileProvider
import java.io.File

class PrivateTypeIME : InputMethodService() {

    private val corrections = mapOf("teh" to "the", "im" to "I'm", "dont" to "don't", "youre" to "you're")

    override fun onCreateInputView(): View {
        val root = layoutInflater.inflate(R.layout.keyboard_main, null)
        
        val keyIds = listOf(
            R.id.key_q, R.id.key_w, R.id.key_e, R.id.key_r, R.id.key_t, R.id.key_y, R.id.key_u, R.id.key_i, R.id.key_o, R.id.key_p,
            R.id.key_a, R.id.key_s, R.id.key_d, R.id.key_f, R.id.key_g, R.id.key_h, R.id.key_j, R.id.key_k, R.id.key_l,
            R.id.key_z, R.id.key_x, R.id.key_c, R.id.key_v, R.id.key_b, R.id.key_n, R.id.key_m
        )

        keyIds.forEach { id ->
            val view = root.findViewById<TextView>(id)
            view.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    currentInputConnection.commitText(view.text, 1)
                    v.isPressed = true
                } else if (event.action == MotionEvent.ACTION_UP) { v.isPressed = false }
                true
            }
        }

        root.findViewById<TextView>(R.id.key_space).setOnClickListener {
            handleAutoCorrect()
            currentInputConnection.commitText(" ", 1)
        }

        root.findViewById<TextView>(R.id.key_backspace).setOnClickListener {
            currentInputConnection.deleteSurroundingText(1, 0)
        }

        val periodKey = root.findViewById<TextView>(R.id.key_period)
        periodKey.setOnClickListener { currentInputConnection.commitText(".", 1) }
        periodKey.setOnLongClickListener {
            showSymbolPopup(periodKey)
            true
        }

        return root
    }

    private fun handleAutoCorrect() {
        val ic = currentInputConnection
        val before = ic.getTextBeforeCursor(15, 0)?.toString() ?: ""
        val lastWord = before.split(" ").lastOrNull() ?: ""
        corrections[lastWord.lowercase()]?.let {
            ic.deleteSurroundingText(lastWord.length, 0)
            ic.commitText(it, 1)
        }
    }

    private fun showSymbolPopup(anchor: View) {
        val popupView = LinearLayout(this).apply {
            orientation = LinearLayout.horizontal
            backgroundColor = Color.parseColor("#303134")
            elevation = 10f
        }
        val symbols = listOf(",", "?", "!", "@", "#")
        val popup = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
        
        symbols.forEach { sym ->
            val tv = TextView(this).apply {
                text = sym
                setTextColor(Color.WHITE)
                textSize = 22sp
                setPadding(40, 20, 40, 20)
                setOnClickListener {
                    currentInputConnection.commitText(sym, 1)
                    popup.dismiss()
                }
            }
            popupView.addView(tv)
        }
        popup.showAsDropDown(anchor, 0, -anchor.height * 2)
    }
}
