# 🌾 Lyros Market - System Documentation

Lyros Market is a modern, farm-to-client ecosystem designed for ordering fresh farm produce directly from farmers in Eldoret. The system consists of an Android application built with Jetpack Compose and a backend built with Ktor.

---

## 1. Project Architecture

Both the **Android Frontend** and **Ktor Backend** follow a mirrored **Clean Architecture** pattern, structured into three main layers:

- **Presentation:** ViewModels (`StateFlow`) and Jetpack Compose Screens.
- **Domain:** Entities, Repository Interfaces, UseCases (e.g., `CheckoutUseCase`, `UploadAvatarUseCase`).
- **Data:** Repository Implementations, Room DB (`OfflineCache`), Ktor Client (`RemoteApi`).

This ensures business logic is isolated, making the system highly testable, maintainable, and scalable.

---

## 2. Tech Stack & Libraries

### Android (Frontend)
- **UI:** Jetpack Compose, Material 3
- **Dependency Injection:** Dagger Hilt
- **Concurrency & State:** Coroutines, StateFlow
- **Networking:** Ktor Client (with Auth & Logging plugins)
- **Local Cache:** Room Database
- **Image Loading:** Coil (with Disk Caching)
- **Session Management:** Encrypted DataStore
- **Error Tracking:** Firebase Crashlytics & Sentry

### Ktor (Backend)
- **Server:** Ktor Framework
- **ORM:** Exposed (MySQL)
- **Security:** JWT (Access + Refresh Tokens)
- **Database Pooling:** HikariCP
- **Payment Gateway:** Safaricom Daraja API (M-Pesa)

---

## 3. Core Features & User Matrix

The platform is designed with a **Pinterest-inspired** aesthetic featuring rounded cards, earthy tones, and staggered grids. It caters to three primary roles:

| Role | Core Capabilities |
| :--- | :--- |
| **Admin** | Full system control, inventory management, driver assignment, review replies, financial auditing. |
| **Client** | Browsing, secure purchasing (M-Pesa), order tracking, profile management, verified reviews. |
| **Driver** | Task roster management, map navigation, real-time fulfillment status updates. |

### Feature Highlights
- **Authentication:** Multi-role login with JWT & auto-managed Refresh Tokens. Account security includes bcrypt hashing and rate limiting.
- **Catalog & Storefront:** Paging 3 powers infinite scrolling through produce lists. Supports offline-first browsing via Room caching.
- **Cart & Checkout:** Client sends location; Backend calculates a secure delivery fee based on an Eldoret distance matrix. Cart items are persisted locally to prevent data loss.
- **Payments:** Seamless M-Pesa STK Push integration with real-time status polling and secure webhook callbacks (IP/Secret validation).
- **Reviews:** Verified purchase badges with 1-5 star ratings. Admins can engage with feedback directly.
- **Fulfillment:** One-tap delivery confirmation for drivers, updating status from Pending → In Transit → Delivered.

---

## 4. Production Hardening & Reliability

Based on professional e-commerce standards, the system includes:
- **Secure Sessions:** Token Rotation prevents session hijacking.
- **Data Integrity:** Exposed ORM Transactions and Atomic Stock Updates wrap all inventory changes.
- **Idempotency:** Unique request keys prevent duplicate orders or double M-Pesa charges during network retries.
- **Financial Security:** Server-Side Delivery Fee Calculation and Secured M-Pesa Callbacks.
- **Enhanced Offline Capabilities:** Cart persistence, Coil image caching, and background sync for queued orders.

---

## 5. UI/UX Design

- **Primary Green:** `#1E5631` (Deep Forest)
- **Surface:** `#FFFFFF` (Crisp White)
- **Background:** `#FAFAF7` (Off-White)
- **UI Elements:** 16.dp rounded cards, soft shadows, staggered grids.

---

## 6. Scalability & Future Growth

- **Caching:** Ready for Redis integration for high-traffic session management.
- **Real-Time:** Structured for Ktor WebSocket implementation for live driver tracking.
- **Intelligence:** Prepared for AI-driven produce recommendations based on purchase history.
