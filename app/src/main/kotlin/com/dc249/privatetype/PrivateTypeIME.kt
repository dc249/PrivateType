package com.dc249.privatetype

import android.inputmethodservice.InputMethodService
import android.view.View
import android.view.KeyEvent
import android.widget.Button
import android.widget.Toast
import androidx.core.view.inputmethod.InputConnectionCompat
import androidx.core.view.inputmethod.InputContentInfoCompat
import androidx.core.content.FileProvider
import java.io.File

class PrivateTypeIME : InputMethodService() {

    private var isCaps = false

    override fun onCreateInputView(): View {
        val view = layoutInflater.inflate(R.layout.keyboard_main, null)
        
        // List of all letter buttons
        val keys = listOf(
            R.id.key_q, R.id.key_w, R.id.key_e, R.id.key_r, R.id.key_t, R.id.key_y, R.id.key_u, R.id.key_i, R.id.key_o, R.id.key_p,
            R.id.key_a, R.id.key_s, R.id.key_d, R.id.key_f, R.id.key_g, R.id.key_h, R.id.key_j, R.id.key_k, R.id.key_l,
            R.id.key_z, R.id.key_x, R.id.key_c, R.id.key_v, R.id.key_b, R.id.key_n, R.id.key_m
        )

        // Assign click listener to each letter
        keys.forEach { id ->
            view.findViewById<Button>(id).setOnClickListener { 
                val b = it as Button
                var text = b.text.toString()
                currentInputConnection.commitText(text, 1)
            }
        }

        // Space
        view.findViewById<Button>(R.id.key_space).setOnClickListener {
            currentInputConnection.commitText(" ", 1)
        }

        // Backspace
        view.findViewById<Button>(R.id.key_backspace).setOnClickListener {
            currentInputConnection.deleteSurroundingText(1, 0)
        }

        // Enter
        view.findViewById<Button>(R.id.key_enter).setOnClickListener {
            currentInputConnection.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
        }

        // Shift (Simplistic toggle)
        view.findViewById<Button>(R.id.key_shift).setOnClickListener {
            isCaps = !isCaps
            keys.forEach { id ->
                val b = view.findViewById<Button>(id)
                b.text = if (isCaps) b.text.toString().uppercase() else b.text.toString().lowercase()
            }
        }

        // GIF button
        view.findViewById<Button>(R.id.key_gif).setOnClickListener {
            val gifDir = File(filesDir, "gifs")
            val files = gifDir.listFiles()
            if (files.isNullOrEmpty()) {
                Toast.makeText(this, "No GIFs found. Import in Settings app.", Toast.LENGTH_SHORT).show()
            } else {
                shareGif(files[0]) // Share the first available GIF
            }
        }

        return view
    }

    private fun shareGif(file: File) {
        val contentUri = FileProvider.getUriForFile(this, "com.dc249.privatetype.fileprovider", file)
        val description = android.content.ClipDescription("GIF", arrayOf("image/gif"))
        val info = InputContentInfoCompat(contentUri, description, null)

        InputConnectionCompat.commitContent(
            currentInputConnection, currentInputEditorInfo, info,
            InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION, null
        )
    }
}
