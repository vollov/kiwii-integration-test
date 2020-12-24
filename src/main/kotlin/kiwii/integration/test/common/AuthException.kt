package kiwii.integration.test.common

class AuthException (errorCode: String) : Exception() {
    val errorCode: String = errorCode
}
