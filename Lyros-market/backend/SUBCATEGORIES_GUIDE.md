# Understanding Hierarchical Categories (Sub-categories) in Kijani Market

This guide provides a detailed explanation of how sub-categories are implemented and managed in the Kijani Market backend. This approach allows for a flexible and organized product catalog, moving beyond a simple flat list of categories.

---

## 1. What are Hierarchical Categories?

Imagine your product catalog like a tree.
*   **Top-Level Categories** are the main branches (e.g., "Cereals", "Vegetables").
*   **Sub-categories** are smaller branches stemming from the main ones (e.g., "Rice" under "Cereals", "Leafy Greens" under "Vegetables").
*   **Products** are the leaves, attached to the smallest, most specific branch.

This structure helps users navigate large catalogs more easily and makes product organization more logical.

## 2. The Backend Implementation: The `parentId` Field

Our Kijani Market backend uses a simple yet powerful mechanism to create this hierarchy: the `parentId` field.

*   **`ProductCategory` Model (`src/main/kotlin/com/example/domain/models/Product.kt`):**
    ```kotlin
    @Serializable
    data class ProductCategory(
        val id: Int? = null,
        val name: String,
        val description: String? = null,
        val parentId: Int? = null // <-- This is the key!
    )
    ```
*   **`CategoriesTable` (`src/main/kotlin/com/example/data/entities/ProductEntities.kt`):**
    ```kotlin
    object CategoriesTable : IntIdTable("product_category") {
        val name = varchar("name", 50)
        val description = varchar("description", 255).nullable()
        val parentId = reference("parent_id", CategoriesTable).nullable() // <-- Self-referencing foreign key
    }
    ```

**How `parentId` Works:**

*   **Top-Level Category:** If a `ProductCategory` has `parentId = null`, it means it has no parent. It's a main, top-level category.
*   **Sub-Category:** If a `ProductCategory` has `parentId = <ID_of_another_category>`, it means it is a sub-category of the category identified by `<ID_of_another_category>`.

**Example:**
*   `Cereals` (id: 1, parentId: null)
*   `Rice` (id: 2, parentId: 1)  <-- `Rice` is a sub-category of `Cereals`
*   `Pishori Rice` (product, categoryId: 2) <-- `Pishori Rice` belongs to the `Rice` category.

## 3. Step-by-Step Example: Building Your Catalog

Let's use your provided list to demonstrate how to create categories and sub-categories using Postman.

**Assumptions:**
*   You have an **Admin JWT Token**.
*   Your database is clean or you're starting fresh.

---

### **Step 3.1: Create Top-Level Categories**

First, create your main categories. For these, `parentId` will be `null`.

**Example: Create "Cereals"**
*   **Endpoint:** `POST http://localhost:8080/categories`
*   **Auth:** Bearer Token (Admin)
*   **Body (JSON):**
    ```json
    {
        "name": "Cereals",
        "description": "Grains, flours, and legumes",
        "parentId": null
    }
    ```
*   **Action:** Send Request.
*   **Expected Response:** `201 Created` with the new category object. **Note down the `id` of "Cereals" (e.g., `1`).**

**Repeat for other top-level categories:** "Seeds", "Spreads", "Vegetables", "Fruits", "Spices", "Nuts", "Tubers", "Others". Note down their IDs.

---

### **Step 3.2: Create First-Level Sub-Categories**

Now, create categories that belong to a top-level category. For these, `parentId` will be the ID of their parent.

**Example: Create "Rice" (under "Cereals")**
*   **Endpoint:** `POST http://localhost:8080/categories`
*   **Auth:** Bearer Token (Admin)
*   **Body (JSON):**
    ```json
    {
        "name": "Rice",
        "description": "Various types of rice",
        "parentId": 1  // <-- Use the ID of the "Cereals" category here
    }
    ```
*   **Action:** Send Request.
*   **Expected Response:** `201 Created` with the new category object. **Note down the `id` of "Rice" (e.g., `2`).**

**Example: Create "Beans" (under "Cereals")**
*   **Endpoint:** `POST http://localhost:8080/categories`
*   **Auth:** Bearer Token (Admin)
*   **Body (JSON):**
    ```json
    {
        "name": "Beans",
        "description": "Different varieties of beans",
        "parentId": 1  // <-- Use the ID of the "Cereals" category here
    }
    ```
*   **Action:** Send Request.
*   **Expected Response:** `201 Created` with the new category object. **Note down the `id` of "Beans" (e.g., `3`).**

---

### **Step 3.3: Create Products and Link to the Lowest-Level Category**

When creating a product, you link it to the *most specific* category it belongs to.

**Example: Create "Pishori Rice"**
*   **Endpoint:** `POST http://localhost:8080/products`
*   **Auth:** Bearer Token (Admin)
*   **Body (JSON):**
    ```json
    {
        "name": "Pishori Rice",
        "description": "Aromatic Pishori rice, premium quality.",
        "price": 220.0,
        "categoryId": 2, // <-- Use the ID of the "Rice" sub-category here
        "stockQuantity": 300,
        "imageUrl": "pishori_rice.jpg",
        "isHighDemand": false
    }
    ```
*   **Action:** Send Request.
*   **Expected Response:** `201 Created` with the new product object.

**Example: Create "Dengu"**
*   **Endpoint:** `POST http://localhost:8080/products`
*   **Auth:** Bearer Token (Admin)
*   **Body (JSON):**
    ```json
    {
        "name": "Dengu (Green Grams)",
        "description": "Fresh green grams, locally sourced.",
        "price": 180.0,
        "categoryId": 3, // <-- Use the ID of the "Beans" sub-category here
        "stockQuantity": 200,
        "imageUrl": "dengu.jpg",
        "isHighDemand": false
    }
    ```
*   **Action:** Send Request.
*   **Expected Response:** `201 Created` with the new product object.

---

## 4. How the Frontend Uses This Hierarchy

When the frontend calls `GET http://localhost:8080/categories`, it receives a flat list of `ProductCategory` objects, each potentially having a `parentId`.

**Frontend's Job:**

1.  **Fetch All Categories:** Get the flat list from `GET /categories`.
2.  **Build the Tree:** Write logic to iterate through this list and construct a tree or nested structure based on the `parentId` relationships.
    *   Categories with `parentId = null` are root nodes.
    *   Categories with a `parentId` are children of the category matching that `id`.
3.  **Display Navigation:** Use this tree to render nested menus, dropdowns, or breadcrumbs in the UI.
4.  **Filter Products:** When a user selects a category (e.g., "Rice"), the frontend takes that category's `id` (e.g., `2`) and calls `GET http://localhost:8080/products?categoryId=2`. This will retrieve all products directly associated with the "Rice" category.

**Important Note:** Our current `getProductsByCategory(categoryId)` method only retrieves products directly linked to that specific `categoryId`. If you wanted to retrieve products from a parent category *and all its sub-categories*, that would require a more complex query on the backend (e.g., a recursive query or fetching all child category IDs first). For now, assume filtering is for the *selected* category only.

---

This detailed guide should clarify the concept and practical application of hierarchical categories in your Kijani Market backend!
