package com.dc249.privatetype
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        findViewById<TextView>(R.id.privacy_status).text = "Privacy Audit: No Internet permission declared.\nMode: Offline-only."
    }
}