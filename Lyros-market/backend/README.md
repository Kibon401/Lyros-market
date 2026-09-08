# Kijani Market - Technical System Specification

This document serves as the primary technical reference for builders working on the Kijani Market backend. It outlines the architectural patterns, data persistence layer, and service integrations.

---

## 1. System Architecture: The Clean Hexagon
The project follows **Clean Architecture** principles, enforcing a strict unidirectional dependency flow:
`Presentation -> Domain <- Data`.

### Layer Responsibilities:
- **Domain Layer (`/domain`):** Contains the "Source of Truth."
    - **Models:** POJOs (Plain Old Java Objects) using `kotlinx.serialization`.
    - **Repository Interfaces:** Contractual definitions that the Data layer must satisfy.
- **Data Layer (`/data`):** Handles persistence and external infrastructure.
    - **Entities:** Exposed DSL Table mappings.
    - **Repository Implementations:** Concrete logic for SQL execution and external API calls (e.g., Daraja).
    - **Database Factory:** Singleton for HikariCP connection pooling and schema migrations.
- **Presentation Layer (`/presentation`):** The Ktor interface.
    - **Routes:** Endpoint definitions and input validation.
    - **DTOs:** Request/Response objects to prevent exposing internal entities.

---

## 2. Persistence Layer: Exposed ORM
We utilize the **JetBrains Exposed DSL** for typesafe SQL construction.

### Table Schema & Relationships:
- **`UsersTable`:** Inherits `IntIdTable`. Stores BCrypt-hashed credentials and roles.
- **`ProductsTable`:** Holds a `reference` (Foreign Key) to `CategoriesTable`. Includes a `stockQuantity` integer for atomic inventory updates.
- **`OrdersTable`:** The central hub. Maps a `userId` (Customer) and an optional `driverId` (both referencing `UsersTable`).
- **`OrderLinesTable`:** A junction table for a many-to-many relationship between Orders and Products, capturing the `priceAtTime` to preserve historical financial data.
- **`MpesaPaymentsTable`:** Tracks STK Push lifecycle using `MerchantRequestID` and `CheckoutRequestID` as primary lookups for Safaricom callbacks.
- **`DriverLocationsTable`:** Stores real-time location updates (latitude, longitude, timestamp) for drivers.

### Transaction Management:
All database interactions are wrapped in the `dbQuery` utility, which utilizes `newSuspendedTransaction(Dispatchers.IO)` to ensure thread-safe, non-blocking I/O.

---

## 3. Security & Identity Management
### JWT (JSON Web Token) Implementation:
- **Authentication:** Bearer token strategy.
- **Payload Claims:** Includes `userId` and `role`. This allows for "Stateless Authorization"—the server knows the user's permissions without querying the DB on every request.
- **RBAC (Role-Based Access Control):** Implemented via Ktor `interceptors` that check claims before reaching the route handler.

### Cryptography:
- **Passwords:** Hashed using `JBcrypt` with a default salt rounds of 12.
- **M-Pesa:** Credentials generated via `Base64` encoding of Consumer Key/Secret for OAuth2 and `SHA256` for STK Push passwords.

---

## 4. Business Logic & Integrations

### Geo-Fencing & Delivery Logic:
- **Distance Calculation:** Uses the **Haversine Formula** (implemented in `LocationUtils`) to calculate the great-circle distance between Eldoret Center and the client's coordinates.
- **Fee Engine:** A linear function: `Base_Fee + (Per_KM_Rate * Distance)`.

### M-Pesa Callback Handshake:
1. **Initiation:** Server sends STK Push to Safaricom via Ktor `HttpClient`.
2. **Persistence:** Server stores the `CheckoutRequestID` with a `PENDING` status.
3. **Webhook:** Safaricom sends an asynchronous `POST` to `/payments/callback` via the **ngrok tunnel**.
4. **Resolution:** The server validates the `ResultCode`, extracts the `MpesaReceiptNumber`, and atomically updates both the Payment and the Order status to `PAID`.

### Concurrency & Cron Jobs:
- **Inventory Protection:** To prevent "ghost stock" (reserved items that are never paid for), a `timer` task runs in `Application.kt`.
- **Logic:** It scans for `PENDING` orders > 30 mins, executes an atomic SQL addition to `ProductsTable.stockQuantity`, and marks the order as `CANCELLED`.

---

## 5. Builder's Workflow
- **Environment:** ngrok is mandatory for testing payments locally.
- **Migrations:** Managed automatically via `SchemaUtils.createMissingTablesAndColumns`.
- **Testing:** Use `API_TESTING_GUIDE.md` for the sequence of operations (Auth -> Upload -> Category -> Product).

---

## 6. Current Technical Status
- **Enhanced Security:** All critical endpoints now have explicit role-based access control (RBAC) checks (Admin-only for product/category creation, Driver-only for driver routes).
- **Password Reset (SMTP):** Implemented `forgot-password` and `reset-password` endpoints with email-based code verification.
- **Inventory Validation:** Implemented pre-order stock checks to prevent negative values.
- **Analytics Engine:** Custom Exposed functions for daily growth and revenue aggregation.
- **Image Serving:** Multi-part file upload with UUID obfuscation and static serving.
- **Driver Tracking:** Real-time location updates for drivers and retrieval for order tracking.
- **Hierarchical Categories:** Support for nested categories using `parentId`.
- **High Demand Products:** Flagging and retrieval of high-demand products.
- **Pagination:** Implemented for product listing endpoints.

---

## 7. Key Implementations & Guides for Builders

### 7.1 SMTP Email Service (Gmail Configuration)

The backend includes a feature for password reset that relies on sending emails via an SMTP server. For development and testing, you can easily configure this using a Gmail account.

**File:** `src/main/kotlin/com/example/core/utils/EmailService.kt`

**Steps to Configure Gmail:**

1.  **Enable 2-Step Verification for your Gmail Account:**
    *   Go to your Google Account settings: `myaccount.google.com`.
    *   Navigate to the "Security" section.
    *   Under "How you sign in to Google," ensure "2-Step Verification" is ON. This is a prerequisite for generating App Passwords.

2.  **Generate an App Password:**
    *   Still in the "Security" section of your Google Account, find "App passwords" (you might need to scroll down).
    *   You may be asked to re-enter your Google password.
    *   From the "Select app" dropdown, choose "Mail".
    *   From the "Select device" dropdown, choose "Other (Custom name)" and give it a name like "Kijani Market App".
    *   Click "Generate". Google will provide a 16-character password. **Copy this password immediately.** You won't see it again.

3.  **Update `EmailService.kt`:**
    *   Open `src/main/kotlin/com/example/core/utils/EmailService.kt`.
    *   **Uncomment** the line `Transport.send(message)`.
    *   Set `SENDER_EMAIL` to your actual Gmail address (e.g., `"your.email@gmail.com"`).
    *   Set `SENDER_PASSWORD` to the **16-character App Password** you generated in step 2 (NOT your regular Gmail password).
    *   Ensure `SMTP_HOST` is `"smtp.gmail.com"` and `SMTP_PORT` is `"587"`.

4.  **Restart your Ktor Server:** The changes will take effect upon server restart.

Now, when you trigger the `POST /auth/forgot-password` endpoint, your server will send an actual email with the reset code.

### 7.2 Hierarchical Categories (Sub-categories)

The product catalog supports nested categories, allowing for a more organized and granular product classification (e.g., "Cereals" -> "Rice" -> "Pishori Rice").

**Implementation Details:**

*   **Model (`ProductCategory`):** The `ProductCategory` data class now includes a `parentId: Int?` field. If `parentId` is `null`, it's a top-level category. If it contains an ID, it refers to its parent category.
*   **Database (`CategoriesTable`):** The `product_category` table has a `parent_id` column, which is a self-referencing foreign key to `product_category.id`.
*   **API Endpoints:**
    *   `POST /categories` (Admin Only): When creating a category, you can optionally include `"parentId": <ID_of_parent_category>` in the request body.
    *   `PUT /admin/categories/{categoryId}` (Admin Only): You can update an existing category's `parentId` to move it within the hierarchy.
    *   `GET /categories`: Returns a flat list of all categories, each with its `parentId` (if any).

**Frontend Responsibility:**

The frontend application is responsible for consuming the flat list of categories from `GET /categories` and then building the hierarchical (tree-like) structure in the UI for display and navigation (e.g., nested menus, breadcrumbs). When filtering products, the `categoryId` parameter in `GET /products?categoryId={id}` should point to the specific category (or sub-category) whose products you wish to retrieve.
