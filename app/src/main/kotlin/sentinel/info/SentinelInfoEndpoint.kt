package sentinel.info

class SentinelInfoEndpoint(private val base: String) {
    fun healthCheck() = "$base/info/health"
}