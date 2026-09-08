# Kijani Market - Frontend Application Specification

This document outlines all the essential screens required for the Kijani Market frontend application, detailing their purpose, the backend API endpoints they interact with, and the key data they need to display or collect. This serves as a functional specification for frontend development.

---

## 1. Authentication & User Management

### 1.1 Login Screen
*   **Purpose:** Allows existing users to authenticate and gain access to the application.
*   **Backend Interaction:** `POST /auth/login`
*   **Data Collected:** User's email, password.
*   **Data Displayed:** Error messages (e.g., invalid credentials).

### 1.2 Registration Screen
*   **Purpose:** Allows new users to create an account.
*   **Backend Interaction:** `POST /auth/register`
*   **Data Collected:** Desired username, email, password, password confirmation.
*   **Data Displayed:** Error messages (e.g., email already exists, password mismatch).

### 1.3 Forgot Password (Request Code) Screen
*   **Purpose:** Allows users to initiate a password reset by requesting a verification code.
*   **Backend Interaction:** `POST /auth/forgot-password`
*   **Data Collected:** User's email address.
*   **Data Displayed:** Confirmation message (code sent), error messages.

### 1.4 Reset Password (Verify & Update) Screen
*   **Purpose:** Allows users to enter a verification code and set a new password.
*   **Backend Interaction:** `POST /auth/reset-password`
*   **Data Collected:** User's email, 6-digit verification code, new password, new password confirmation.
*   **Data Displayed:** Success message (password reset), error messages (e.g., invalid/expired code, password mismatch).

### 1.5 User Profile View Screen
*   **Purpose:** Displays the authenticated user's personal information.
*   **Backend Interaction:** `GET /users/profile`
*   **Data Displayed:** Username, email, user role, (optional) profile picture, (optional) aggregated order statistics.
*   **Actions:** Navigate to profile edit, order history, address management.

### 1.6 User Profile Edit/Settings Screen
*   **Purpose:** Allows the authenticated user to update their profile details.
*   **Backend Interaction:** `PUT /users/profile`
*   **Data Collected:** Updated username, email.
*   **Data Displayed:** Current profile data (pre-filled), success/error messages.
*   **Actions:** Save changes, cancel, navigate to change password, manage addresses, delete account.

### 1.7 User Address Management Screen
*   **Purpose:** Allows users to view, add, edit, and delete their delivery addresses.
*   **Backend Interaction:** (Placeholder: `POST /users/change-address` for default). Full CRUD for addresses would require: `GET /users/addresses`, `POST /users/addresses`, `PUT /users/addresses/{id}`, `DELETE /users/addresses/{id}`.
*   **Data Collected:** Full address details (street, city, postal code, latitude, longitude).
*   **Data Displayed:** List of saved addresses, indication of default address.
*   **Actions:** Add new address, edit existing, set as default, delete address.

### 1.8 Confirm Delete Account Screen
*   **Purpose:** Provides a final confirmation before a user's account is soft-deleted.
*   **Backend Interaction:** `DELETE /users/delete-account` (currently a placeholder, backend implements soft delete).
*   **Data Displayed:** Warning message about account deactivation.
*   **Actions:** Confirm deletion, cancel.

---

## 2. Product Catalog & Details

### 2.1 Product Catalog/Home Screen
*   **Purpose:** Displays a list of available products, allows searching and filtering.
*   **Backend Interaction:**
    *   `GET /products` (all products)
    *   `GET /products?categoryId={id}` (filtered products)
    *   `GET /products/search?q={query}` (search results)
    *   `GET /categories` (for filter options)
    *   `POST /cart/add` (add to cart)
*   **Data Displayed:** Product image, name, price, (optional) short description, "Add to Cart" button. List of categories for filtering.
*   **Actions:** Search, filter by category, view product details, add to cart.

### 2.2 Product Detail Screen
*   **Purpose:** Displays comprehensive information about a single product.
*   **Backend Interaction:**
    *   `GET /products/{id}` (product details)
    *   `GET /products/{id}/reviews` (product reviews)
    *   `POST /cart/add` (add to cart)
*   **Data Displayed:** Product image(s), name, full description, price, stock availability, farm information, overall rating, individual reviews (reviewer, rating, comment, date, verified status), related products.
*   **Actions:** Select quantity, add to cart, view all reviews, navigate to similar product details.

---

## 3. Shopping Cart & Checkout

### 3.1 Cart View & Management Screen
*   **Purpose:** Displays items in the shopping cart and allows management before checkout.
*   **Backend Interaction:**
    *   `GET /cart` (view cart contents and total)
    *   `PUT /cart/update-qty` (update item quantity)
    *   `DELETE /cart/remove/{productId}` (remove item)
    *   `DELETE /cart/clear` (clear cart)
*   **Data Displayed:** List of cart items (product image, name, quantity, price), subtotal, delivery fee (if estimated), total amount.
*   **Actions:** Increase/decrease item quantity, remove item, clear cart, proceed to checkout.

### 3.2 Payment Initiation Screen
*   **Purpose:** Confirms order details and initiates the M-Pesa STK Push payment process.
*   **Backend Interaction:** `POST /orders/checkout`, `POST /payments/stk-push`
*   **Data Collected:** Delivery address, (optional) latitude/longitude, M-Pesa phone number.
*   **Data Displayed:** Order summary (items, subtotal, delivery fee, total), delivery address.
*   **Actions:** Confirm order and initiate M-Pesa payment.

### 3.3 M-Pesa Payment Status (Processing) Screen
*   **Purpose:** Informs the user that their M-Pesa payment is being processed.
*   **Backend Interaction:** Triggered after `POST /payments/stk-push`. Frontend needs to poll or listen for status.
*   **Data Displayed:** "Processing Payment" message, loading indicator, amount, order ID.
*   **Actions:** (Optional) Cancel payment (frontend action).

### 3.4 Payment Success Screen
*   **Purpose:** Confirms that the M-Pesa payment was successful and the order is placed.
*   **Backend Interaction:** Triggered after successful `POST /payments/stk-push` and backend callback.
*   **Data Displayed:** Success message, order ID, total amount paid, M-Pesa transaction ID.
*   **Actions:** View order details, continue shopping.

### 3.5 Payment Failure Screen
*   **Purpose:** Informs the user that their M-Pesa payment failed.
*   **Backend Interaction:** Triggered after `POST /payments/stk-push` failure or callback indicates failure.
*   **Data Displayed:** Error message, order ID, total amount.
*   **Actions:** Try again, change payment method, contact support.

---

## 4. Order History & Tracking

### 4.1 Order History List Screen
*   **Purpose:** Displays a list of all past and current orders for the authenticated user.
*   **Backend Interaction:** `GET /orders`
*   **Data Displayed:** List of order cards (Order ID, total amount, date, item count, status, thumbnail image).
*   **Actions:** Filter by status (e.g., Delivered, In Transit), view order details.

### 4.2 Order Detail View Screen
*   **Purpose:** Displays comprehensive details of a single order for the customer.
*   **Backend Interaction:** `GET /orders/{id}`, `GET /orders/{id}/track` (for driver location, if assigned)
*   **Data Displayed:** Order ID, date, status, total amount, subtotal, delivery fee, list of ordered items, delivery address, (conditional) driver name, driver's last known location (latitude, longitude, timestamp), estimated time of arrival (ETA), order status timeline.
*   **Actions:** (Conditional) Contact driver, leave a review (if delivered), cancel order, reorder.

---

## 5. Driver Workflow

### 5.1 Driver Dashboard Screen
*   **Purpose:** Driver's main interface to manage their status and view available orders.
*   **Backend Interaction:**
    *   `PUT /driver/status` (set online/offline status)
    *   `GET /driver/available-orders`
    *   `POST /driver/accept/{orderId}`
*   **Data Displayed:** Driver's online/offline status, (optional) today's earnings, (optional) deliveries count. List of available orders (order ID, customer location, distance, items, total amount).
*   **Actions:** Toggle online/offline status, accept an available order.

### 5.2 Delivery Fulfillment/Tracking Screen (Driver's View)
*   **Purpose:** Driver's view for an active delivery, showing customer info, order items, and status updates.
*   **Backend Interaction:**
    *   `GET /orders/{id}` (order details)
    *   `PUT /driver/status/{orderId}` (update status to Picked Up/Delivered)
    *   `POST /driver/location` (driver sends location updates)
*   **Data Displayed:** Order ID, customer name, delivery address, order items, current order status, (optional) map with route.
*   **Actions:** Update status (Picked Up, Delivered), (optional) send location updates, (optional) contact customer.

### 5.3 Driver's Assigned Orders List Screen
*   **Purpose:** Driver to view a list of orders they have accepted and are currently responsible for.
*   **Backend Interaction:** `GET /driver/my-orders`
*   **Data Displayed:** List of assigned order cards (Order ID, customer name, delivery address, current status, total amount).
*   **Actions:** View order details (navigates to Delivery Fulfillment screen), filter by status.

---

## 6. Admin Dashboard & Management

### 6.1 Admin Dashboard Overview Screen
*   **Purpose:** Provides a high-level overview of key business metrics for administrators.
*   **Backend Interaction:** `GET /admin/dashboard`
*   **Data Displayed:** Total revenue, total orders, total users, top 5 products, user growth trend. (Optional: active orders, low stock items).
*   **Actions:** Navigate to user management, inventory management, order management.

### 6.2 Admin User Management (List & Detail) Screen
*   **Purpose:** Allows administrators to view, edit, and soft-delete user accounts.
*   **Backend Interaction:**
    *   `GET /admin/users` (list all users)
    *   `PUT /admin/users/{userId}` (update user details)
    *   `DELETE /admin/users/{userId}` (soft-delete user)
    *   `POST /admin/set-role/{userId}` (set user role)
*   **Data Displayed:** List of users (username, email, role, active status). User details for editing.
*   **Actions:** Search users, edit user details, change user role, soft-delete user.

### 6.3 Admin Inventory Management (List & Detail) Screen
*   **Purpose:** Allows administrators to view, search, filter, create, edit, and delete products.
*   **Backend Interaction:**
    *   `GET /admin/inventory` (list all products)
    *   `GET /products/search?q={query}` (search products)
    *   `GET /products?categoryId={id}` (filter by category)
    *   `POST /products` (create product)
    *   `PUT /admin/products/{productId}` (update product)
    *   `DELETE /admin/products/{productId}` (delete product)
    *   `POST /upload/product-image` (upload product image)
*   **Data Displayed:** List of products (image, name, price, stock, category, status). Product details for editing.
*   **Actions:** Search, filter by category, add new product, edit product, delete product, upload product image.

### 6.4 Admin Category Management (List & Detail) Screen
*   **Purpose:** Allows administrators to view, create, edit, and delete product categories.
*   **Backend Interaction:**
    *   `GET /categories` (list all categories)
    *   `POST /categories` (create category)
    *   `PUT /admin/categories/{categoryId}` (update category)
    *   `DELETE /admin/categories/{categoryId}` (delete category)
*   **Data Displayed:** List of categories (name, description). Category details for editing.
*   **Actions:** Add new category, edit category, delete category.

---

## 7. Reviews

### 7.1 Write Product Review Screen
*   **Purpose:** Allows a user to submit a review for a specific product after it has been delivered.
*   **Backend Interaction:** `POST /reviews/add`
*   **Data Collected:** Product ID, rating (1-5 stars), review comment.
*   **Data Displayed:** Product context (image, name), success/error messages.

---
