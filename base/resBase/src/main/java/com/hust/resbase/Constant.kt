package com.hust.resbase

object Constant {
    const val IS_LOGIN = "isLogin"
    const val LOGIN_TOKEN = "login_token"
    const val CURRENT_USER_ID = "current_user_id"
    const val CURRENT_USER_NICKNAME = "current_user_name"
    const val CURRENT_USER_PICPATH = "current_user_pic_path"
}

object RouteConfig {
    const val LOGIN_PAGE = "login_page"
    const val SIGN_PAGE = "sign_page"
}

object ParamsConfig {
    const val PARAMS_USER_NAME = "username"
}

object TimeSpan {
    const val SHORT = 200L
    const val MEDIUM = 500L
    const val LONG = 800L
    const val VERY_LONG = 1000L
}

object NetworkConstant {

    object SortMode {
        const val LATEST_REPLY = "LATEST_REPLY"
        const val LATEST = "LATEST"
        const val REC = "REC"
        const val ASC = "ASC"
    }
    const val CONSTANT_STANDARD_LOAD_SIZE = 20
}

object ArouterConfig {
    const val ACTIVITY_START = "/app/StartActivity"
    const val ACTIVITY_LAR = "/lar/LARActivity"
    const val ACTIVITY_HOME = "/homepage/HomePageActivity"
}

enum class PlaceholderType {
    PLACEHOLDER_NO_CONTENT,
    PLACEHOLDER_NETWORK_ERROR
}

enum class ApiStatus {
    SUCCESSFUL,
    LOADING,
    ERROR
}

sealed class ApiResult {
    data class Success<T>(
        val status: ApiStatus = ApiStatus.SUCCESSFUL,
        var data: T? = null
    ) : ApiResult()

    data class Error(
        val status: ApiStatus = ApiStatus.ERROR,
        val code: Int,
        val errorMessage: String? = null
    ) : ApiResult()

    data class Loading(
        val status: ApiStatus = ApiStatus.LOADING
    ) : ApiResult()
}
