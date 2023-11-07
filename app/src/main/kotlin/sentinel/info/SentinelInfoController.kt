package sentinel.info

import kotlinx.serialization.StringFormat

class SentinelInfoController(
    val service: SentinelInfoService,
    val endpoint: SentinelInfoEndpoint,
    val codec: StringFormat
)