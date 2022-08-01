/*
 * Copyright (c) 2022 KagChi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package lavolt

import com.sedmelluq.discord.lavaplayer.tools.PlayerLibrary
import org.slf4j.LoggerFactory
import org.springframework.boot.Banner
import org.springframework.boot.SpringApplication
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent
import org.springframework.boot.context.event.ApplicationFailedEvent
import org.springframework.context.ApplicationListener

@SpringBootApplication
class ShoukoApplication

object Main {

    private val log = LoggerFactory.getLogger(Main::class.java)

    private fun getVersionInfo(indentation: String = "\t", vanity: Boolean = true): String {
        return buildString {
            if (vanity) {
                appendln()
                appendln()
                appendln(getVanity())
            }
            appendln()
            append("${indentation}JVM:            "); appendln(System.getProperty("java.version"))
            append("${indentation}Lavaplayer      "); appendln(PlayerLibrary.VERSION)
        }
    }

    private fun getVanity(): String {
        return ",--.                                ,--.   ,--.   \n" +
                "|  |     ,--,--. ,--.  ,--.  ,---.  |  | ,-'  '-. \n" +
                "|  |    ' ,-.  |  \\  `'  /  | .-. | |  | '-.  .-' \n" +
                "|  '--. \\ '-'  |   \\    /   ' '-' ' |  |   |  |   \n" +
                "`-----'  `--`--'    `--'     `---'  `--'   `--'   "
    }

    val startTime = System.currentTimeMillis()

    @JvmStatic
    fun main(args: Array<String>) {

        val sa = SpringApplication(ShoukoApplication::class.java)
        sa.webApplicationType = WebApplicationType.SERVLET
        sa.setBannerMode(Banner.Mode.OFF)
        sa.addListeners(
                ApplicationListener { event: Any ->
                    if (event is ApplicationEnvironmentPreparedEvent) {
                        log.info(getVersionInfo())
                    }
                    if (event is ApplicationFailedEvent) {
                        log.error("Application failed", event.exception)
                    }
                }
        )
        sa.run(*args)
    }
}
