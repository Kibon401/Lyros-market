package com.example.data.database

import com.example.domain.models.Product
import com.example.domain.models.ProductCategory
import com.example.domain.repositories.ProductRepository
import com.example.data.database.DatabaseFactory.dbQuery // Import dbQuery
import kotlinx.coroutines.runBlocking // Still needed for the outer call in DatabaseFactory

object SeedData {
    suspend fun seed(productRepository: ProductRepository) {
        dbQuery { // All database operations within this dbQuery block
            println("Seeding initial data...")

            // --- 1. Create Top-Level Categories ---
            val seedsCategory = productRepository.createCategory(ProductCategory(name = "Seeds", description = "Various healthy seeds"))
            val cerealsCategory = productRepository.createCategory(ProductCategory(name = "Cereals", description = "Grains and legumes"))
            val spreadsCategory = productRepository.createCategory(ProductCategory(name = "Spreads", description = "Natural spreads and sweeteners"))
            val vegetablesCategory = productRepository.createCategory(ProductCategory(name = "Vegetables", description = "Fresh farm vegetables"))
            val fruitsCategory = productRepository.createCategory(ProductCategory(name = "Fruits", description = "Fresh seasonal fruits"))
            val spicesCategory = productRepository.createCategory(ProductCategory(name = "Spices", description = "Aromatic herbs and spices"))
            val nutsCategory = productRepository.createCategory(ProductCategory(name = "Nuts", description = "Healthy and crunchy nuts"))
            val tubersCategory = productRepository.createCategory(ProductCategory(name = "Tubers", description = "Root vegetables"))
            val othersCategory = productRepository.createCategory(ProductCategory(name = "Others", description = "Miscellaneous farm products"))

            // --- 2. Create Sub-Categories ---
            val riceSubCategory = productRepository.createCategory(ProductCategory(name = "Rice", description = "Different varieties of rice", parentId = cerealsCategory?.id))
            val beansSubCategory = productRepository.createCategory(ProductCategory(name = "Beans", description = "Various types of beans", parentId = cerealsCategory?.id))

            // --- 3. Create Products ---
            // Seeds
            productRepository.createProduct(Product(name = "Pumpkin Seeds", description = "Nutrient-rich pumpkin seeds", price = 150.0, categoryId = seedsCategory!!.id!!, stockQuantity = 100, imageUrl = "pumpkin_seeds.jpg"))
            productRepository.createProduct(Product(name = "Watermelon Seeds", description = "Healthy watermelon seeds", price = 120.0, categoryId = seedsCategory.id!!, stockQuantity = 80, imageUrl = "watermelon_seeds.jpg"))
            productRepository.createProduct(Product(name = "Sesame (Simsim) Seeds", description = "Toasted sesame seeds", price = 180.0, categoryId = seedsCategory.id!!, stockQuantity = 150, imageUrl = "sesame_seeds.jpg"))
            productRepository.createProduct(Product(name = "Sunflower Seeds", description = "Crunchy sunflower seeds", price = 130.0, categoryId = seedsCategory.id!!, stockQuantity = 120, imageUrl = "sunflower_seeds.jpg"))
            productRepository.createProduct(Product(name = "Chia Seeds", description = "High-fiber chia seeds", price = 250.0, categoryId = seedsCategory.id!!, stockQuantity = 200, imageUrl = "chia_seeds.jpg", isHighDemand = true))

            // Cereals
            productRepository.createProduct(Product(name = "Maize Flour", description = "Fine maize flour", price = 100.0, categoryId = cerealsCategory!!.id!!, stockQuantity = 500, imageUrl = "maize_flour.jpg"))
            productRepository.createProduct(Product(name = "Millet Flour", description = "Nutritious millet flour", price = 160.0, categoryId = cerealsCategory.id!!, stockQuantity = 300, imageUrl = "millet_flour.jpg"))
            productRepository.createProduct(Product(name = "Sorghum Flour", description = "Healthy sorghum flour", price = 140.0, categoryId = cerealsCategory.id!!, stockQuantity = 250, imageUrl = "sorghum_flour.jpg"))
            productRepository.createProduct(Product(name = "Kamande", description = "Green grams (Kamande)", price = 200.0, categoryId = cerealsCategory.id!!, stockQuantity = 180, imageUrl = "kamande.jpg"))
            productRepository.createProduct(Product(name = "Lentils", description = "Brown lentils", price = 190.0, categoryId = cerealsCategory.id!!, stockQuantity = 170, imageUrl = "lentils.jpg"))

            // Rice (Sub-category)
            productRepository.createProduct(Product(name = "Pishori Rice", description = "Aromatic Pishori rice", price = 220.0, categoryId = riceSubCategory!!.id!!, stockQuantity = 300, imageUrl = "pishori_rice.jpg"))
            productRepository.createProduct(Product(name = "Basmati Rice", description = "Premium Basmati rice", price = 280.0, categoryId = riceSubCategory.id!!, stockQuantity = 150, imageUrl = "basmati_rice.jpg"))
            productRepository.createProduct(Product(name = "Mwea Rice", description = "Local Mwea rice", price = 190.0, categoryId = riceSubCategory.id!!, stockQuantity = 400, imageUrl = "mwea_rice.jpg"))
            productRepository.createProduct(Product(name = "Sindano Rice", description = "Standard Sindano rice", price = 170.0, categoryId = riceSubCategory.id!!, stockQuantity = 350, imageUrl = "sindano_rice.jpg"))
            productRepository.createProduct(Product(name = "Biriani Rice", description = "Special Biriani rice", price = 300.0, categoryId = riceSubCategory.id!!, stockQuantity = 100, imageUrl = "biriani_rice.jpg"))
            productRepository.createProduct(Product(name = "Brown Rice", description = "Healthy Brown rice", price = 250.0, categoryId = riceSubCategory.id!!, stockQuantity = 120, imageUrl = "brown_rice.jpg"))

            // Beans (Sub-category)
            productRepository.createProduct(Product(name = "Dengu (Green Grams)", description = "Fresh green grams", price = 180.0, categoryId = beansSubCategory!!.id!!, stockQuantity = 200, imageUrl = "dengu.jpg"))

            // Spreads
            productRepository.createProduct(Product(name = "Pure Honey", description = "Natural wildflower honey", price = 500.0, categoryId = spreadsCategory!!.id!!, stockQuantity = 80, imageUrl = "pure_honey.jpg"))
            productRepository.createProduct(Product(name = "Stingless Bee Honey", description = "Rare stingless bee honey", price = 1200.0, categoryId = spreadsCategory.id!!, stockQuantity = 30, imageUrl = "stingless_honey.jpg"))

            // Vegetables
            productRepository.createProduct(Product(name = "Kales (Sukuma Wiki)", description = "Fresh kales", price = 50.0, categoryId = vegetablesCategory!!.id!!, stockQuantity = 300, imageUrl = "kales.jpg"))
            productRepository.createProduct(Product(name = "Spinach", description = "Organic spinach", price = 60.0, categoryId = vegetablesCategory.id!!, stockQuantity = 250, imageUrl = "spinach.jpg"))
            productRepository.createProduct(Product(name = "Managu", description = "African nightshade", price = 70.0, categoryId = vegetablesCategory.id!!, stockQuantity = 180, imageUrl = "managu.jpg"))
            productRepository.createProduct(Product(name = "Cabbage", description = "Green cabbage", price = 80.0, categoryId = vegetablesCategory.id!!, stockQuantity = 200, imageUrl = "cabbage.jpg"))
            productRepository.createProduct(Product(name = "Nderma", description = "Jute mallow", price = 65.0, categoryId = vegetablesCategory.id!!, stockQuantity = 150, imageUrl = "nderma.jpg"))
            productRepository.createProduct(Product(name = "Kunde", description = "Cowpeas leaves", price = 75.0, categoryId = vegetablesCategory.id!!, stockQuantity = 160, imageUrl = "kunde.jpg"))
            productRepository.createProduct(Product(name = "Sagga", description = "Spider plant", price = 85.0, categoryId = vegetablesCategory.id!!, stockQuantity = 140, imageUrl = "sagga.jpg"))
            productRepository.createProduct(Product(name = "Mrenda", description = "Jew's mallow", price = 90.0, categoryId = vegetablesCategory.id!!, stockQuantity = 130, imageUrl = "mrenda.jpg"))
            productRepository.createProduct(Product(name = "Tomato", description = "Red ripe tomatoes", price = 100.0, categoryId = vegetablesCategory.id!!, stockQuantity = 400, imageUrl = "tomato.jpg"))
            productRepository.createProduct(Product(name = "Onions", description = "Fresh red onions", price = 70.0, categoryId = vegetablesCategory.id!!, stockQuantity = 350, imageUrl = "onions.jpg"))

            // Fruits
            productRepository.createProduct(Product(name = "Mango", description = "Sweet ripe mangoes", price = 120.0, categoryId = fruitsCategory!!.id!!, stockQuantity = 150, imageUrl = "mango.jpg"))
            productRepository.createProduct(Product(name = "Banana", description = "Yellow ripe bananas", price = 80.0, categoryId = fruitsCategory.id!!, stockQuantity = 200, imageUrl = "banana.jpg"))
            productRepository.createProduct(Product(name = "Avocado", description = "Creamy Hass avocado", price = 90.0, categoryId = fruitsCategory.id!!, stockQuantity = 100, imageUrl = "avocado.jpg"))
            productRepository.createProduct(Product(name = "Oranges", description = "Juicy oranges", price = 110.0, categoryId = fruitsCategory.id!!, stockQuantity = 180, imageUrl = "oranges.jpg"))
            productRepository.createProduct(Product(name = "Apples", description = "Crisp red apples", price = 150.0, categoryId = fruitsCategory.id!!, stockQuantity = 130, imageUrl = "apples.jpg"))
            productRepository.createProduct(Product(name = "Watermelon", description = "Sweet watermelon", price = 200.0, categoryId = fruitsCategory.id!!, stockQuantity = 50, imageUrl = "watermelon.jpg"))
            productRepository.createProduct(Product(name = "Pineapple", description = "Fresh pineapple", price = 180.0, categoryId = fruitsCategory.id!!, stockQuantity = 70, imageUrl = "pineapple.jpg"))
            productRepository.createProduct(Product(name = "Passion Fruit", description = "Tangy passion fruit", price = 130.0, categoryId = fruitsCategory.id!!, stockQuantity = 90, imageUrl = "passion_fruit.jpg"))

            // Spices
            productRepository.createProduct(Product(name = "Turmeric Powder", description = "Organic turmeric powder", price = 90.0, categoryId = spicesCategory!!.id!!, stockQuantity = 100, imageUrl = "turmeric.jpg"))
            productRepository.createProduct(Product(name = "Fresh Ginger", description = "Zesty fresh ginger", price = 70.0, categoryId = spicesCategory.id!!, stockQuantity = 120, imageUrl = "ginger.jpg"))
            productRepository.createProduct(Product(name = "Garlic", description = "Pungent garlic bulbs", price = 60.0, categoryId = spicesCategory.id!!, stockQuantity = 150, imageUrl = "garlic.jpg"))
            productRepository.createProduct(Product(name = "Coriander", description = "Fresh coriander leaves", price = 40.0, categoryId = spicesCategory.id!!, stockQuantity = 200, imageUrl = "coriander.jpg"))
            productRepository.createProduct(Product(name = "Black Pepper", description = "Ground black pepper", price = 110.0, categoryId = spicesCategory.id!!, stockQuantity = 80, imageUrl = "black_pepper.jpg"))
            productRepository.createProduct(Product(name = "Paprika", description = "Smoked paprika powder", price = 95.0, categoryId = spicesCategory.id!!, stockQuantity = 70, imageUrl = "paprika.jpg"))
            productRepository.createProduct(Product(name = "Cumin", description = "Ground cumin powder", price = 80.0, categoryId = spicesCategory.id!!, stockQuantity = 90, imageUrl = "cumin.jpg"))

            // Nuts
            productRepository.createProduct(Product(name = "Groundnuts", description = "Roasted groundnuts", price = 150.0, categoryId = nutsCategory!!.id!!, stockQuantity = 100, imageUrl = "groundnuts.jpg"))
            productRepository.createProduct(Product(name = "Cashewnuts", description = "Premium cashewnuts", price = 300.0, categoryId = nutsCategory.id!!, stockQuantity = 60, imageUrl = "cashewnuts.jpg"))
            productRepository.createProduct(Product(name = "Macadamia Nuts", description = "Local macadamia nuts", price = 400.0, categoryId = nutsCategory.id!!, stockQuantity = 40, imageUrl = "macadamia_nuts.jpg"))

            // Tubers
            productRepository.createProduct(Product(name = "Sweet Potatoes", description = "Orange flesh sweet potatoes", price = 90.0, categoryId = tubersCategory!!.id!!, stockQuantity = 200, imageUrl = "sweet_potatoes.jpg"))
            productRepository.createProduct(Product(name = "Cassava", description = "Fresh cassava root", price = 70.0, categoryId = tubersCategory.id!!, stockQuantity = 150, imageUrl = "cassava.jpg"))
            productRepository.createProduct(Product(name = "Arrow Roots", description = "Nutritious arrow roots", price = 110.0, categoryId = tubersCategory.id!!, stockQuantity = 100, imageUrl = "arrow_roots.jpg"))
            productRepository.createProduct(Product(name = "Carrot", description = "Crunchy carrots", price = 60.0, categoryId = tubersCategory.id!!, stockQuantity = 250, imageUrl = "carrot.jpg"))

                // Others
                productRepository.createProduct(Product(name = "Farm Fresh Eggs (Dozen)", description = "Locally sourced eggs", price = 200.0, categoryId = othersCategory!!.id!!, stockQuantity = 100, imageUrl = "eggs.jpg"))
                productRepository.createProduct(Product(name = "Fresh Milk (1L)", description = "Pasteurized fresh milk", price = 70.0, categoryId = othersCategory.id!!, stockQuantity = 150, imageUrl = "milk.jpg"))
                productRepository.createProduct(Product(name = "Musrik (Fermented Milk)", description = "Traditional fermented milk", price = 120.0, categoryId = othersCategory.id!!, stockQuantity = 50, imageUrl = "muskik.jpg"))

                println("Seeding complete!")
            }
        }
    }
