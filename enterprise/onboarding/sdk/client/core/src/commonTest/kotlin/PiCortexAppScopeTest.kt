import bringer.MockDownloader
import keep.CacheMock
import keep.CacheMockConfig
import koncurrent.SynchronousExecutor
import kotlinx.serialization.json.Json
import krest.VoidWorkManager
import lexi.ConsoleAppender
import lexi.Logger
import picortex.PiCortexApiTest
import picortex.PiCortexAppScenes
import picortex.PiCortexAppScopeConfig

fun PiCortexAppScopeTest(): PiCortexAppScenes {
    val uiExecutor = SynchronousExecutor
    val codec = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    val config = PiCortexAppScopeConfig(
        api = PiCortexApiTest(),
        executor = uiExecutor,
        logger = Logger(ConsoleAppender()),
        cache = CacheMock(CacheMockConfig(namespace = "picortex.test.ui")),
        downloader = MockDownloader(uiExecutor),
        codec = codec,
        workManager = VoidWorkManager
    )

    return PiCortexAppScenes(config)
}