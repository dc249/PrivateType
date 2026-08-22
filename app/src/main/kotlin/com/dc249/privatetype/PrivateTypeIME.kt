package com.dc249.privatetype
import android.inputmethodservice.InputMethodService
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import androidx.core.view.inputmethod.InputConnectionCompat
import androidx.core.view.inputmethod.InputContentInfoCompat
import androidx.core.content.FileProvider
import java.io.File

class PrivateTypeIME : InputMethodService() {
    override fun onCreateInputView(): View {
        val view = layoutInflater.inflate(R.layout.keyboard_main, null)
        view.findViewById<Button>(R.id.key_q).setOnClickListener { currentInputConnection.commitText("q", 1) }
        return view
    }
    private fun commitGif(gifFile: File) {
        val contentUri = FileProvider.getUriForFile(this, "com.dc249.privatetype.fileprovider", gifFile)
        val description = android.content.ClipDescription("GIF", arrayOf("image/gif"))
        val info = InputContentInfoCompat(contentUri, description, null)
        InputConnectionCompat.commitContent(currentInputConnection, currentInputEditorInfo, info, InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION, null)
    }
}