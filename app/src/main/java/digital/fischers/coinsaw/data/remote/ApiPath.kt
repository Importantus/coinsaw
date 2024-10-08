package digital.fischers.coinsaw.data.remote

enum class ApiPath(val path: String) {
    CREATE_GROUP("/v1/group"),
    DELETE_GROUP("/v1/group"),
    CREATE_SESSION("/v1/sessions"),
    GET_ALL_SESSIONS("/v1/sessions"),
    DELETE_SESSION("/v1/sessions"),
    CREATE_SHARE("/v1/share"),
    GET_ALL_SHARES("/v1/share"),
    GET_SHARE("/v1/share"),
    DELETE_SHARE("/v1/share"),
    GET_ENTRIES("/v1/data"),
    POST_ENTRIES("/v1/data"),
    SYNC_ENTRIES("/v1/data/sync")
}