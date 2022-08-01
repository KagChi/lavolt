package lavolt.rest

import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import org.jetbrains.annotations.Nullable
import java.util.*


class LoadResult {
    var loadResultType: ResultStatus
    var tracks: List<AudioTrack>
    var playlistName: String?
    var selectedTrack: Int?
    var exception: FriendlyException?

    constructor(
        loadResultType: ResultStatus, tracks: List<AudioTrack>?,
        @Nullable playlistName: String?, @Nullable selectedTrack: Int?
    ) {
        this.loadResultType = loadResultType
        this.tracks = Collections.unmodifiableList(tracks)
        this.playlistName = playlistName
        this.selectedTrack = selectedTrack
        exception = null
    }

    constructor(exception: FriendlyException?) {
        loadResultType = ResultStatus.LOAD_FAILED
        tracks = emptyList()
        playlistName = null
        selectedTrack = null
        this.exception = exception
    }
}