package demo.com.household.data

data class Product(
    val name: String? = null,
    val price: Int? = null,
    val description: String? = null,
    var images: List<String>? = null,
    val categoryID: String? = null,
    val subCategoryID: String? = null,
    var productID: String? = null

)