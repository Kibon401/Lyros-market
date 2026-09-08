package com.example.presentation.routes

import com.example.domain.models.Product
import com.example.domain.models.ProductCategory
import com.example.domain.repositories.ProductRepository
import com.example.presentation.requests.CreateProductRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.productRoutes(productRepository: ProductRepository) {
    get("/products") {
        val categoryId = call.request.queryParameters["categoryId"]?.toIntOrNull()
        val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
        val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 10

        val products = if (categoryId != null) {
            productRepository.getProductsByCategory(categoryId, page, size)
        } else {
            productRepository.getAllProducts(page, size)
        }
        call.respond(products)
    }

    get("/products/search") {
        val query = call.request.queryParameters["q"] ?: ""
        val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
        val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 10

        if (query.isEmpty()) {
            call.respond(emptyList<Product>())
            return@get
        }
        val results = productRepository.searchProducts(query, page, size)
        call.respond(results)
    }

    get("/products/high-demand") {
        val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
        val size = call.request.queryParameters["size"]?.toIntOrNull() ?: 10
        val products = productRepository.getHighDemandProducts(page, size)
        call.respond(products)
    }

    get("/products/{id}") {
        val idStr = call.parameters["id"]
        if (idStr == "search" || idStr == "high-demand") { // Handle routing overlap
            call.respond(HttpStatusCode.BadRequest, "Invalid product ID format or reserved keyword.")
            return@get
        }
        
        val id = idStr?.toIntOrNull()
        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid ID")
            return@get
        }
        val product = productRepository.getProductById(id)
        if (product != null) {
            call.respond(product)
        } else {
            call.respond(HttpStatusCode.NotFound)
        }
    }

    get("/categories") {
        val categories = productRepository.getAllCategories()
        call.respond(categories)
    }

    authenticate {
        // Admin-only routes for product and category creation/management
        route("/") {
            // Intercept all routes within this block to check for ADMIN role
            intercept(ApplicationCallPipeline.Call) {
                val principal = call.principal<JWTPrincipal>()
                val role = principal?.payload?.getClaim("role")?.asString()
                if (role != "ADMIN") {
                    call.respond(HttpStatusCode.Forbidden, "Admin access required")
                    return@intercept finish()
                }
            }

            post("/categories") {
                try {
                    val category = call.receive<ProductCategory>()
                    val created = productRepository.createCategory(category)
                    if (created != null) {
                        call.respond(HttpStatusCode.Created, created)
                    } else {
                        call.respond(HttpStatusCode.InternalServerError, "Failed to create category")
                    }
                } catch (e: Exception) {
                    println("Error creating category: ${e.message}")
                    call.respond(HttpStatusCode.BadRequest, "Invalid JSON: ${e.message}")
                }
            }

            post("/products") {
                try {
                    val request = call.receive<CreateProductRequest>()
                    
                    // Check if category exists first
                    val categories = productRepository.getAllCategories()
                    if (categories.none { it.id == request.categoryId }) {
                        call.respond(HttpStatusCode.BadRequest, "Category with ID ${request.categoryId} does not exist. Create it first.")
                        return@post
                    }

                    val product = Product(
                        name = request.name,
                        description = request.description,
                        price = request.price,
                        categoryId = request.categoryId,
                        stockQuantity = request.stockQuantity,
                        imageUrl = request.imageUrl,
                        isHighDemand = request.isHighDemand
                    )
                    val created = productRepository.createProduct(product)
                    if (created != null) {
                        call.respond(HttpStatusCode.Created, created)
                    } else {
                        call.respond(HttpStatusCode.InternalServerError, "Failed to create product")
                    }
                } catch (e: Exception) {
                    println("Error creating product: ${e.message}")
                    call.respond(HttpStatusCode.BadRequest, "Invalid JSON or missing fields: ${e.message}")
                }
            }
        }
    }
}
