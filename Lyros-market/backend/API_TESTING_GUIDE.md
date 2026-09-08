# Kijani Market API - Comprehensive Testing Guide

This guide covers all implemented endpoints. Ensure the server is running and `ngrok` is active for M-Pesa.

---

## 1. Authentication & User Profile
### 1.1 Register
- **POST** `/auth/register`
- **Body**: `{"username": "tester", "email": "test@example.com", "password": "password123"}`

### 1.2 Login (Get Token)
- **POST** `/auth/login`
- **Body**: `{"email": "test@example.com", "password": "password123"}`
- **ACTION**: Copy the `token`. In Postman, go to **Auth > Bearer Token** and paste it for all subsequent requests.

### 1.3 Forgot Password
- **POST** `/auth/forgot-password`
- **Body**: `{"email": "test@example.com"}`
- **ACTION**: Check IntelliJ logs for the 6-digit code.

### 1.4 Reset Password
- **POST** `/auth/reset-password`
- **Body**: `{"email": "test@example.com", "code": "123456", "newPassword": "newPassword123"}`

### 1.5 Update Profile
- **PUT** `/users/profile`
- **Body**: `{"username": "tester_updated", "email": "test@example.com"}`

### 1.6 Get Profile
- **GET** `/users/profile`

---

## 2. Images & Products
### 2.1 Upload Image (Admin Only)
- **POST** `/upload/product-image`
- **Auth**: Bearer Token (Admin)
- **Body**: `form-data` -> Key: `image`, Type: `File`, Value: `[Select Image]`
- **ACTION**: Copy the returned `imageUrl`.

### 2.2 Create Category (Admin Only)
- **POST** `/categories`
- **Auth**: Bearer Token (Admin)
- **Body**: `{"name": "Vegetables", "description": "Fresh from farm", "parentId": null}`
- **ACTION**: Note the `id` of the created category.

### 2.3 Create Sub-Category (Admin Only)
- **POST** `/categories`
- **Auth**: Bearer Token (Admin)
- **Body**: `{"name": "Organic Vegetables", "description": "Locally sourced organic veggies", "parentId": 1}`
- **ACTION**: Replace `1` with the ID of your main "Vegetables" category.

### 2.4 Create Product (Admin Only)
- **POST** `/products`
- **Auth**: Bearer Token (Admin)
- **Body**: 
```json
{
    "name": "Kales",
    "description": "Organic Sukuma Wiki",
    "price": 50.0,
    "categoryId": 1,
    "stockQuantity": 100,
    "imageUrl": "/uploads/uuid.jpg",
    "isHighDemand": false
}
```

### 2.5 Update Product (Admin Only)
- **PUT** `/admin/products/{productId}`
- **Auth**: Bearer Token (Admin)
- **Body**: 
```json
{
    "name": "Updated Kales",
    "description": "Organic Sukuma Wiki (Updated)",
    "price": 55.0,
    "categoryId": 1,
    "stockQuantity": 120,
    "imageUrl": "/uploads/new_uuid.jpg",
    "isHighDemand": true
}
```

### 2.6 Delete Product (Admin Only)
- **DELETE** `/admin/products/{productId}`
- **Auth**: Bearer Token (Admin)

### 2.7 Get All Products (Paginated)
- **GET** `/products?page=1&size=10`
- **ACTION**: Optional `categoryId` parameter: `/products?categoryId=1&page=1&size=10`

### 2.8 Search Products (Paginated)
- **GET** `/products/search?q=kale&page=1&size=10`

### 2.9 Get High Demand Products (Paginated)
- **GET** `/products/high-demand?page=1&size=10`

---

## 🛒 3. Shopping Cart
### 3.1 Add to Cart
- **POST** `/cart/add`
- **Body**: `{"productId": 1, "quantity": 5}`

### 3.2 Update Quantity
- **PUT** `/cart/update-qty`
- **Body**: `{"productId": 1, "quantity": 10}`

### 3.3 View Cart
- **GET** `/cart`

### 3.4 Remove Item
- **DELETE** `/cart/remove/1`

### 3.5 Clear Cart
- **DELETE** `/cart/clear`

---

## 4. Orders & Payments
### 4.1 Checkout
- **POST** `/orders/checkout`
- **Body**: `{"deliveryAddress": "Eldoret CBD", "latitude": 0.5142, "longitude": 35.2697}`

### 4.2 Pay via M-Pesa (STK Push)
- **POST** `/payments/stk-push`
- **Body**: `{"orderId": 1, "phoneNumber": "2547XXXXXXXX"}`

### 4.3 View Order History
- **GET** `/orders`

### 4.4 View All Orders (Admin Only)
- **GET** `/admin/orders`
- **Auth**: Bearer Token (Admin)

### 4.5 Track Order (Customer/Admin)
- **GET** `/orders/{orderId}/track`
- **Auth**: Bearer Token (Customer or Admin)
- **ACTION**: Requires the order to be assigned to a driver.

---

## 5. Driver Workflow (Driver Only)
### 5.1 Available Orders
- **GET** `/driver/available-orders`
- **Auth**: Bearer Token (Driver)

### 5.2 Accept Order
- **POST** `/driver/accept/1`
- **Auth**: Bearer Token (Driver)

### 5.3 Update Status (Delivered)
- **PUT** `/driver/status/1`
- **Auth**: Bearer Token (Driver)
- **Body**: `{"status": "DELIVERED"}`

### 5.4 Update Location
- **POST** `/driver/location`
- **Auth**: Bearer Token (Driver)
- **Body**: `{"latitude": 0.5142, "longitude": 35.2697}`

---

## 6. Admin & Reviews
### 6.1 Set Admin Role (Development)
- **POST** `/make-me-admin`
- **ACTION**: Re-Login to refresh your token.

### 6.2 Admin Dashboard (Admin Only)
- **GET** `/admin/dashboard`
- **Auth**: Bearer Token (Admin)

### 6.3 View All Users (Admin Only)
- **GET** `/admin/users`
- **Auth**: Bearer Token (Admin)

### 6.4 Update User (Admin Only)
- **PUT** `/admin/users/{userId}`
- **Auth**: Bearer Token (Admin)
- **Body**: `{"username": "new_username", "email": "new_email@example.com", "role": "USER"}`

### 6.5 Delete User (Admin Only - Soft Delete)
- **DELETE** `/admin/users/{userId}`
- **Auth**: Bearer Token (Admin)

### 6.6 View Inventory (Admin Only)
- **GET** `/admin/inventory`
- **Auth**: Bearer Token (Admin)

### 6.7 Update Category (Admin Only)
- **PUT** `/admin/categories/{categoryId}`
- **Auth**: Bearer Token (Admin)
- **Body**: `{"name": "Updated Vegetables", "description": "Fresh from farm (Updated)", "parentId": null}`

### 6.8 Delete Category (Admin Only)
- **DELETE** `/admin/categories/{categoryId}`
- **Auth**: Bearer Token (Admin)

### 6.9 Set User Role (Admin Only)
- **POST** `/admin/set-role/{userId}`
- **Auth**: Bearer Token (Admin)
- **Body**: `{"role": "DRIVER"}` (or "USER", "ADMIN")

### 6.10 Mark Order Paid (Admin Only)
- **POST** `/orders/test-mark-paid/1`
- **Auth**: Bearer Token (Admin)

### 6.11 Add Review (Post-Delivery)
- **POST** `/reviews/add`
- **Body**: `{"productId": 1, "rating": 5, "comment": "Excellent quality!"}`

### 6.12 View Product Reviews
- **GET** `/products/1/reviews`
