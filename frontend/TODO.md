# 🌱 Lyros Market Project Roadmap & TODO List

This list tracks the development of the Farm-to-Client ecosystem.

## 🟦 Phase 1: Ktor Backend - Foundation & Security
- [ ] **Project Setup**
    - [ ] Initialize Ktor Project with Kotlin DSL.
    - [ ] Configure `application.conf` with Environment Variables.
    - [ ] Set up HikariCP & Exposed ORM connection.
- [ ] **Authentication Module (Core)**
    - [ ] Implement `SiteUsers` table and Role ENUM.
    - [ ] Setup BCrypt password hashing.
    - [ ] Implement JWT Access Token logic.
    - [ ] **Production Hardening:** Implement **Refresh Token Rotation** logic.
    - [ ] Create `/auth/register` and `/auth/login` endpoints.
- [ ] **User Management**
    - [ ] Implement `/users/profile` (GET/PUT).
    - [ ] Implement Profile Picture upload logic.

## 🟩 Phase 2: Ktor Backend - Inventory & Carts
- [ ] **Product Catalog**
    - [ ] Implement `Categories` and `Products` tables.
    - [ ] Implement **Agricultural Attributes** (Key-Value) tables for seedlings.
    - [ ] Create GET `/products` with filtering and search.
- [ ] **Shopping Cart Engine**
    - [ ] Implement `shopping_cart` and `shopping_cart_item` tables.
    - [ ] Build logic for adding/updating/clearing cart items.

## 🟧 Phase 3: Ktor Backend - Orders & Payments
- [ ] **Order Management**
    - [ ] Implement `shop_order` and `order_line` tables.
    - [ ] **Production Hardening:** Implement **Server-Side Delivery Fee Calculation** logic.
    - [ ] **Production Hardening:** Implement **Idempotency Key** checks for order placement.
- [ ] **M-Pesa Integration**
    - [ ] Integrate Ktor Client with Daraja API (STK Push).
    - [ ] Create **Secured Callback Endpoint** for M-Pesa webhooks.
    - [ ] Implement payment verification polling logic.

## 🟪 Phase 4: Android App - Core Architecture
- [ ] **Project Foundation**
    - [ ] Set up Dagger Hilt for Dependency Injection.
    - [ ] Configure Ktor Client with Auth & Logging observers.
    - [ ] Setup **Encrypted DataStore** for JWT storage.
- [ ] **Data Layer (Offline First)**
    - [ ] Implement Room Database for `CachedProducts`.
    - [ ] Create Repository implementations for Auth and Products.

## 🟥 Phase 5: Android App - UI & Features
### Screen TODO List
#### 🔐 Authentication & Onboarding
- [ ] Splash Screen
- [ ] Onboarding (Walkthrough)
- [x] Login Screen
- [x] Registration Screen
- [ ] Forgot Password Screen
- [ ] Reset Password Screen

#### 🏠 Discovery (Main Flow)
- [x] Home Screen (Categories, High Demand, Featured)
- [x] Search Screen (with filters)
- [ ] Category Products Listing
- [x] Product Details Screen
- [ ] Product Reviews Screen

#### 🛒 Shopping & Checkout
- [ ] Shopping Cart Screen
- [ ] Checkout Screen (Address selection + M-Pesa entry)
- [ ] Payment Status Overlay (Success/Failure/Pending)

#### 📦 Orders & Profile
- [ ] User Profile Screen (View/Edit)
- [ ] Order History Screen
- [ ] Order Tracking Screen (Real-time map tracking)
- [ ] Write Review Screen

#### 🚚 Driver Portal (Role-based)
- [ ] Driver Dashboard (Available tasks)
- [ ] Active Delivery Screen (Update status + GPS update)
- [ ] Driver Delivery History

#### 📊 Admin Portal (Role-based)
- [ ] Admin Dashboard (Statistics)
- [ ] Inventory Management Screen
- [ ] User Role Management Screen
- [ ] Order Fulfillment/Assignment Screen

### Feature Implementation
- [ ] **Data Layer (Offline First)**
    - [ ] Implement Room Database for `CachedProducts`.
    - [ ] Create Repository implementations for Auth and Products.
- [ ] **Discovery Flow**
    - [ ] Build **Pinterest-style** staggered grid for products.
    - [ ] Implement Paging 3 for smooth scrolling.
    - [ ] Build Product Details screen with **Verified Reviews**.
- [ ] **Checkout Flow Flow**
    - [ ] Implement Cart UI.
    - [ ] Integrate Location Services for delivery address pinning.
    - [ ] Build M-Pesa Payment status overlay.

## 🟨 Phase 6: Logistics & Admin Tools
- [ ] **Driver Module**
    - [ ] Build Driver Dashboard (Task Roster).
    - [ ] Implement Status Update logic (In Transit -> Delivered).
- [ ] **Admin Dashboard**
    - [ ] Build Order Assignment interface.
    - [ ] Implement Review Reply logic.

## 🏁 Phase 7: Production Hardening & Launch
- [ ] **Monitoring**
    - [ ] Integrate **Sentry** for Backend & Android error tracking.
    - [ ] Configure **Firebase Crashlytics** for Android.
- [ ] **Security Audit**
    - [ ] Validate Rate Limiting on Auth endpoints.
    - [ ] Perform final check on Token Rotation.
- [ ] **Deployment**
    - [ ] Deploy Ktor Server to production environment.
    - [ ] Generate Signed APK/Bundle for Android.
