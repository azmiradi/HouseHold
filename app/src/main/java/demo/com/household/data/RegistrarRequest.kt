package demo.com.household.data


data class RegistrarRequest(
    val username: String? = null,
    val password: String? = null,
    val mobile: String? = null,
    val email: String? = null,
    val id: String? = null,
    val _accountType: Int? = null
) {
    fun accountType(): AccountType? = AccountType.values().find {
        it.accountType == _accountType
    }
}


enum class AccountType(val accountType: Int) {
    Admin(1), User(2)
}