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

    // Déclaration de la variable SharedPreferences pour sauvegarder et récupérer les préférences
    private lateinit var sharedPreferences: SharedPreferences

    // Méthode appelée lors de la création de l'activité
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        // Initialisation de SharedPreferences pour accéder aux préférences de l'application
        sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)

        // Récupération de la langue enregistrée dans SharedPreferences, sinon la langue par défaut est "en"
        val language = sharedPreferences.getString("language", "en")
        // Mise à jour de la langue de l'application en fonction de la préférence enregistrée
        updateLocale(language ?: "en")

        // Initialisation du bouton "Next" et définition de son action au clic
        val nextButton: Button = findViewById(R.id.next_button)
        nextButton.setOnClickListener {
            // Lorsque le bouton "Next" est cliqué, l'application passe à MainActivity
            startActivity(Intent(this, MainActivity::class.java))
            finish()  // Ferme l'activité actuelle
        }
    }

    // Méthode pour mettre à jour la langue de l'application en fonction de la sélection
    private fun updateLocale(language: String) {
        // Choix de la locale en fonction de la langue sélectionnée
        val locale = when (language) {
            "en" -> Locale("en", "US")  // Anglais
            "ar" -> Locale("ar", "AE")  // Arabe
            "fr" -> Locale("fr", "FR")  // Français
            else -> Locale("en", "US")  // Par défaut, l'anglais
        }

        // Configuration des ressources pour appliquer la nouvelle locale
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        // Sauvegarde de la langue sélectionnée dans SharedPreferences
        val editor = sharedPreferences.edit()
        editor.putString("language", language)
        editor.apply()
    }
}
