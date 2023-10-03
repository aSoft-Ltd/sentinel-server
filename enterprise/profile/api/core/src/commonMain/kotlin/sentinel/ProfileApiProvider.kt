package sentinel

import identifier.IdentifierSettings
import koncurrent.Later

interface ProfileApiProvider {
    val profile: ProfileApi

    fun <R> settings(data: R): Later<IdentifierSettings<R>>
    fun settings() = settings(null)
}