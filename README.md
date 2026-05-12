# Lost and Found App

## Overview
Lost and Found App is an Android mobile app built in Java using Android Studio.  
The app allows users to create, view, search, and remove lost/found item adverts using a local SQLite database.
The main goal of the app is to help connect lost items with their owners by storing multiple details about each item.

## Features
- Create Lost or Found Advert
  - Users can choose whether the advert is for a lost or found item.
  - Users enter item name, phone number, description, location, and category.

- Image Upload
  - Users can select an image from the device using Android’s document picker.
  - The app stores the selected image URI as a string in SQLite.
  - Persistent read permission is used so the image can be displayed again later.
  - Glide is used to load images into the app.

- Automatic Timestamp
  - Each advert is given an automatic date/time stamp when saved.
  - The timestamp is stored with the advert record in the SQLite database.

- View All Lost and Found Items
  - Saved adverts are displayed in a RecyclerView.
  - Each list item shows the advert title, category, location, timestamp, and image.

- Real-Time Search and Filtering
  - The search bar updates dynamically as the user types. It checks multiple advert fields.
  - Users can filter by category or show all adverts.

- Advert Detail Screen
  - Users can tap an advert to look at its full details.
  - The detail screen displays the item image, title, category, location, description, phone number and timestamp.
  - The user can also choose to remove an advert on this screen. A confirmation dialog appears before deletion.

## Built With
- Java
- Android Studio
- XML layouts
- SQLite database
- Glide
- Android document picker

## Database
The app uses a local SQLite database through a DatabaseHelper class that extends SQLiteOpenHelper.  
The app supports inserting adverts, searching/filtering adverts, retrieving an advert by ID, and deleting adverts.

## How to Run
1. Open the project in Android Studio.
2. Sync the Gradle files.
3. Run the app using an emulator or Android device.
4. Use the home screen to create a new advert or view all saved lost/found items.

## Notes
- Images are not stored directly in the database.
- The app stores the image URI as text and uses Glide to display the image.
- The database is stored locally on the device and persists while the app is installed.

## Author
Dean Kennedy  
s224318581
