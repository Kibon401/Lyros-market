package com.example.data.repository

import com.example.data.database.DatabaseFactory.dbQuery
import com.example.data.entities.CartItemsTable
import com.example.data.entities.ProductsTable
import com.example.domain.models.CartItem
import com.example.domain.repositories.CartRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class CartRepositoryImpl : CartRepository {
    override suspend fun getCart(userId: Int): List<CartItem> = dbQuery {
        (CartItemsTable innerJoin ProductsTable)
            .selectAll().where { CartItemsTable.userId eq userId }
            .map {
                CartItem(
                    productId = it[CartItemsTable.productId].value,
                    productName = it[ProductsTable.name],
                    quantity = it[CartItemsTable.quantity],
                    price = it[ProductsTable.price],
                    imageUrl = it[ProductsTable.imageUrl]
                )
            }
    }

    override suspend fun addToCart(userId: Int, productId: Int, quantity: Int): Unit = dbQuery {
        val existing = CartItemsTable.selectAll()
            .where { (CartItemsTable.userId eq userId) and (CartItemsTable.productId eq productId) }
            .singleOrNull()

        if (existing != null) {
            CartItemsTable.update({ (CartItemsTable.userId eq userId) and (CartItemsTable.productId eq productId) }) {
                it[CartItemsTable.quantity] = existing[CartItemsTable.quantity] + quantity
            }
        } else {
            CartItemsTable.insert {
                it[CartItemsTable.userId] = userId
                it[CartItemsTable.productId] = productId
                it[CartItemsTable.quantity] = quantity
            }
        }
        Unit
    }

    override suspend fun removeFromCart(userId: Int, productId: Int): Unit = dbQuery {
        CartItemsTable.deleteWhere { (CartItemsTable.userId eq userId) and (CartItemsTable.productId eq productId) }
        Unit
    }

    override suspend fun updateQuantity(userId: Int, productId: Int, quantity: Int): Unit = dbQuery {
        CartItemsTable.update({ (CartItemsTable.userId eq userId) and (CartItemsTable.productId eq productId) }) {
            it[CartItemsTable.quantity] = quantity
        }
        Unit
    }

    override suspend fun clearCart(userId: Int): Unit = dbQuery {
        CartItemsTable.deleteWhere { CartItemsTable.userId eq userId }
        Unit
    }
}
