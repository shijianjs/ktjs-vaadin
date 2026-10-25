package demo.e2e

import cn.hutool.http.server.SimpleServer
import java.io.File
import java.nio.file.Path

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
        val started = SimpleServer(0).setRoot(root)
        started.start()
        Runtime.getRuntime().addShutdownHook(Thread { started.rawServer.stop(0) })
        started
    }

    val baseUrl: String
        get() = "http://127.0.0.1:${server.address.port}/"
}
