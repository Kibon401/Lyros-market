# Farm-to-Client Android App Specification

## 1. Project Architecture (Clean Architecture + MVVM)
Built with **Jetpack Compose** and **Dagger Hilt**, mirroring the backend's clean structure.

### Layer Breakdown
- **Domain:** Entities, Repository Interfaces, UseCases (`CheckoutUseCase`, `UploadAvatarUseCase`, `RefreshTokenUseCase`).
- **Data:** Repository Implementations, Room DB (`OfflineCache`), Ktor Client (`RemoteApi`).
- **Presentation:** ViewModels (StateFlow) and Compose Screens.

---

## 2. Feature-Rich UI Matrix
Following a **Pinterest-inspired** design with rounded cards and earthy tones.

| Feature Group | Screens / Components | Key Logic |
| :--- | :--- | :--- |
| **Authentication** | Login, Signup, Reset Password | JWT & **Refresh Token** auto-management in Ktor Client. |
| **User Profile** | Profile View, Edit Details, **Avatar Upload** | DataStore for session, Image Picker for profile photos. |
| **Catalog** | Staggered Grid, Filter Chips, Search | **Paging 3** for infinite scroll produce lists. |
| **Cart & Checkout**| Cart List, Delivery Address Pinning | Client sends location; Backend returns **Secure Fee**. |
| **Payments** | M-Pesa Status Overlay, Receipt View | Real-time polling for payment verification status. |
| **Fulfillment** | Driver Task List, Status Stepper | One-tap delivery confirmation for drivers. |
| **Reviews** | Star Rating, Comment Field, Admin Replies | Displays "Verified Purchase" badge and Admin feedback. |

---

## 3. Tech Stack & Libraries
- **UI:** Jetpack Compose, Material 3
- **DI:** Dagger Hilt
- **Async:** Coroutines, StateFlow
- **Networking:** Ktor Client (with Auth & Logging plugins)
- **Local Cache:** Room Database
- **Image Loading:** Coil (with Disk Caching)
- **Session:** Encrypted DataStore
- **Error Tracking:** Firebase Crashlytics & Sentry

---

## 4. Enhanced Offline Capabilities
- **Cart Persistance:** Cart items are saved in Room to prevent data loss during network drops.
- **Image Caching:** Product images are cached on disk via Coil.
- **Background Sync:** Orders placed offline are queued for submission once connectivity returns.
