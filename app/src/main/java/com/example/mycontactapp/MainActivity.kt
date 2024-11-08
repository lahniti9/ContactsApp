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
    private val contactListData = mutableListOf<Contact>()  // List to store contacts
    private val originalContactList = mutableListOf<Contact>()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)

        // Get the selected language from SharedPreferences
        val language = sharedPreferences.getString("language", "en") ?: "en"
        updateLocale(language)

        setContentView(R.layout.activity_main)

        // Initialize RecyclerView
        contactList = findViewById(R.id.contact_list)
        contactAdapter = ContactAdapter(contactListData, this)
        contactList.layoutManager = LinearLayoutManager(this)
        contactList.adapter = contactAdapter

        // Set up SearchView for filtering contacts
        setupSearchView()

        // Check permissions and load contacts if permission is granted
        checkPermissions()

        // Language button setup
        val languageButton: ImageView = findViewById(R.id.languageButton)
        languageButton.setOnClickListener {
            showLanguageMenu(it)
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

        // Update the layout direction based on language (for RTL support)
        if (locale.language == "ar") {
            config.setLayoutDirection(Locale("ar"))
        } else {
            config.setLayoutDirection(Locale("en"))
        }

        resources.updateConfiguration(config, resources.displayMetrics)
    }

    private fun showLanguageMenu(view: View) {
        // Create a PopupMenu
        val popupMenu = PopupMenu(this, view)
        menuInflater.inflate(R.menu.menu_languages, popupMenu.menu)

        // Set menu item click listener
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.english -> onLanguageSelected("en")
                R.id.arabic -> onLanguageSelected("ar")
                R.id.french -> onLanguageSelected("fr")
            }
            true
        }

        // Show the PopupMenu
        popupMenu.show()
    }

    private fun onLanguageSelected(language: String) {
        // Save the selected language in SharedPreferences
        sharedPreferences.edit().putString("language", language).apply()

        // Update the locale and reload the activity to apply the language change
        updateLocale(language)
        recreate()  // Recreate the activity to apply language changes
    }

    @SuppressLint("Range")
    private fun loadContacts() {
        originalContactList.clear()  // Clear the original list before adding contacts
        contactListData.clear()  // Clear the list used for displaying contacts

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

                            // Add the contact to both lists
                            originalContactList.add(contact)
                            contactListData.add(contact)
                        }
                        phone.close()
                    }
                }
            }
            it.close()
            contactAdapter.notifyDataSetChanged()
        }
    }

    // Setup the SearchView for filtering contacts
    private fun setupSearchView() {
        val searchView: EditText = findViewById(R.id.searchView)
        searchView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // No need to do anything here
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No need to do anything here
            }

            override fun onTextChanged(newText: CharSequence?, start: Int, before: Int, count: Int) {
                if (newText.isNullOrEmpty()) {
                    // If search field is empty, reset the contact list
                    contactAdapter.updateData(originalContactList)
                } else {
                    // Filter the contacts list based on the search query
                    val filteredContacts = originalContactList.filter {
                        it.name.contains(newText.toString(), ignoreCase = true)
                    }
                    contactAdapter.updateData(filteredContacts)
                }
            }
        })
    }

    // Function to check if permission is granted, otherwise request it
    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
        } else {
            loadContacts()
        }
    }

    // Launch permission request for reading contacts
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            loadContacts()
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    // Handle permission request for making phone calls
    fun checkCallPermission(contactPhone: String) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
            != PackageManager.PERMISSION_GRANTED) {
            requestCallPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
        } else {
            makePhoneCall(contactPhone)
        }
    }

    // Launch call permission request
    private val requestCallPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Permission granted for calls", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Call permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to make a phone call
    fun makePhoneCall(phoneNumber: String) {
        val callIntent = Intent(Intent.ACTION_CALL)
        callIntent.data = Uri.parse("tel:$phoneNumber")
        startActivity(callIntent)
    }

    // Function to send a message
    fun sendMessage(phoneNumber: String) {
        val smsIntent = Intent(Intent.ACTION_VIEW)
        smsIntent.data = Uri.parse("sms:$phoneNumber")
        startActivity(smsIntent)
    }
}
