# MyContactApp
![WhatsApp Image 2024-11-08 at 19 19 00](https://github.com/user-attachments/assets/c6b743d3-ccd4-4759-b305-07c61225c899)



![WhatsApp Image 2024-11-08 at 19 ![WhatsApp Image 2024-11-08 at 19 18 58 (1)](https://github.com/user-attachments/assets/4428aee0-cfa2-4808-b29a-89f1a8c14d07)
19 00 (1)](https://github.com/user-attachments/assets/0b8f5417-41cd-4901-aaac-3474f96db4a1)

**MyContactApp** est une application Android permettant de gérer et d'afficher des contacts. Elle offre une interface simple pour afficher la liste des contacts, avec des options pour appeler ou envoyer des messages. Chaque contact est affiché avec une première lettre de son nom dans un cercle coloré unique, généré dynamiquement en fonction du nom du contact.

## Fonctionnalités

- Affichage de la liste des contacts.
- Affichage du nom et du numéro de téléphone.
- Affichage d'une lettre représentant chaque contact dans un cercle coloré.
- Possibilité d'appeler ou d'envoyer un message à un contact via un menu contextuel.
- Recherche de contacts via une barre de recherche.
- Changement dynamique de la couleur de fond du cercle de profil en fonction du nom du contact.

## Prérequis

- Android Studio (dernière version recommandée)
- Un appareil Android ou un émulateur pour exécuter l'application.

## Installation

1. Clonez le repository :
   ```bash
   git clone https://github.com/lahniti9/MyContactApp.git
2. Ouvrez le projet dans Android Studio.
3. Assurez-vous d'avoir les dernières dépendances installées en ouvrant le terminal dans Android Studio et en exécutant :
bash
Copier le code
./gradlew build
4. Lancez l'application sur un appareil physique ou un émulateur.

   
   Utilisation
L'application affiche une liste de contacts, chacun ayant une icône circulaire colorée avec la première lettre de son nom.
Vous pouvez rechercher un contact en utilisant la barre de recherche située en haut de l'écran.
Cliquez sur un bouton d'action pour choisir entre appeler ou envoyer un message au contact sélectionné.
Structure du Code
ContactAdapter
Le ContactAdapter est responsable de l'affichage des contacts dans une RecyclerView. Il prend en charge l'affichage du nom et du numéro de téléphone du contact, ainsi que l'affichage de la première lettre du nom dans un cercle coloré.

MainActivity
La MainActivity contient la logique principale de l'application, y compris l'initialisation de la liste des contacts, la gestion de la recherche et le traitement des actions pour appeler ou envoyer un message.

Ressources XML
contact_item.xml : Layout pour chaque élément de contact affiché dans la liste.
activity_main.xml : Layout principal de l'activité avec la RecyclerView pour afficher les contacts et la barre de recherche.
Permissions
L'application nécessite les permissions suivantes pour pouvoir passer des appels et envoyer des messages :

CALL_PHONE
SEND_SMS
Ces permissions peuvent être ajoutées dans le fichier AndroidManifest.xml.

Auteurs
Ismail Lahniti : Développeur principal
