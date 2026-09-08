# Kijani Market - Daily Changelog

This document tracks significant changes and feature implementations on a daily basis.

---

## 2026-07-08 - Security Hardening & Password Reset

### **New Features Implemented:**
- **Password Reset Flow (SMTP Integration):**
    - Implemented `POST /auth/forgot-password` to generate and email a 6-digit reset code.
    - Implemented `POST /auth/reset-password` to verify the code and update the user's password.
    - Integrated `javax.mail` for sending emails (requires configuration in `EmailService.kt`).
    - Updated `UsersTable` with `resetCode` and `resetExpiry` fields.

### **Security Enhancements & Bug Fixes:**
- **Comprehensive Role-Based Access Control (RBAC):**
    - `POST /products`: Now strictly **Admin only**.
    - `POST /categories`: Now strictly **Admin only**.
    - `POST /orders/test-mark-paid/{id}`: Now strictly **Admin only**.
    - All `DriverRoutes` (`/driver/*`): Now strictly **Driver only**.
    - `POST /upload/product-image`: Now strictly **Admin only**.
    - All other authenticated routes (`/users/*`, `/cart/*`, `/orders/*`, `/payments/stk-push`, `/reviews/add`) require **any authenticated user**.
- **Negative Stock Prevention:**
    - Implemented pre-order stock validation in `OrderRepositoryImpl.createOrder` to prevent `stockQuantity` from dropping below zero. Orders will now fail if any item is out of stock.
- **M-Pesa Callback URL Fix:**
    - Corrected a typo (space) in `DarajaConfig.CALLBACK_URL` to resolve "Invalid CallBackURL" errors from Safaricom.

### **Documentation & Project Management:**
- **`README.md`:** Transformed into a comprehensive "Technical System Specification" for builders, detailing architecture, ORM, security, and business logic.
- **`TODO.md`:** Updated to reflect completion of password reset endpoints and security hardening.
- **`API_TESTING_GUIDE.md`:** Updated to include new password reset endpoints.
- **Frontend Compatibility Analysis:** Performed a detailed analysis of all `code.html` files in `src/KM/stitch_kijani_market_android_app/` subfolders, confirming high compatibility with the Ktor backend and identifying minor frontend implementation tasks.

---

## 2026-07-09 - Admin Control & Driver Tracking

### **New Features Implemented:**
- **Comprehensive Admin Control:**
    - **User Management:**
        - `GET /admin/users`: List all users (Admin only).
        - `PUT /admin/users/{userId}`: Update user details (username, email, role) (Admin only).
        - `DELETE /admin/users/{userId}`: Soft-delete user (Admin only).
    - **Product Management:**
        - `PUT /admin/products/{productId}`: Update product details (Admin only).
        - `DELETE /admin/products/{productId}`: Delete product (Admin only).
    - **Category Management:**
        - `PUT /admin/categories/{categoryId}`: Update category details (Admin only).
        - `DELETE /admin/categories/{categoryId}`: Delete category (Admin only).
    - **Order Management:**
        - `GET /admin/orders`: List all orders in the system (Admin only).
- **Driver Real-time Tracking:**
    - `DriverLocationTable`: New database table to store driver's latitude, longitude, and timestamp.
    - `POST /driver/location`: Driver sends location updates (Driver only).
    - `GET /orders/{orderId}/track`: Customer/Admin retrieves last known driver location for an order.

### **Documentation & Project Management:**
- **`API_TESTING_GUIDE.md`:** Updated to include all new Admin management and Driver tracking endpoints.
- **`TODO.md`:** Updated to reflect completion of Admin control and Driver tracking features.

---

### **Instructions for Future Updates:**
- At the end of each development session, add a new entry with the current date.
- Briefly summarize the features implemented, bugs fixed, or significant architectural changes.
- Keep it concise but informative.

---
