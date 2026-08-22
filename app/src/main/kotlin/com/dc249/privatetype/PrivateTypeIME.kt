package com.dc249.privatetype

import android.inputmethodservice.InputMethodService
import android.view.*
import android.widget.*
import android.os.Handler
import android.os.Looper

class PrivateTypeIME : InputMethodService() {

    private val handler = Handler(Looper.getMainLooper())
    private var symbolPopup: PopupWindow? = null

    // Local Auto-correct Dictionary (Privacy-Safe)
    private val corrections = mapOf(
        "teh" to "the",
        "i" to "I",
        "dont" to "don't",
        "wont" to "won't",
        "cant" to "can't",
        "id" to "I'd",
        "im" to "I'm",
        "youre" to "you're",
        "thier" to "their"
    )

    override fun onCreateInputView(): View {
        val root = layoutInflater.inflate(R.layout.keyboard_main, null)
        setupKeys(root)
        return root
    }

    private fun setupKeys(root: View) {
        // ... Standard QWERTY setup ...

        // Spacebar with Auto-Correct
        val space = root.findViewById<TextView>(R.id.key_space)
        space.setOnClickListener {
            handleAutoCorrect()
            currentInputConnection.commitText(" ", 1)
        }

        // Period Key with Long-Press Popup
        val periodKey = root.findViewById<TextView>(R.id.key_period)
        periodKey.setOnClickListener { currentInputConnection.commitText(".", 1) }
        periodKey.setOnLongClickListener {
            showSymbolPopup(periodKey)
            true
        }
    }

    private fun handleAutoCorrect() {
        val ic = currentInputConnection
        val before = ic.getTextBeforeCursor(20, 0)?.toString() ?: ""
        val lastWord = before.split(" ").lastOrNull() ?: ""
        
        if (corrections.containsKey(lastWord.lowercase())) {
            val correction = corrections[lastWord.lowercase()] ?: return
            ic.deleteSurroundingText(lastWord.length, 0)
            ic.commitText(correction, 1)
        }
    }

    private fun showSymbolPopup(anchor: View) {
        val layout = layoutInflater.inflate(R.layout.symbol_popup, null)
        symbolPopup = PopupWindow(layout, 
            ViewGroup.LayoutParams.WRAP_CONTENT, 
            ViewGroup.LayoutParams.WRAP_CONTENT, true)
        
        val symbols = listOf(",", "?", "!", "#", "@", ":", ";", "/")
        val container = layout.findViewById<LinearLayout>(R.id.popup_container)
        
        symbols.forEach { sym ->
            val tv = TextView(this).apply {
                text = sym
                textSize = 24sp
                setPadding(30, 20, 30, 20)
                setTextColor(android.graphics.Color.WHITE)
                setOnClickListener {
                    currentInputConnection.commitText(sym, 1)
                    symbolPopup?.dismiss()
                }
            }
            container.addView(tv)
        }
        
        symbolPopup?.showAsDropDown(anchor, 0, -anchor.height * 2)
    }
}
