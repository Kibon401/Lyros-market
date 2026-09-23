import sys
path = r'c:\Users\ZBOOK STUDIO NVIDIA\Desktop\Lyros-market\Lyros-market\frontend\app\src\main\java\com\lyrosmarket\app\presentation\Navigation.kt'
with open(path, 'r', encoding='utf-8') as f:
    content = f.read()

target = """            CartScreen(
                viewModel = cartViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToCheckout = {
                    navController.navigate(Screen.Checkout.route)
                }
            )"""

replacement = """            CartScreen(
                viewModel = cartViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToCheckout = {
                    navController.navigate(Screen.Checkout.route)
                },
                onNavigateToMap = {
                    navController.navigate(Screen.MapAddressPicker.route)
                }
            )"""

# Normalize line endings
target_normalized = target.replace('\r\n', '\n')
content_normalized = content.replace('\r\n', '\n')

if target_normalized in content_normalized:
    new_content = content_normalized.replace(target_normalized, replacement)
    with open(path, 'w', encoding='utf-8') as f:
        f.write(new_content)
    print('Success')
else:
    print('Target not found.')
