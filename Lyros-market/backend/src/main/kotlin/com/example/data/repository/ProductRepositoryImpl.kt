package com.example.data.repository

import com.example.data.database.DatabaseFactory.dbQuery
import com.example.data.entities.CategoriesTable
import com.example.data.entities.ProductsTable
import com.example.domain.models.Product
import com.example.domain.models.ProductCategory
import com.example.domain.repositories.ProductRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.booleanLiteral

class ProductRepositoryImpl : ProductRepository {
    override suspend fun getAllProducts(page: Int, size: Int): List<Product> = dbQuery {
        ProductsTable.selectAll()
            .limit(size, offset = (page - 1) * size.toLong())
            .map { rowToProduct(it) }
    }

    override suspend fun getProductById(id: Int): Product? = dbQuery {
        ProductsTable.selectAll().where { ProductsTable.id eq id }
            .map { rowToProduct(it) }
            .singleOrNull()
    }

    override suspend fun getProductsByCategory(categoryId: Int, page: Int, size: Int): List<Product> = dbQuery {
        ProductsTable.selectAll().where { ProductsTable.categoryId eq categoryId }
            .limit(size, offset = (page - 1) * size.toLong())
            .map { rowToProduct(it) }
    }

    override suspend fun getAllCategories(): List<ProductCategory> = dbQuery {
        CategoriesTable.selectAll().map { 
            ProductCategory(
                id = it[CategoriesTable.id].value,
                name = it[CategoriesTable.name],
                description = it[CategoriesTable.description],
                parentId = it[CategoriesTable.parentId]?.value // Include parentId
            )
        }
    }

    override suspend fun createProduct(product: Product): Product? = dbQuery {
        val insertStatement = ProductsTable.insert {
            it[name] = product.name
            it[description] = product.description
            it[price] = product.price
            it[categoryId] = product.categoryId
            it[stockQuantity] = product.stockQuantity
            it[imageUrl] = product.imageUrl
            it[isHighDemand] = booleanLiteral(product.isHighDemand) // Handle isHighDemand
        }
        insertStatement.resultedValues?.singleOrNull()?.let { rowToProduct(it) }
    }

    override suspend fun createCategory(category: ProductCategory): ProductCategory? = dbQuery {
        val insertStatement = CategoriesTable.insert {
            it[name] = category.name
            it[description] = category.description
            it[parentId] = category.parentId // Handle parentId
        }
        insertStatement.resultedValues?.singleOrNull()?.let {
            ProductCategory(
                id = it[CategoriesTable.id].value,
                name = it[CategoriesTable.name],
                description = it[CategoriesTable.description],
                parentId = it[CategoriesTable.parentId]?.value
            )
        }
    }

    override suspend fun searchProducts(query: String, page: Int, size: Int): List<Product> = dbQuery {
        ProductsTable.selectAll().where { 
            (ProductsTable.name.lowerCase() like "%${query.lowercase()}%") or 
            (ProductsTable.description.lowerCase() like "%${query.lowercase()}%") 
        }
            .limit(size, offset = (page - 1) * size.toLong())
            .map { rowToProduct(it) }
    }

    override suspend fun updateProduct(
        productId: Int, 
        name: String, 
        description: String, 
        price: Double, 
        categoryId: Int, 
        stockQuantity: Int, 
        imageUrl: String?,
        isHighDemand: Boolean
    ): Boolean = dbQuery {
        ProductsTable.update({ ProductsTable.id eq productId }) {
            it[ProductsTable.name] = name
            it[ProductsTable.description] = description
            it[ProductsTable.price] = price
            it[ProductsTable.categoryId] = categoryId
            it[ProductsTable.stockQuantity] = stockQuantity
            it[ProductsTable.imageUrl] = imageUrl
            it[ProductsTable.isHighDemand] = booleanLiteral(isHighDemand) // Handle isHighDemand
        } > 0
    }

    override suspend fun deleteProduct(productId: Int): Boolean = dbQuery {
        ProductsTable.deleteWhere { ProductsTable.id eq productId } > 0
    }

    override suspend fun updateCategory(categoryId: Int, name: String, description: String?, parentId: Int?): Boolean = dbQuery {
        CategoriesTable.update({ CategoriesTable.id eq categoryId }) {
            it[CategoriesTable.name] = name
            it[CategoriesTable.description] = description
            it[CategoriesTable.parentId] = parentId // Handle parentId
        } > 0
    }

    override suspend fun deleteCategory(categoryId: Int): Boolean = dbQuery {
        CategoriesTable.deleteWhere { CategoriesTable.id eq categoryId } > 0
    }

    override suspend fun getHighDemandProducts(page: Int, size: Int): List<Product> = dbQuery {
        ProductsTable.selectAll().where { ProductsTable.isHighDemand eq booleanLiteral(true) }
            .limit(size, offset = (page - 1) * size.toLong())
            .map { rowToProduct(it) }
    }

    private fun rowToProduct(row: ResultRow) = Product(
        id = row[ProductsTable.id].value,
        name = row[ProductsTable.name],
        description = row[ProductsTable.description],
        price = row[ProductsTable.price],
        categoryId = row[ProductsTable.categoryId].value,
        stockQuantity = row[ProductsTable.stockQuantity],
        imageUrl = row[ProductsTable.imageUrl],
        isHighDemand = row[ProductsTable.isHighDemand]
    )
}
