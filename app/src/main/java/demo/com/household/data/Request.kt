package demo.com.household.data

class Request(
    private val products: List<String>? = null,
    private val id: String? = null,
    private val totalPrice: String? = null,
    private val _status: Short? = null
) {
    fun status(): RequestStatus? = RequestStatus.values().find {
        it.status == _status
    }
}

enum class RequestStatus(val status: Short) {
    Ready(1), OnWay(2), Delivered(3)
}