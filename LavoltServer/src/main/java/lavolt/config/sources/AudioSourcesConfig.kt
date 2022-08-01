package lavolt.config.sources

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@ConfigurationProperties(prefix = "lavolt.server.sources")
@Component
class AudioSourcesConfig {
    private var youtube = true
    private var bandcamp = true
    private var soundcloud = true
    private var twitch = true
    private var vimeo = true
    private var mixer = true
    private var http = true
    private var local = false
    fun isYoutube(): Boolean {
        return youtube
    }

    fun setYoutube(youtube: Boolean) {
        this.youtube = youtube
    }

    fun isBandcamp(): Boolean {
        return bandcamp
    }

    fun setBandcamp(bandcamp: Boolean) {
        this.bandcamp = bandcamp
    }

    fun isSoundcloud(): Boolean {
        return soundcloud
    }

    fun setSoundcloud(soundcloud: Boolean) {
        this.soundcloud = soundcloud
    }

    fun isTwitch(): Boolean {
        return twitch
    }

    fun setTwitch(twitch: Boolean) {
        this.twitch = twitch
    }

    fun isVimeo(): Boolean {
        return vimeo
    }

    fun setVimeo(vimeo: Boolean) {
        this.vimeo = vimeo
    }

    fun isMixer(): Boolean {
        return mixer
    }

    fun setMixer(mixer: Boolean) {
        this.mixer = mixer
    }

    fun isHttp(): Boolean {
        return http
    }

    fun setHttp(http: Boolean) {
        this.http = http
    }

    fun isLocal(): Boolean {
        return local
    }

    fun setLocal(local: Boolean) {
        this.local = local
    }
}