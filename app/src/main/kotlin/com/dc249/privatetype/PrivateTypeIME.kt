package com.dc249.privatetype

import android.inputmethodservice.InputMethodService
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ViewFlipper
import android.widget.Toast
import androidx.core.view.inputmethod.InputConnectionCompat
import androidx.core.view.inputmethod.InputContentInfoCompat
import androidx.core.content.FileProvider
import java.io.File
import kotlin.math.abs

class PrivateTypeIME : InputMethodService() {

    private var isCaps = false
    private val handler = Handler(Looper.getMainLooper())
    private var backspaceRunnable: Runnable? = null
    
    private var lastX = 0f
    private val threshold = 50f

    override fun onCreateInputView(): View {
        val root = layoutInflater.inflate(R.layout.keyboard_main, null)
        val flipper = root.findViewById<ViewFlipper>(R.id.keyboard_flipper)
        
        setupAlphabetKeys(root)
        setupSpacebar(root)
        setupBackspace(root)
        
        // Mode Toggle (?123 <-> ABC)
        root.findViewById<Button>(R.id.key_mode_toggle).setOnClickListener {
            if (flipper.displayedChild == 0) {
                flipper.displayedChild = 1
                (it as Button).text = "ABC"
            } else {
                flipper.displayedChild = 0
                (it as Button).text = "?123"
            }
        }

        root.findViewById<Button>(R.id.key_enter).setOnClickListener {
            currentInputConnection.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
        }

        return root
    }

    private fun setupAlphabetKeys(view: View) {
        val keys = listOf(
            R.id.key_q, R.id.key_w, R.id.key_e, R.id.key_r, R.id.key_t, R.id.key_y, R.id.key_u, R.id.key_i, R.id.key_o, R.id.key_p,
            R.id.key_a, R.id.key_s, R.id.key_d, R.id.key_f, R.id.key_g, R.id.key_h, R.id.key_j, R.id.key_k, R.id.key_l,
            R.id.key_z, R.id.key_x, R.id.key_c, R.id.key_v, R.id.key_b, R.id.key_n, R.id.key_m
        )
        keys.forEach { id ->
            view.findViewById<Button>(id)?.setOnClickListener { 
                currentInputConnection.commitText((it as Button).text, 1)
            }
        }
    }

    private fun setupSpacebar(view: View) {
        val space = view.findViewById<Button>(R.id.key_space)
        space.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    lastX = event.x
                    false 
                }
                MotionEvent.ACTION_MOVE -> {
                    val deltaX = event.x - lastX
                    if (abs(deltaX) > threshold) {
                        val key = if (deltaX > 0) KeyEvent.KEYCODE_DPAD_RIGHT else KeyEvent.KEYCODE_DPAD_LEFT
                        currentInputConnection.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, key))
                        lastX = event.x
                    }
                    true
                }
                MotionEvent.ACTION_UP -> {
                    if (abs(event.x - lastX) < threshold) currentInputConnection.commitText(" ", 1)
                    v.performClick()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupBackspace(view: View) {
        val backspace = view.findViewById<Button>(R.id.key_backspace)
        backspace.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    backspaceRunnable = object : Runnable {
                        override fun run() {
                            currentInputConnection.deleteSurroundingText(1, 0)
                            handler.postDelayed(this, 100)
                        }
                    }
                    handler.post(backspaceRunnable!!)
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    handler.removeCallbacks(backspaceRunnable!!)
                    true
                }
                else -> false
            }
        }
    }
}
