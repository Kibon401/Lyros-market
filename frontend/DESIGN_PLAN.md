# 🌱 Farm Fresh: Design Plan & Specifications

A modern Farm-to-Client ecosystem for ordering fresh farm produce directly from farmers in Eldoret.

---

# 🛠 Tech Stack

### Android (Frontend)
- **UI:** Jetpack Compose, Material 3
- **Architecture:** Clean Architecture + MVVM
- **DI:** Dagger Hilt
- **Networking:** Ktor Client
- **Database:** Room (Offline Cache)
- **State:** StateFlow / Coroutines

### Ktor (Backend)
- **Server:** Ktor Framework (Clean Architecture)
- **ORM:** Exposed (MySQL)
- **Security:** JWT (Access + Refresh Tokens)
- **Pooling:** HikariCP
- **Payment:** Safaricom Daraja (M-Pesa)

---

# 📋 Main Features & Capabilities

### 1. Authentication & Security
- **Multi-Role Login:** Admin, Client, Driver.
- **JWT + Refresh Token:** Long-lived secure sessions.
- **Account Security:** Forgot Password, BCrypt Hashing, **Rate Limiting**.
- **User Management:** Profile updates, **Avatar Uploads**, and Address management.

### 2. Storefront (Pinterest Style)
- **Catalog:** Categories, Search, and Filters (including Seedling attributes).
- **Offline First:** Local caching of products and images.
- **Reviews:** **Verified Purchase** badges and 1-5 star ratings.
- **Admin Engagement:** Ability for Admins to reply to reviews.

### 3. Shopping & Checkout
- **Cart Management:** Add/Remove/Clear, Quantity updates.
- **Secure Logistics:** App-side location pinning → **Server-Side Distance/Fee Calculation**.
- **M-Pesa Integration:** STK Push, Webhook Callbacks, and Real-time Verification.

### 4. Logistics & Fulfillment
- **Admin Dispatch:** Assigning paid orders to seeded drivers.
- **Driver Tools:** Task list, Status updates (Pending → Out for Delivery → Delivered).
- **History:** Order tracking for clients and delivery history for drivers.

---

# 📐 Clean Architecture Layers

**Presentation** (Routes/Screens/ViewModels)
   ↕
**Domain** (UseCases/Models/Repo Interfaces)
   ↕
**Data** (Repo Impl/Database/Remote Api)

---

# 🎨 App Theme (Pinterest Inspired)
- **Primary Green:** `#1E5631` (Deep Forest)
- **Surface:** `#FFFFFF` (Crisp White)
- **Background:** `#FAFAF7` (Off-White)
- **UI Elements:** 16.dp Rounded Cards, Soft Shadows, Staggered Grids.

---

# 🚀 Deployment & Production Readiness
- **Monitoring:** Sentry & Firebase for real-time crash reporting.
- **Concurrency:** Exposed transactions for thread-safe inventory management.
- **Efficiency:** HikariCP connection pooling for backend performance.
