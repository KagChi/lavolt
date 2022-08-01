package lavolt.util

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.tools.io.MessageInput
import com.sedmelluq.discord.lavaplayer.tools.io.MessageOutput
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import org.apache.commons.codec.binary.Base64
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException


object Util {
    fun getShardFromSnowflake(snowflake: String, numShards: Int): Int {
        return ((snowflake.toLong() shr 22) % numShards).toInt()
    }

    @Throws(IOException::class)
    fun toAudioTrack(audioPlayerManager: AudioPlayerManager, message: String?): AudioTrack {
        val b64 = Base64.decodeBase64(message)
        val bais = ByteArrayInputStream(b64)
        return audioPlayerManager.decodeTrack(MessageInput(bais)).decodedTrack
    }

    @Throws(IOException::class)
    fun toMessage(audioPlayerManager: AudioPlayerManager, track: AudioTrack?): String {
        val baos = ByteArrayOutputStream()
        audioPlayerManager.encodeTrack(MessageOutput(baos), track)
        return Base64.encodeBase64String(baos.toByteArray())
    }

    fun getRootCause(throwable: Throwable?): Throwable? {
        var rootCause = throwable
        while (rootCause!!.cause != null) {
            rootCause = rootCause.cause
        }
        return rootCause
    }
}