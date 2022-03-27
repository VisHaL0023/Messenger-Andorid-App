
# Messenger Android Application

This is my college project. It's look like instagram(user can message, post, call, add status etc.).
Messages are encrypted using AES algorithm which make more secure conversation between end user. All data are hosted on firebase. which provide realtime database to access it form anywhere.


## Demo

Some Sample images of app are -

![PicsArt_03-01-11 25 01](https://user-images.githubusercontent.com/73978467/160288089-5c4f2642-da3f-4929-8eb7-822d4d31ca88.jpg)
![PicsArt_03-01-11 25 36](https://user-images.githubusercontent.com/73978467/160288094-e879fe83-17ef-4307-9009-a6f10998573b.jpg)
![Screenshot_20220301-111244_Messenger](https://user-images.githubusercontent.com/73978467/160288096-e151a419-a825-4d1a-8466-e2b353e41021.jpg = 200*400)
![Screenshot_20220301-111404_Messenger](https://user-images.githubusercontent.com/73978467/160288098-2b575683-c00c-4b04-89b4-4389e032b873.jpg)
![Screenshot_20220301-111422_Messenger](https://user-images.githubusercontent.com/73978467/160288099-8d51d00d-a755-4917-a8be-adc73f70454b.jpg)
![Screenshot_20220301-111434_Messenger](https://user-images.githubusercontent.com/73978467/160288100-5fa78971-da0b-4043-83c4-d4e469d1c6dd.jpg)
![Screenshot_20220301-111731_Messenger](https://user-images.githubusercontent.com/73978467/160288102-fa3e81b1-5cf5-4ee5-a47b-35663eb65748.jpg)
![Screenshot_20220301-112108_Messenger](https://user-images.githubusercontent.com/73978467/160288104-e80521da-c3c7-4f3d-bfc0-fa9cae47cef2.jpg)



## Features

Features

- Login/ signup using firebase
- One-on-one messaging and Group chat
- High quality voice and video calling
- Video and Voice Conferencing
- Rich messaging (text, picture)
- Encrypted Messages
- Status like whatsapp
- Message status and typing indicators
- Online status (presence) and real-time profile update
- Push notifications
- Post photos like intagram 
- like, comment and comment on a post 
- Privacy Setting
- Follow/Unfollow
- Reactions on Messages
- Block/Unblock any user
- Fingerprint lock

## Build and Run

Before we dive into building and running a fully featured Messenger for Android, ensure that you've read the following.

    1. Latest Android Studio Installed
    2. An Android Device

Building the code is as simple as:

    1. Launch Android Studio
    2. Open the project from the folder where you have downloaded the code using menu File -> Open
    3. Build using menu Build -> Rebuild Project
    4. It may take a while to build the project for the first time.
    5. Once the build is over, run on the device using menu Run -> Run (app)
    6. That's it, you should see the welcome screen like below.

Login using your phone number and OTP from the mesibo console. You can even start using the app you've just built to communicate with your family and friends.

Firebase Panel
- Create Firebase Project (https://console.firebase.google.com/);
- Import the file google-service.json into your project
- Connect to firebase console authentication and database from your IDE
- in firebase Storage Rules, change value of "allow read, write:" from "if request.auth != null" to "if true;"
- For sending notification, paste your Firebase project key into your project APIService.java
- When you change database settings, you likely will need to uninstall and reinstall apps to avoid app crashes due to app caches.

## Contact 
For any query you can mail on vishal65.p@gmail.com
