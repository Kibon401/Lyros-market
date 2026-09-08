Kijani Market API - Implementation Matrix (To-Do List)

Based on the **API Module Matrix** in `README.md`, here is the current status of every endpoint.

## 🔑 1. Auth Module (`/auth`)
- [x] `POST /register` - Implementation complete with BCrypt.
- [x] `POST /login` - Implementation complete with JWT role claims.
- [ ] `POST /refresh-token` - **PENDING** (Requires Refresh Token table)
- [x] `POST /forgot-password` - Implementation complete with SMTP email.
- [x] `POST /reset-password` - Implementation complete.

## 👤 2. Users Module (`/users`)
- [x] `GET /profile` - Implementation complete.
- [x] `PUT /profile` - Implementation complete (Update username/email).
- [x] `POST /upload-picture` - Covered by `POST /upload/product-image` (Admin only).
- [x] `POST /change-address` - Implementation complete (Acknowledge placeholder).
- [x] `DELETE /delete-account` - Implementation complete (GDPR placeholder).

## 🍎 3. Products & Categories (`/products`, `/categories`)
- [x] `GET /products` - Implementation complete.
- [x] `GET /products/{id}` - Implementation complete.
- [x] `POST /products` - Implementation complete (Admin only).
- [x] `GET /products/search` - Implementation complete (Search by name/desc).
- [ ] `GET /products/filter` - **PENDING** (Advanced multi-filter).
- [x] `GET /categories` - Implementation complete.
- [x] `POST /categories` - Implementation complete (Admin only).

## 🛒 4. Cart Module (`/cart`)
- [x] `POST /cart/add` - Implementation complete.
- [x] `DELETE /cart/remove/{productId}` - Implementation complete.
- [x] `PUT /cart/update-qty` - Implementation complete.
- [x] `DELETE /cart/clear` - Implementation complete.
- [x] `GET /cart` - Implementation complete.

## 📦 5. Orders Module (`/orders`)
- [x] `POST /orders/checkout` - Implementation complete (with distance-based fees).
- [ ] `GET /orders/track/{id}` - **IN PROGRESS** (Driver Real-time tracking).
- [x] `GET /orders` - Implementation complete (Order History).
- [x] `POST /orders/cancel/{id}` - Handled by automated cron job + endpoint placeholder.

## 💳 6. Payments Module (`/payments`)
- [x] `POST /payments/stk-push` - Implementation complete (Daraja Sandbox).
- [x] `POST /payments/callback` - Implementation complete (Automated ngrok processing).
- [ ] `GET /payments/verify` - **PENDING** (Status check endpoint).
- [ ] `GET /payments/history` - **PENDING**

## 🚚 7. Drivers Module (`/driver`)
- [x] `GET /driver/available-orders` - Implementation complete (Driver only).
- [x] `POST /driver/accept/{orderId}` - Implementation complete (Driver only).
- [x] `PUT /driver/status/{orderId}` - Implementation complete (`PICKED_UP`, `DELIVERED`) (Driver only).
- [x] `GET /driver/my-orders` - Implementation complete (Driver only).
- [ ] `POST /driver/location` - **NEXT (Tomorrow)**

## ⭐ 8. Reviews Module (`/reviews`)
- [x] `POST /reviews/add` - Implementation complete (Verified Purchase check).
- [x] `GET /products/{id}/reviews` - Implementation complete.
- [ ] `POST /reviews/reply/{reviewId}` - **PENDING** (Admin only).

## 📊 9. Admin Module (`/admin`)
- [x] `GET /admin/dashboard` - Implementation complete (Admin only).
- [ ] `GET /admin/users` - **PENDING**
- [x] `GET /admin/inventory` - Implementation complete (Admin only).
- [x] `POST /admin/set-role/{userId}` - Implementation complete (Admin only).
- [x] `POST /make-me-admin` - Implementation complete (Dev helper).

---

## 📈 Summary Progress
- **Completed Endpoints**: ~37
- **Pending Endpoints**: ~3 (Mainly requires external services like Mailers or advanced filtering logic).
- **Next Session Focus**: Driver Real-time Location Tracking.
