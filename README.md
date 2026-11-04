# TripMate
A travel advisor project built on android

## 📋 Functional Requirements

The **TripMate** application must include the following core screens, with clear ownership allocated to individual team members:

*   **Home / Dashboard Screen:** 
    *   Displays a dynamic countdown of days remaining until the next scheduled trip.
    *   Shows a summary of total planned activities across all trips.
    *   Tracks budget utilization (total budget spent vs. remaining total budget).
    *   Renders a comprehensive list of all trips paired with their next upcoming activity using a `LazyColumn`.
*   **Add / Edit Activity Screen:** 
    *   A digital entry form to add or modify trip itinerary items.
    *   Features input fields for Activity Title, Trip Association (via a Dropdown menu), Date, Time, Estimated Cost, and Notes.
    *   Enforces strict form validation (prevents submission of empty fields, blocks negative currency inputs, and displays precise inline error messages).
*   **Itinerary Screen:** 
    *   A scrollable `LazyColumn` listing all planned activities sequentially.
    *   Implements filter constraints to sort activities by a specific trip profile or custom date ranges.
    *   Provides quick click interaction on lists items to route to detail screens.
*   **Activity Detail Screen:** 
    *   A complete, read-only view of a selected activity fetched dynamically via navigation arguments.
    *   Integrates direct navigation controls targeting the **Edit** and **Delete** actions.
*   **Trip Manager Screen:** 
    *   A dedicated module to view, add, and configure parent trip entries (Trip Name, Destination, Colour Tag, and Total Budget allocation).
    *   Directly wired to handle underlying local database entity updates.
*   **Budget / Stats Screen (Optional / Bonus):** 
    *   A summary or visual bar chart representation showing spending breakdowns per trip, daily costs, remaining balances, and daily activity frequencies.

---

## 🛠️ Technical Requirements

All developed codebase features must strictly conform to the following architectural design standards:

*   **Language:** Kotlin
*   **UI Framework:** Jetpack Compose with Material 3 components and custom theming configurations.
*   **System Architecture:** MVVM (Model-View-ViewModel) utilizing `ViewModel` linked to reactive asynchronous properties like `StateFlow` or `LiveData`.
*   **Data Persistence:** Room Database utilizing a minimum of 2 entities aligned directly with your physical codebase paths:
    *   `Trip` (Manages parent trip profiles and budget allocations)
    *   `TripActivityEntity` (Manages itemized itinerary tasks linked to a parent trip)
*   **Navigation Routing:** Managed seamlessly via a central `NavController` and `NavHost` setup supporting type-safe argument passing across screen graphs.
*   **State Management:** Proper execution of state hoisting to ensure seamless, real-time UI data updates across multi-layered composables.
*   **User Feedback:** Prompt display of execution status notifications via a `Toast` or standard scaffold `Snackbar` whenever database CRUD (Create, Read, Update, Delete) processes complete.
