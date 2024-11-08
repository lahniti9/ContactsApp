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
    private val activity: MainActivity  // Passe l'activité pour gérer les actions
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    // Crée le ViewHolder pour chaque élément de la liste
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.contact_item, parent, false)
        return ContactViewHolder(itemView)
    }

    // Lie les données du contact à l'élément de la liste
    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contacts[position]

        // Définit le nom et le téléphone du contact
        holder.nameTextView.text = contact.name
        holder.phoneTextView.text = contact.phoneNumber

        // Définit la première lettre du nom du contact dans le cercle du profil
        val firstLetter = contact.name.first().toString().uppercase()
        holder.profileCircleTextView.text = firstLetter

        // Génère une couleur de fond unique pour le cercle du profil en fonction du nom du contact
        val backgroundColor = generateColorFromName(contact.name)

        // Crée un Drawable circulaire et applique la couleur de fond
        val drawable = GradientDrawable()
        drawable.shape = GradientDrawable.OVAL
        drawable.setColor(backgroundColor)
        holder.profileCircleTextView.background = drawable

        // Configure le bouton d'action pour afficher les options d'appel ou de message
        holder.actionButton.setOnClickListener {
            showActionDialog(contact.phoneNumber)
        }
    }

    // Retourne le nombre total de contacts
    override fun getItemCount(): Int {
        return contacts.size
    }

    // Classe ViewHolder pour maintenir les vues de chaque élément de la liste
    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.contact_name)
        val phoneTextView: TextView = itemView.findViewById(R.id.contact_phone)
        val profileCircleTextView: TextView = itemView.findViewById(R.id.profile_circle)
        val actionButton: ImageButton = itemView.findViewById(R.id.action_button)  // Utilise ImageButton
    }

    // Affiche une boîte de dialogue pour choisir entre appeler ou envoyer un message
    private fun showActionDialog(phoneNumber: String) {
        // Récupère les chaînes traduites
        val chooseActionTitle = activity.getString(R.string.choose_action)
        val callOption = activity.getString(R.string.call)
        val messageOption = activity.getString(R.string.message)

        val options = arrayOf(callOption, messageOption)

        val builder = AlertDialog.Builder(activity)
        builder.setTitle(chooseActionTitle)
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> activity.checkCallPermission(phoneNumber)  // Appeler
                1 -> activity.sendMessage(phoneNumber)           // Envoyer un message
            }
        }
        builder.show()
    }

    // Fonction pour générer une couleur unique basée sur le nom du contact
    private fun generateColorFromName(name: String): Int {
        val hash = name.hashCode()
        val r = (hash and 0xFF0000) shr 16
        val g = (hash and 0x00FF00) shr 8
        val b = (hash and 0x0000FF)
        return Color.rgb(r, g, b)
    }

    // Méthode pour mettre à jour les données de l'adaptateur (si nécessaire)
    fun updateData(newContacts: List<Contact>) {
        (contacts as MutableList).clear()
        contacts.addAll(newContacts)
        notifyDataSetChanged()
    }
}
