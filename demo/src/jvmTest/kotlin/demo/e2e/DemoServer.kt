package demo.e2e

import cn.hutool.http.server.SimpleServer
import java.io.File
import java.nio.file.Path
import kotlin.random.Random

/**
 * Serves the production demo bundle for the suite. Hutool's `SimpleServer` uses
 * `com.sun.net.httpserver` and `RootAction` maps request paths to files under the root directory,
 * so no hand-written handler is needed. Hash routes need no SPA fallback.
 */
object DemoServer {
    private val root: File = File(
        System.getProperty("demo.dist")
            ?: Path.of("build", "dist", "js", "productionExecutable").toString(),
    ).canonicalFile

    private val server: SimpleServer by lazy {
        val started = startOnSafePort()
        Runtime.getRuntime().addShutdownHook(Thread { started.rawServer.stop(0) })
        started
    }

    /**
     * A port Chromium accepts: `SimpleServer(0)` asks the OS for any free port, and the OS may hand
     * out one from the blocked range (2049, 6000, 6665–6669, …), where Chrome answers
     * `ERR_UNSAFE_PORT`. Browsers never block the 8100–8999 range, so the server picks from there
     * and retries when the port is already taken.
     */
    private fun startOnSafePort(): SimpleServer {
        repeat(20) {
            val port = 8100 + Random.nextInt(800)
            try {
                return SimpleServer(port).setRoot(root).apply { start() }
            } catch (taken: Exception) {
                // Port already in use: try the next one.
            }
        }
        error("no free port in 8100-8999 for the demo server")
    }

    val baseUrl: String
        get() = "http://127.0.0.1:${server.address.port}/"
}
