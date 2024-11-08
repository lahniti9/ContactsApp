package com.example.mycontactapp
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.mycontactapp.MainActivity
import com.example.mycontactapp.R
import java.util.*

class WelcomeActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)


        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)

        // Set the language from SharedPreferences
        val language = sharedPreferences.getString("language", "en")
        updateLocale(language ?: "en")

        val nextButton: Button = findViewById(R.id.next_button)
        nextButton.setOnClickListener {
            // When next button is clicked, navigate to MainActivity
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    // Method to update the locale based on selected language
    private fun updateLocale(language: String) {
        val locale = when (language) {
            "en" -> Locale("en", "US")
            "ar" -> Locale("ar", "AE")
            "fr" -> Locale("fr", "FR")
            else -> Locale("en", "US")
        }
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        // Save the selected language in SharedPreferences
        val editor = sharedPreferences.edit()
        editor.putString("language", language)
        editor.apply()
    }
}
