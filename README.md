
# Messenger Android Application

This is my college project. It's look like instagram(user can message, post, call, add status etc.).
Messages are encrypted using AES algorithm which make more secure conversation between end user. All data are hosted on firebase. which provide realtime database to access it form anywhere.


## Demo

Some Sample images of app are -

![PicsArt_03-01-11 25 01](https://user-images.githubusercontent.com/73978467/160287954-8890a863-cb32-4733-a314-26![Screenshot_20220301-111422_Messenger](https://user-images.githubusercontent.com/73978467/160287973-704f1937-47b6-430a-a48a-d98d4c1a851f.jpg)
981271b905.jpg)
![PicsArt_03-01-11 25 36](https://user-images.githubusercontent.com/73978467/160287962-63f110b0-6c7f-445c-89cd-6ac7841f31fa.![Screenshot_20220301-111731_Messenger](https://user-images.githubusercontent.com/73978467/160287974-69783d3d-3ab3-4057-8466-0b22cb7fb92e.jpg)
jpg)
![Screenshot_20220301-111244_Messenger](https://user-images.githubusercontent.com/73978467/160287964-8ec37350-a658-445a-b2a2-0e5ca4441e98.jpg)
![Uploading![Screenshot_20220301-111244_Messenger](https://user-images.githubusercontent.com/73978467/160287967-7de720c6-ff03-44e0-ab97-![Screenshot_20220301-112108_Messenger](https://user-images.githubusercontent.com/73978467/160287977-d2a5b78f-fe5c-4d8e-9eaa-73b14a7b6898.jpg)
![Screenshot_20220301-111434_Messenger](https://user-images.githubusercontent.com/73978467/160287978-b43ce6d8-7565-4b74-8faa-b02ce0041cad.jpg)
![Screenshot_20220301-111731_Messenger](https://user-images.githubusercontent.com/73978467/160287979-f29562fa-fc96-4fab-bea9-ea78d3848fe6.jpg)
![Screenshot_20220301-112108_Messenger](https://user-images.githubusercontent.com/73978467/160287980-da5aa514-9f7e-4c9e-9b88-0af2fa0faf93.jpg)
![PicsArt_03-01-11 25 36](https://user-images.githubusercontent.com/73978467/160287982-2edd02da-0295-4199-a64d-6daf34420b38.jpg)
f1c463d93b74.jpg)
![Screenshot_20220301-111434_Messenger](https://user-images.githubusercontent.com/73978467/160287969-3fc34154-31f7-4d87-bd29-7415dcb2daee.jpg)
![Screenshot_20220301-111404_Messenger](https://user-images.githubusercontent.com/73978467/160287972-71e02251-ce35-4a2e-9445-1d8682c24344.jpg)
 Screenshot_20220301-111422_Messenger.jpg…]()



## Features

Features

- Login/ signup using firebase
- One-on-one messaging and Group chat
- High![Screenshot_20220301-111404_Messenger](https://user-images.githubusercontent.com/73978467/160287965-f45bdda8-08a4-4eaf-a5b5-1157b1bdd035.jpg)
 quality voice and video calling
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
