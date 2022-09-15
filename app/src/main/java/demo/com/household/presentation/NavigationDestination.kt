package demo.com.household.presentation

enum class NavigationDestination(val destination: String) {
    Splash("splashScreen"),
    Login("login"),
    Signup("signup"),
    Home("home"),
    AddProduct("add_product"),
    Products("products/subCategory={subCategory}"),
    Product("product/product={product}"),
    Cart("cart/cart={cartID}"),
    MyOrders("myOrders"),
    Orders("orders"),

    Purchase("purchase"),
    SubCategory("sub_category/categoryID={categoryID}")
}