# Fix Unresolved Reference for ArrowBack Icon

The project is experiencing "Unresolved reference" errors when using `Icons.AutoMirrored.Filled.ArrowBack`. This appears to be due to a mismatch between the expected receiver for the auto-mirrored icon extension and the library versions in use. Switching to the standard `Icons.Default.ArrowBack` (which refers to the same icon in a non-mirrored but more compatible way) resolves the build failure.

## Proposed Changes

### Admin Module

#### [MODIFY] [AdminCategoriesScreen.kt](file:///C:/Users/ZBOOK%20STUDIO%20NVIDIA/Desktop/km/app/src/main/java/com/example/lyrosmarket/presentation/admin/categories/AdminCategoriesScreen.kt)
- Change `Icons.AutoMirrored.Filled.ArrowBack` to `Icons.Default.ArrowBack`.
- Update import to `androidx.compose.material.icons.filled.ArrowBack`.

#### [MODIFY] [AdminOrdersScreen.kt](file:///C:/Users/ZBOOK%20STUDIO%20NVIDIA/Desktop/km/app/src/main/java/com/example/lyrosmarket/presentation/admin/orders/AdminOrdersScreen.kt)
- Change `Icons.AutoMirrored.Filled.ArrowBack` to `Icons.Default.ArrowBack`.
- Update import to `androidx.compose.material.icons.filled.ArrowBack`.

#### [MODIFY] [AdminProductsScreen.kt](file:///C:/Users/ZBOOK%20STUDIO%20NVIDIA/Desktop/km/app/src/main/java/com/example/lyrosmarket/presentation/admin/products/AdminProductsScreen.kt)
- Change `Icons.AutoMirrored.Filled.ArrowBack` to `Icons.Default.ArrowBack`.
- Update import to `androidx.compose.material.icons.filled.ArrowBack`.

#### [MODIFY] [AdminUsersScreen.kt](file:///C:/Users/ZBOOK%20STUDIO%20NVIDIA/Desktop/km/app/src/main/java/com/example/lyrosmarket/presentation/admin/users/AdminUsersScreen.kt)
- Change `Icons.AutoMirrored.Filled.ArrowBack` to `Icons.Default.ArrowBack`.
- Update import to `androidx.compose.material.icons.filled.ArrowBack`.

## Verification Plan

### Automated Tests
- Run `./gradlew :app:compileDebugKotlin` to verify that the unresolved reference errors are resolved and the project builds successfully.
