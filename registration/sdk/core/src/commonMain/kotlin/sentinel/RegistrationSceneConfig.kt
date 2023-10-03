@file:JsExport

package sentinel

import hormone.HasApi
import keep.Cacheable
import lexi.Logable
import kotlin.js.JsExport

interface RegistrationSceneConfig<out A> : HasApi<A>, Logable, Cacheable