@file:Suppress("NOTHING_TO_INLINE")

package sentinel

import sentinel.internal.RegistrationScenesImpl

inline fun RegistrationScenes(config: RegistrationSceneConfig<RegistrationApi>): RegistrationScenes = RegistrationScenesImpl(config)