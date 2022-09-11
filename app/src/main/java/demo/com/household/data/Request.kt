package demo.com.household.data

class Request(
      val products: List<String>? = null,
      val id: String? = null,
      val totalPrice: String? = null,
      val _status: Short? = null
) {
    fun status(): RequestStatus? = RequestStatus.values().find {
        it.status == _status
    }
}

enum class RequestStatus(val status: Short) {
    Ready(1), OnWay(2), Delivered(3)
}

