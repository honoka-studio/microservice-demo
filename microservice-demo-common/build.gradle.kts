dependencies {
    compileOnly("org.springframework.boot:spring-boot-starter-web")
    compileOnly("org.springframework.boot:spring-boot-starter-security")
    compileOnly("org.springframework.cloud:spring-cloud-starter-openfeign")
    compileOnly(libs.mybatis.plus.spring.boot.starter)
    compileOnly(libs.honoka.spring.boot.starter)
}
