package sentinel.transformers

import krono.Clock
import krono.currentJavaLocalDateTime
import sentinel.RegistrationCandidateDao
import sentinel.params.SignUpParams

fun SignUpParams.toDao(clock: Clock) = RegistrationCandidateDao(
    name = name,
    email = email,
    on = clock.currentJavaLocalDateTime(),
    tokens = emptyList(),
    verified = false
)