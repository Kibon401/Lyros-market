-- SQL script for Supabase (PostgreSQL) database

-- Drop tables in reverse order of creation to avoid foreign key constraints
DROP TABLE IF EXISTS user_review;
DROP TABLE IF EXISTS shopping_cart_item;
DROP TABLE IF EXISTS order_line;
DROP TABLE IF EXISTS mpesa_payment;
DROP TABLE IF EXISTS driver_location;
DROP TABLE IF EXISTS shop_order;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS product_category;
DROP TABLE IF EXISTS site_user;

-- Create tables and insert data

--
-- Table structure for table "site_user"
--
CREATE TABLE site_user (
  id SERIAL PRIMARY KEY,
  username varchar(50) NOT NULL UNIQUE,
  email varchar(100) NOT NULL UNIQUE,
  password_hash varchar(255) NOT NULL,
  "role" varchar(20) NOT NULL DEFAULT 'USER',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  is_active BOOLEAN NOT NULL DEFAULT TRUE,
  reset_code varchar(10) DEFAULT NULL,
  reset_expiry TIMESTAMP DEFAULT NULL
);

--
-- Dumping data for table "site_user"
--
INSERT INTO site_user (id, username, email, password_hash, "role", created_at, is_active, reset_code, reset_expiry) VALUES
(1, 'Domi', 'kibondominic18@gmail.com', '$2a$10$pKWS4YJ1lDVj1RBHUObSC.HOUQWiYHN9sj6w/63YizvKmqtW3xrha', 'USER', '2026-07-14 15:15:06.222445', TRUE, '379180', '2026-07-14 13:05:05.705823'),
(2, 'tester_updated', 'test@example.com', '$2a$10$Zo9VJ5WSs2gCzZJnnc60u./uSEhRou8ZGhsaBuDiNPkD1Sg7Seqoa', 'USER', '2026-07-16 13:14:47.103883', TRUE, NULL, NULL),
(3, 'john', 'jnyangara303@gmail.com', '$2a$10$maNOEoUSS1JzCZn.OEyUmeadSrTIK7W8EAWLzvpBUk8NRpuF4lPMa', 'USER', '2026-07-16 13:28:34.303842', TRUE, NULL, NULL);

--
-- Table structure for table "product_category"
--
CREATE TABLE product_category (
  id SERIAL PRIMARY KEY,
  name varchar(50) NOT NULL,
  description varchar(255) DEFAULT NULL,
  parent_id int DEFAULT NULL,
  CONSTRAINT fk_product_category_parent_id__id FOREIGN KEY (parent_id) REFERENCES product_category (id) ON DELETE RESTRICT ON UPDATE RESTRICT
);

--
-- Dumping data for table "product_category"
--
INSERT INTO product_category (id, name, description, parent_id) VALUES
(1, 'Seeds', 'Various healthy seeds', NULL),
(2, 'Cereals', 'Grains and legumes', NULL),
(3, 'Spreads', 'Natural spreads and sweeteners', NULL),
(4, 'Vegetables', 'Fresh farm vegetables', NULL),
(5, 'Fruits', 'Fresh seasonal fruits', NULL),
(6, 'Spices', 'Aromatic herbs and spices', NULL),
(7, 'Nuts', 'Healthy and crunchy nuts', NULL),
(8, 'Tubers', 'Root vegetables', NULL),
(9, 'Others', 'Miscellaneous farm products', NULL),
(10, 'Rice', 'Different varieties of rice', 2),
(11, 'Beans', 'Various types of beans', 2);

--
-- Table structure for table "product"
--
CREATE TABLE product (
  id SERIAL PRIMARY KEY,
  name varchar(100) NOT NULL,
  description TEXT NOT NULL,
  price double precision NOT NULL,
  category_id int NOT NULL,
  stock_quantity int NOT NULL,
  image_url varchar(255) DEFAULT NULL,
  is_high_demand BOOLEAN NOT NULL DEFAULT FALSE,
  CONSTRAINT fk_product_category_id__id FOREIGN KEY (category_id) REFERENCES product_category (id) ON DELETE RESTRICT ON UPDATE RESTRICT
);

--
-- Dumping data for table "product"
--
INSERT INTO product (id, name, description, price, category_id, stock_quantity, image_url, is_high_demand) VALUES
(1, 'Pumpkin Seeds', 'Nutrient-rich pumpkin seeds', 150, 1, 100, 'pumpkin_seeds.jpg', FALSE),
(2, 'Watermelon Seeds', 'Healthy watermelon seeds', 120, 1, 80, 'watermelon_seeds.jpg', FALSE),
(3, 'Sesame (Simsim) Seeds', 'Toasted sesame seeds', 180, 1, 150, 'sesame_seeds.jpg', FALSE),
(4, 'Sunflower Seeds', 'Crunchy sunflower seeds', 130, 1, 120, 'sunflower_seeds.jpg', FALSE),
(5, 'Chia Seeds', 'High-fiber chia seeds', 250, 1, 200, 'chia_seeds.jpg', TRUE),
(6, 'Maize Flour', 'Fine maize flour', 100, 2, 500, 'maize_flour.jpg', FALSE),
(7, 'Millet Flour', 'Nutritious millet flour', 160, 2, 300, 'millet_flour.jpg', FALSE),
(8, 'Sorghum Flour', 'Healthy sorghum flour', 140, 2, 250, 'sorghum_flour.jpg', FALSE),
(9, 'Kamande', 'Green grams (Kamande)', 200, 2, 180, 'kamande.jpg', FALSE),
(10, 'Lentils', 'Brown lentils', 190, 2, 170, 'lentils.jpg', FALSE),
(11, 'Pishori Rice', 'Aromatic Pishori rice', 220, 10, 300, 'pishori_rice.jpg', FALSE),
(12, 'Basmati Rice', 'Premium Basmati rice', 280, 10, 150, 'basmati_rice.jpg', FALSE),
(13, 'Mwea Rice', 'Local Mwea rice', 190, 10, 400, 'mwea_rice.jpg', FALSE),
(14, 'Sindano Rice', 'Standard Sindano rice', 170, 10, 350, 'sindano_rice.jpg', FALSE),
(15, 'Biriani Rice', 'Special Biriani rice', 300, 10, 100, 'biriani_rice.jpg', FALSE),
(16, 'Brown Rice', 'Healthy Brown rice', 250, 10, 120, 'brown_rice.jpg', FALSE),
(17, 'Dengu (Green Grams)', 'Fresh green grams', 180, 11, 200, 'dengu.jpg', FALSE),
(18, 'Pure Honey', 'Natural wildflower honey', 500, 3, 80, 'pure_honey.jpg', FALSE),
(19, 'Stingless Bee Honey', 'Rare stingless bee honey', 1200, 3, 30, 'stingless_honey.jpg', FALSE),
(20, 'Kales (Sukuma Wiki)', 'Fresh kales', 50, 4, 300, 'kales.jpg', FALSE),
(21, 'Spinach', 'Organic spinach', 60, 4, 250, 'spinach.jpg', FALSE),
(22, 'Managu', 'African nightshade', 70, 4, 180, 'managu.jpg', FALSE),
(23, 'Cabbage', 'Green cabbage', 80, 4, 200, 'cabbage.jpg', FALSE),
(24, 'Nderma', 'Jute mallow', 65, 4, 150, 'nderma.jpg', FALSE),
(25, 'Kunde', 'Cowpeas leaves', 75, 4, 160, 'kunde.jpg', FALSE),
(26, 'Sagga', 'Spider plant', 85, 4, 140, 'sagga.jpg', FALSE),
(27, 'Mrenda', 'Jew''s mallow', 90, 4, 130, 'mrenda.jpg', FALSE),
(28, 'Tomato', 'Red ripe tomatoes', 100, 4, 400, 'tomato.jpg', FALSE),
(29, 'Onions', 'Fresh red onions', 70, 4, 350, 'onions.jpg', FALSE),
(30, 'Mango', 'Sweet ripe mangoes', 120, 5, 150, 'mango.jpg', FALSE),
(31, 'Banana', 'Yellow ripe bananas', 80, 5, 200, 'banana.jpg', FALSE),
(32, 'Avocado', 'Creamy Hass avocado', 90, 5, 100, 'avocado.jpg', FALSE),
(33, 'Oranges', 'Juicy oranges', 110, 5, 180, 'oranges.jpg', FALSE),
(34, 'Apples', 'Crisp red apples', 150, 5, 130, 'apples.jpg', FALSE),
(35, 'Watermelon', 'Sweet watermelon', 200, 5, 50, 'watermelon.jpg', FALSE),
(36, 'Pineapple', 'Fresh pineapple', 180, 5, 70, 'pineapple.jpg', FALSE),
(37, 'Passion Fruit', 'Tangy passion fruit', 130, 5, 90, 'passion_fruit.jpg', FALSE),
(38, 'Turmeric Powder', 'Organic turmeric powder', 90, 6, 100, 'turmeric.jpg', FALSE),
(39, 'Fresh Ginger', 'Zesty fresh ginger', 70, 6, 120, 'ginger.jpg', FALSE),
(40, 'Garlic', 'Pungent garlic bulbs', 60, 6, 150, 'garlic.jpg', FALSE),
(41, 'Coriander', 'Fresh coriander leaves', 40, 6, 200, 'coriander.jpg', FALSE),
(42, 'Black Pepper', 'Ground black pepper', 110, 6, 80, 'black_pepper.jpg', FALSE),
(43, 'Paprika', 'Smoked paprika powder', 95, 6, 70, 'paprika.jpg', FALSE),
(44, 'Cumin', 'Ground cumin powder', 80, 6, 90, 'cumin.jpg', FALSE),
(45, 'Groundnuts', 'Roasted groundnuts', 150, 7, 100, 'groundnuts.jpg', FALSE),
(46, 'Cashewnuts', 'Premium cashewnuts', 300, 7, 60, 'cashewnuts.jpg', FALSE),
(47, 'Macadamia Nuts', 'Local macadamia nuts', 400, 7, 40, 'macadamia_nuts.jpg', FALSE),
(48, 'Sweet Potatoes', 'Orange flesh sweet potatoes', 90, 8, 200, 'sweet_potatoes.jpg', FALSE),
(49, 'Cassava', 'Fresh cassava root', 70, 8, 150, 'cassava.jpg', FALSE),
(50, 'Arrow Roots', 'Nutritious arrow roots', 110, 8, 100, 'arrow_roots.jpg', FALSE),
(51, 'Carrot', 'Crunchy carrots', 60, 8, 250, 'carrot.jpg', FALSE),
(52, 'Farm Fresh Eggs (Dozen)', 'Locally sourced eggs', 200, 9, 100, 'eggs.jpg', FALSE),
(53, 'Fresh Milk (1L)', 'Pasteurized fresh milk', 70, 9, 150, 'milk.jpg', FALSE),
(54, 'Muskik (Fermented Milk)', 'Traditional fermented milk', 120, 9, 50, 'muskik.jpg', FALSE);

--
-- Table structure for table "shop_order"
--
CREATE TABLE shop_order (
  id SERIAL PRIMARY KEY,
  user_id int NOT NULL,
  driver_id int DEFAULT NULL,
  order_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  subtotal double precision NOT NULL,
  delivery_fee double precision NOT NULL DEFAULT 0,
  total_amount double precision NOT NULL,
  status varchar(20) NOT NULL DEFAULT 'PENDING',
  delivery_address TEXT NOT NULL,
  latitude double precision DEFAULT NULL,
  longitude double precision DEFAULT NULL,
  CONSTRAINT fk_shop_order_driver_id__id FOREIGN KEY (driver_id) REFERENCES site_user (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT fk_shop_order_user_id__id FOREIGN KEY (user_id) REFERENCES site_user (id) ON DELETE RESTRICT ON UPDATE RESTRICT
);

--
-- Table structure for table "driver_location"
--
CREATE TABLE driver_location (
  id SERIAL PRIMARY KEY,
  driver_id int NOT NULL,
  latitude double precision NOT NULL,
  longitude double precision NOT NULL,
  "timestamp" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_driver_location_driver_id__id FOREIGN KEY (driver_id) REFERENCES site_user (id) ON DELETE RESTRICT ON UPDATE RESTRICT
);

--
-- Table structure for table "mpesa_payment"
--
CREATE TABLE mpesa_payment (
  id SERIAL PRIMARY KEY,
  order_id int NOT NULL,
  merchant_request_id varchar(100) NOT NULL UNIQUE,
  checkout_request_id varchar(100) NOT NULL UNIQUE,
  amount double precision NOT NULL,
  phone_number varchar(15) NOT NULL,
  mpesa_receipt_number varchar(50) DEFAULT NULL,
  transaction_date TIMESTAMP DEFAULT NULL,
  status varchar(20) NOT NULL DEFAULT 'PENDING',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_mpesa_payment_order_id__id FOREIGN KEY (order_id) REFERENCES shop_order (id) ON DELETE RESTRICT ON UPDATE RESTRICT
);

--
-- Table structure for table "order_line"
--
CREATE TABLE order_line (
  id SERIAL PRIMARY KEY,
  order_id int NOT NULL,
  product_id int NOT NULL,
  quantity int NOT NULL,
  price double precision NOT NULL,
  CONSTRAINT fk_order_line_order_id__id FOREIGN KEY (order_id) REFERENCES shop_order (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT fk_order_line_product_id__id FOREIGN KEY (product_id) REFERENCES product (id) ON DELETE RESTRICT ON UPDATE RESTRICT
);

--
-- Table structure for table "shopping_cart_item"
--
CREATE TABLE shopping_cart_item (
  id SERIAL PRIMARY KEY,
  user_id int NOT NULL,
  product_id int NOT NULL,
  quantity int NOT NULL,
  CONSTRAINT fk_shopping_cart_item_product_id__id FOREIGN KEY (product_id) REFERENCES product (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT fk_shopping_cart_item_user_id__id FOREIGN KEY (user_id) REFERENCES site_user (id) ON DELETE RESTRICT ON UPDATE RESTRICT
);

--
-- Table structure for table "user_review"
--
CREATE TABLE user_review (
  id SERIAL PRIMARY KEY,
  user_id int NOT NULL,
  product_id int NOT NULL,
  rating int NOT NULL,
  comment TEXT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_user_review_product_id__id FOREIGN KEY (product_id) REFERENCES product (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT fk_user_review_user_id__id FOREIGN KEY (user_id) REFERENCES site_user (id) ON DELETE RESTRICT ON UPDATE RESTRICT
);

-- Reset sequences after bulk insert
SELECT setval('site_user_id_seq', (SELECT MAX(id) FROM site_user));
SELECT setval('product_category_id_seq', (SELECT MAX(id) FROM product_category));
SELECT setval('product_id_seq', (SELECT MAX(id) FROM product));

