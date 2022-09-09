package demo.com.household.data

data class Product(
    private val name: String? = null,
    private val price: Int? = null,
    private val description: String? = null,
    var images: List<String>? = null,
    private val categoryID:String?=null,
    private val subCategoryID:String?=null,
    var productID:String?=null

)