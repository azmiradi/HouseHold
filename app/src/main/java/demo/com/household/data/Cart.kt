package demo.com.household.data

data class Cart(
    val products: List<Product>? = null,
    var _status: Int? = null,
    var time: String? = null,
    var id: String? = null,
    var userName: String? = null,
    var userID: String? = null,
    var isDeliver:Boolean =false
) {
    fun status(): RequestStatus? = RequestStatus.values().find {
        it.status == _status
    }
}


enum class RequestStatus(val status: Int) {
    Cart(0), Ready(1), OnWay(2), Delivered(3)
}