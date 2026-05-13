# Lost and Found Map App

## Overview
Lost and Found Map App is an Android mobile app built in Java using Android Studio.  
The app allows users to create, view, search, remove, and map lost/found item adverts using a local SQLite database.
This project extends the previous Lost and Found App by adding Google Maps, location autocomplete, current location support, and radius-based map searching.

The main goal of the app is to help connect lost items with their owners by storing multiple details about each item, including its location and map coordinates.

## Features
- Create Lost or Found Advert
  - Users can choose whether the advert is for a lost or found item.
  - Users enter item name, phone number, description, location, and category.
  - The location field uses Google Places autocomplete to help users select a real location.

- Get Current Location
  - Users can select their current device/emulator location using the Get Current Location button.
  - The app requests location permission when needed.
  - The selected coordinates are converted into a readable address for display.

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
  - The detail screen displays the item image, title, category, location, description, phone number, and timestamp.
  - The user can also choose to remove an advert on this screen. A confirmation dialog appears before deletion.

- Map View
  - Users can open a Google Map from the home screen.
  - Saved adverts are displayed as markers using their stored latitude and longitude values.
  - Each marker shows the advert title and location/category information.

- Radius-Based Search
  - Users can enter a radius in kilometres.
  - The app compares the user’s current location with each saved advert location.
  - Only adverts within the selected radius are shown on the map.

## Built With
- Java
- Android Studio
- XML layouts
- SQLite database
- Glide
- Android document picker
- Google Maps SDK for Android
- Google Places API

## Database
The app uses a local SQLite database through a DatabaseHelper class that extends SQLiteOpenHelper.  
The app supports inserting adverts, searching/filtering adverts, retrieving an advert by ID, and deleting adverts.

## API Key Setup
This app uses Google Maps Platform. The API key is not included.
To run the app, add your Google Maps API key to the project’s `local.properties` file.
include it as: MAPS_API_KEY=your_google_maps_api_key_here
Required APIs: Maps SDK for Android and Places API.

## How to Run
1. Open the project in Android Studio.
2. Sync the Gradle files.
3. Run the app using an emulator or Android device.
4. Use the home screen to create a new advert or view all saved lost/found items.

## Notes
- Images are not stored directly in the database.
- The app stores the image URI as text and uses Glide to display the image.
- The database is stored locally on the device and persists while the app is installed.
- Map markers use the latitude and longitude saved with each advert.
- The radius search is based on the user’s current device/emulator location.

## Author
Dean Kennedy  
s224318581
