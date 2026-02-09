package de.honoka.demo.microservice.common.api.auth.data

import cn.hutool.json.JSONArray

data class WebRouteAuthorityData(

    var id: Long? = null,

    var routeName: String? = null,

    var roles: JSONArray? = null
)
