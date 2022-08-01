package lavolt.rest

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import lavolt.util.Util
import org.json.JSONArray
import org.json.JSONObject
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import lavolt.config.ServerConfig
import java.io.IOException
import java.util.concurrent.CompletionStage
import javax.servlet.http.HttpServletRequest


@RestController
class AudioLoaderRestHandler(
    private val audioPlayerManager: AudioPlayerManager,
    private val serverConfig: ServerConfig
) {
    private fun log(request: HttpServletRequest) {
        val path = request.servletPath
        log.info("GET $path")
    }

    private fun trackToJSON(audioTrack: AudioTrack): JSONObject {
        val trackInfo = audioTrack.info
        return JSONObject()
            .put("title", trackInfo.title)
            .put("author", trackInfo.author)
            .put("length", trackInfo.length)
            .put("identifier", trackInfo.identifier)
            .put("uri", trackInfo.uri)
            .put("isStream", trackInfo.isStream)
            .put("isSeekable", audioTrack.isSeekable)
            .put("position", audioTrack.position)
            .put("sourceName", if (audioTrack.sourceManager == null) null else audioTrack.sourceManager.sourceName)
    }

    private fun encodeLoadResult(result: LoadResult): JSONObject {
        val json = JSONObject()
        val playlist = JSONObject()
        val tracks = JSONArray()
        result.tracks.forEach { track: AudioTrack ->
            val `object` = JSONObject()
            `object`.put("info", trackToJSON(track))
            try {
                val encoded: String = Util.toMessage(audioPlayerManager, track)
                `object`.put("track", encoded)
                tracks.put(`object`)
            } catch (e: IOException) {
                log.warn(
                    "Failed to encode a track {}, skipping",
                    track.identifier,
                    e
                )
            }
        }
        playlist.put("name", result.playlistName)
        playlist.put("selectedTrack", result.selectedTrack)
        json.put("playlistInfo", playlist)
        json.put("loadType", result.loadResultType)
        json.put("tracks", tracks)
        if (result.loadResultType === ResultStatus.LOAD_FAILED && result.exception != null) {
            val exception = JSONObject()
            exception.put("message", result.exception!!.localizedMessage)
            exception.put("severity", result.exception!!.severity.toString())
            json.put("exception", exception)
            log.error("Track loading failed", result.exception)
        }
        return json
    }

    @GetMapping(value = ["/loadtracks"], produces = ["application/json"])
    @ResponseBody
    fun getLoadTracks(
        request: HttpServletRequest?,
        @RequestParam identifier: String?
    ): CompletionStage<ResponseEntity<String>> {
        log.info("Got request to load for identifier \"{}\"", identifier)
        return AudioLoader(audioPlayerManager).load(identifier)
            .thenApply { result: LoadResult ->
                encodeLoadResult(
                    result
                )
            }
            .thenApply { loadResultJson: JSONObject ->
                ResponseEntity(
                    loadResultJson.toString(),
                    HttpStatus.OK
                )
            }
    }

    @GetMapping(value = ["/decodetrack"], produces = ["application/json"])
    @ResponseBody
    @Throws(IOException::class)
    fun getDecodeTrack(request: HttpServletRequest, @RequestParam track: String?): ResponseEntity<String> {
        log(request)
        val audioTrack: AudioTrack = Util.toAudioTrack(audioPlayerManager, track)
        return ResponseEntity(trackToJSON(audioTrack).toString(), HttpStatus.OK)
    }

    @PostMapping(value = ["/decodetracks"], consumes = ["application/json"], produces = ["application/json"])
    @ResponseBody
    @Throws(
        IOException::class
    )
    fun postDecodeTracks(request: HttpServletRequest, @RequestBody body: String?): ResponseEntity<String> {
        log(request)
        val requestJSON = JSONArray(body)
        val responseJSON = JSONArray()
        for (i in 0 until requestJSON.length()) {
            val track = requestJSON.getString(i)
            val audioTrack: AudioTrack = Util.toAudioTrack(audioPlayerManager, track)
            val infoJSON = trackToJSON(audioTrack)
            val trackJSON = JSONObject()
                .put("track", track)
                .put("info", infoJSON)
            responseJSON.put(trackJSON)
        }
        return ResponseEntity(responseJSON.toString(), HttpStatus.OK)
    }

    companion object {
        private val log = LoggerFactory.getLogger(AudioLoaderRestHandler::class.java)
    }
}
