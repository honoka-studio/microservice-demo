package de.honoka.demo.microservice.auth.mapper

import com.baomidou.mybatisplus.core.mapper.BaseMapper
import de.honoka.demo.microservice.common.api.auth.entity.WebRouteAuthority
import org.apache.ibatis.annotations.Mapper

@Mapper
interface WebRouteAuthorityMapper : BaseMapper<WebRouteAuthority>
