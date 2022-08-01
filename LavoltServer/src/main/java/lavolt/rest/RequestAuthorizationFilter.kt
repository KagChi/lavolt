package lavolt.rest

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import lavolt.config.ServerConfig
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Configuration
class RequestAuthorizationFilter(
    private val serverConfig: ServerConfig
) :
    HandlerInterceptor, WebMvcConfigurer {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (request.servletPath == "/error") return true
        val authorization = request.getHeader("Authorization")
        if (authorization == null || authorization != serverConfig.password) {
            val method = request.method
            val path = request.requestURI.substring(request.contextPath.length)
            val ip = request.remoteAddr
            if (authorization == null) {
                log.warn("Authorization missing for {} on {} {}", ip, method, path)
                response.status = HttpStatus.UNAUTHORIZED.value()
                return false
            }
            log.warn("Authorization failed for {} on {} {}", ip, method, path)
            response.status = HttpStatus.FORBIDDEN.value()
            return false
        }
        return true
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(this)
    }

    companion object {
        private val log = LoggerFactory.getLogger(RequestAuthorizationFilter::class.java)
    }
}