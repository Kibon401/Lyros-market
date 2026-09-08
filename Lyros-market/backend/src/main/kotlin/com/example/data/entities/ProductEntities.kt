package com.example.data.entities

import org.jetbrains.exposed.dao.id.IntIdTable

object CategoriesTable : IntIdTable("product_category") {
    val name = varchar("name", 50)
    val description = varchar("description", 255).nullable()
    val parentId = reference("parent_id", CategoriesTable).nullable() // Self-referencing foreign key
}

object ProductsTable : IntIdTable("product") {
    val name = varchar("name", 100)
    val description = text("description")
    val price = double("price")
    val categoryId = reference("category_id", CategoriesTable)
    val stockQuantity = integer("stock_quantity")
    val imageUrl = varchar("image_url", 255).nullable()
    val isHighDemand = bool("is_high_demand").default(false) // New field
}