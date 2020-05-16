# DaysCountUp

## Project Overview

DaysCountUp is a notification app allows you to set up notifications in the future to celebrate your special days. This app utilized general Android component and implemented Material UI to provide the easy and meaningful UX. 

For data persistance in local, it uses Room library and LiveData. This app also provides functionality to sync the data via Google Firestore. Users are authenticated by Google Firebase Auth.

## Directly Use

You can find the signed apk at /app/release/app-release.apk

## Keystore

Keystore are stored in the path /keystore/key

## Firebase services

The app utilizes Google Firebase. To get everything works for this app, you should follow along this tutorial https://firebase.google.com/docs/android/setup?authuser=0 to create your own Firebase project and add configuration file into your android project.

Another place you should take care of is you should add your web application type client ID into your project. Here is information you can refer to: https://firebase.google.com/docs/auth/android/google-signin?authuser=0