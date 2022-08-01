package lavolt.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@ConfigurationProperties(prefix = "shouko.server")
@Component
class ServerConfig {
    var password: String? = null
    var isYoutubeSearchEnabled = true
    var isSoundcloudSearchEnabled = true
    var ratelimit: RateLimitConfig? = null
    var youtubeConfig: YoutubeConfig? = null
    var youtubePlaylistLoadLimit: Int? = 200

    var httpConfig: HttpConfig? = null
}