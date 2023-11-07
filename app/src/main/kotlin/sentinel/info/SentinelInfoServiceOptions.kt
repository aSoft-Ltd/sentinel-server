package sentinel.info

import grape.MongoService
import kotlinx.coroutines.CoroutineScope

class SentinelInfoServiceOptions(
    val scope: CoroutineScope,
    val database: MongoService
)