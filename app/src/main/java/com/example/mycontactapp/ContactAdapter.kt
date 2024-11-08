package com.example.mycontactapp

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class ContactAdapter(
    private val contacts: List<Contact>,
    private val activity: MainActivity  // Pass the activity to handle actions
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.contact_item, parent, false)
        return ContactViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contacts[position]

        // Set contact name and phone
        holder.nameTextView.text = contact.name
        holder.phoneTextView.text = contact.phoneNumber

        // Set the first letter of the contact's name in the profile circle
        val firstLetter = contact.name.first().toString().uppercase()
        holder.profileCircleTextView.text = firstLetter

        // Generate a unique background color for the profile circle based on the contact's name
        val backgroundColor = generateColorFromName(contact.name)

        // إنشاء Drawable دائرية وتطبيق اللون والخلفية الدائرية
        val drawable = GradientDrawable()
        drawable.shape = GradientDrawable.OVAL
        drawable.setColor(backgroundColor)
        holder.profileCircleTextView.background = drawable

        // Set up action button to show options for Call or Message
        holder.actionButton.setOnClickListener {
            showActionDialog(contact.phoneNumber)
        }
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    // ViewHolder class to hold the contact item views
    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.contact_name)
        val phoneTextView: TextView = itemView.findViewById(R.id.contact_phone)
        val profileCircleTextView: TextView = itemView.findViewById(R.id.profile_circle)
        val actionButton: ImageButton = itemView.findViewById(R.id.action_button)  // Use ImageButton
    }

    // Show dialog to choose between Call or Message
    private fun showActionDialog(phoneNumber: String) {
        // Fetch translated strings
        val chooseActionTitle = activity.getString(R.string.choose_action)
        val callOption = activity.getString(R.string.call)
        val messageOption = activity.getString(R.string.message)

        val options = arrayOf(callOption, messageOption)

        val builder = AlertDialog.Builder(activity)
        builder.setTitle(chooseActionTitle)
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> activity.checkCallPermission(phoneNumber)  // Call
                1 -> activity.sendMessage(phoneNumber)           // Message
            }
        }
        builder.show()
    }

    // Function to generate a unique color based on the contact's name
    private fun generateColorFromName(name: String): Int {
        val hash = name.hashCode()
        val r = (hash and 0xFF0000) shr 16
        val g = (hash and 0x00FF00) shr 8
        val b = (hash and 0x0000FF)
        return Color.rgb(r, g, b)
    }

    // Method to update data in the adapter (if needed)
    fun updateData(newContacts: List<Contact>) {
        (contacts as MutableList).clear()
        contacts.addAll(newContacts)
        notifyDataSetChanged()
    }
}