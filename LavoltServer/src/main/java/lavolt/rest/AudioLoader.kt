package lavolt.rest

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.atomic.AtomicBoolean

class AudioLoader(private val audioPlayerManager: AudioPlayerManager) :
    AudioLoadResultHandler {
    private val loadResult: CompletableFuture<LoadResult> = CompletableFuture<LoadResult>()
    private val used = AtomicBoolean(false)
    fun load(identifier: String?): CompletionStage<LoadResult> {
        val isUsed = used.getAndSet(true)
        check(!isUsed) { "This loader can only be used once per instance" }
        log.trace("Loading item with identifier {}", identifier)
        audioPlayerManager.loadItem(identifier, this)
        return loadResult
    }

    override fun trackLoaded(audioTrack: AudioTrack) {
        log.info("Loaded track " + audioTrack.info.title)
        val result = ArrayList<AudioTrack>()
        result.add(audioTrack)
        loadResult.complete(LoadResult(ResultStatus.TRACK_LOADED, result, null, null))
    }

    override fun playlistLoaded(audioPlaylist: AudioPlaylist) {
        log.info("Loaded playlist " + audioPlaylist.name)
        var playlistName: String? = null
        var selectedTrack: Int? = null
        if (!audioPlaylist.isSearchResult) {
            playlistName = audioPlaylist.name
            selectedTrack = audioPlaylist.tracks.indexOf(audioPlaylist.selectedTrack)
        }
        val status: ResultStatus =
            if (audioPlaylist.isSearchResult) ResultStatus.SEARCH_RESULT else ResultStatus.PLAYLIST_LOADED
        val loadedItems = audioPlaylist.tracks
        loadResult.complete(LoadResult(status, loadedItems, playlistName, selectedTrack))
    }

    override fun noMatches() {
        log.info("No matches found")
        loadResult.complete(NO_MATCHES)
    }

    override fun loadFailed(e: FriendlyException) {
        log.error("Load failed", e)
        loadResult.complete(LoadResult(e))
    }

    companion object {
        private val log = LoggerFactory.getLogger(AudioLoader::class.java)
        private val NO_MATCHES: LoadResult = LoadResult(
            ResultStatus.NO_MATCHES, emptyList(),
            null, null
        )
    }
}