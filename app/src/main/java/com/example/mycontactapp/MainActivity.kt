package com.example.mycontactapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var contactList: RecyclerView
    private lateinit var contactAdapter: ContactAdapter
    private val contactListData = mutableListOf<Contact>()  // Liste pour stocker les contacts
    private val originalContactList = mutableListOf<Contact>()  // Liste d'origine des contacts
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialiser les SharedPreferences
        sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)

        // Récupérer la langue sélectionnée depuis les SharedPreferences
        val language = sharedPreferences.getString("language", "en") ?: "en"
        updateLocale(language)

        setContentView(R.layout.activity_main)

        // Initialiser RecyclerView pour afficher les contacts
        contactList = findViewById(R.id.contact_list)
        contactAdapter = ContactAdapter(contactListData, this)
        contactList.layoutManager = LinearLayoutManager(this)
        contactList.adapter = contactAdapter

        // Configurer la SearchView pour filtrer les contacts
        setupSearchView()

        // Vérifier les permissions et charger les contacts si la permission est accordée
        checkPermissions()

        // Configuration du bouton pour changer la langue
        val languageButton: ImageView = findViewById(R.id.languageButton)
        languageButton.setOnClickListener {
            showLanguageMenu(it)
        }
    }

    // Méthode pour mettre à jour la langue de l'application
    private fun updateLocale(language: String) {
        val locale = when (language) {
            "en" -> Locale("en", "US")
            "ar" -> Locale("ar", "AE")
            "fr" -> Locale("fr", "FR")
            else -> Locale("en", "US")
        }
        val config = resources.configuration
        config.setLocale(locale)

        // Mettre à jour la direction de la mise en page en fonction de la langue (pour la prise en charge du RTL)
        if (locale.language == "ar") {
            config.setLayoutDirection(Locale("ar"))
        } else {
            config.setLayoutDirection(Locale("en"))
        }

        resources.updateConfiguration(config, resources.displayMetrics)
    }

    private fun showLanguageMenu(view: View) {
        // Créer un PopupMenu pour changer de langue
        val popupMenu = PopupMenu(this, view)
        menuInflater.inflate(R.menu.menu_languages, popupMenu.menu)

        // Définir un écouteur pour gérer la sélection de langue
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.english -> onLanguageSelected("en")
                R.id.arabic -> onLanguageSelected("ar")
                R.id.french -> onLanguageSelected("fr")
            }
            true
        }

        // Afficher le PopupMenu
        popupMenu.show()
    }

    private fun onLanguageSelected(language: String) {
        // Sauvegarder la langue sélectionnée dans SharedPreferences
        sharedPreferences.edit().putString("language", language).apply()

        // Mettre à jour la langue et recréer l'activité pour appliquer les modifications
        updateLocale(language)
        recreate()  // Recréer l'activité pour appliquer le changement de langue
    }

    @SuppressLint("Range")
    private fun loadContacts() {
        originalContactList.clear()  // Vider la liste d'origine avant d'ajouter les contacts
        contactListData.clear()  // Vider la liste utilisée pour l'affichage des contacts

        val contactsUri = ContactsContract.Contacts.CONTENT_URI
        val projection = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.HAS_PHONE_NUMBER
        )

        val cursor = contentResolver.query(contactsUri, projection, null, null, null)
        cursor?.let {
            while (it.moveToNext()) {
                val contactId = it.getString(it.getColumnIndex(ContactsContract.Contacts._ID))
                val displayName = it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val hasPhoneNumber = it.getInt(it.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0

                if (hasPhoneNumber) {
                    val phoneCursor = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        arrayOf(contactId),
                        null
                    )

                    phoneCursor?.let { phone ->
                        while (phone.moveToNext()) {
                            val phoneNumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            val contact = Contact(displayName, phoneNumber)

                            // Ajouter le contact aux deux listes
                            originalContactList.add(contact)
                            contactListData.add(contact)
                        }
                        phone.close()
                    }
                }
            }
            it.close()
            contactAdapter.notifyDataSetChanged()  // Mettre à jour l'adaptateur pour afficher les contacts
        }
    }

    // Configurer la SearchView pour filtrer les contacts
    private fun setupSearchView() {
        val searchView: EditText = findViewById(R.id.searchView)
        searchView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Rien à faire ici
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Rien à faire ici
            }

            override fun onTextChanged(newText: CharSequence?, start: Int, before: Int, count: Int) {
                if (newText.isNullOrEmpty()) {
                    // Si le champ de recherche est vide, réinitialiser la liste des contacts
                    contactAdapter.updateData(originalContactList)
                } else {
                    // Filtrer les contacts en fonction de la requête de recherche
                    val filteredContacts = originalContactList.filter {
                        it.name.contains(newText.toString(), ignoreCase = true)
                    }
                    contactAdapter.updateData(filteredContacts)
                }
            }
        })
    }

    // Fonction pour vérifier si la permission est accordée, sinon la demander
    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
        } else {
            loadContacts()  // Charger les contacts si la permission est accordée
        }
    }

    // Lancer la demande de permission pour lire les contacts
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            loadContacts()
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    // Gérer la demande de permission pour passer des appels téléphoniques
    fun checkCallPermission(contactPhone: String) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
            != PackageManager.PERMISSION_GRANTED) {
            requestCallPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
        } else {
            makePhoneCall(contactPhone)
        }
    }

    // Lancer la demande de permission pour passer un appel téléphonique
    private val requestCallPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Permission granted for calls", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Call permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    // Fonction pour passer un appel téléphonique
    fun makePhoneCall(phoneNumber: String) {
        val callIntent = Intent(Intent.ACTION_CALL)
        callIntent.data = Uri.parse("tel:$phoneNumber")
        startActivity(callIntent)
    }

    // Fonction pour envoyer un message
    fun sendMessage(phoneNumber: String) {
        val smsIntent = Intent(Intent.ACTION_VIEW)
        smsIntent.data = Uri.parse("sms:$phoneNumber")
        startActivity(smsIntent)
    }
}
