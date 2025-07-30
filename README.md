# Karoo CGM

Karoo CGM is a native Android application for monitoring glucose levels from LibreLinkUp, designed for cyclists using the Hammerhead Karoo 3 cycling computer. This app allows users to view their glucose data in real-time on their Karoo device, helping them manage their nutrition and performance during rides.

## Features

*   **LibreLinkUp Integration:** Securely authenticates with your LibreLinkUp account.
*   **Patient Selection:** If you follow multiple people, you can select which patient's data to display.
*   **Real-time Glucose Monitoring:** Displays the latest glucose reading and trend arrow.
*   **Historical Data:** Shows a graph of recent glucose history to visualize trends.
*   **Customizable Thresholds:** High and low glucose thresholds are displayed on the graph.
*   **Data Persistence:** Securely stores your credentials and preferences, so you don't have to log in every time.
*   **Token Management:** Automatically handles authentication token renewal.

## Architecture

The app is built using modern Android development practices and libraries:

*   **UI:** Jetpack Compose for a declarative and modern UI.
*   **Architecture:** MVVM (Model-View-ViewModel) with a reactive approach using Kotlin Flows.
*   **Dependency Injection:** Hilt for managing dependencies and promoting a modular, testable architecture.
*   **Navigation:** Jetpack Navigation Compose for navigating between screens.
*   **Data Persistence:** Jetpack DataStore for storing user preferences and authentication tokens asynchronously and securely.
*   **Networking:** Ktor for making HTTP requests to the LibreLinkUp API.
*   **Charting:** Vico for displaying the glucose graph.

## Getting Started

To build and run the app, you'll need to have Android Studio installed.

## Screenshots

*(Placeholder for screenshots of the app in action)*
