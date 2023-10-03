@file:Suppress("NOTHING_TO_INLINE")

package sentinel.tools

import keep.Cache
import sentinel.params.SignUpParams
import sentinel.params.VerificationParams

@PublishedApi
internal const val KEY_SIGN_UP_PARAMS = "sentinel.registration.sign.up.params"

// --------------------------Sign Up Params --------------------------
inline fun Cache.save(params: SignUpParams) = save(KEY_SIGN_UP_PARAMS, params, SignUpParams.serializer())

inline fun Cache.loadSignUpParams() = load(KEY_SIGN_UP_PARAMS, SignUpParams.serializer())

inline fun Cache.removeSignUpParams() = remove(KEY_SIGN_UP_PARAMS)