package sentinel.transformers

import krono.Clock
import krono.currentJavaLocalDateTime
import sentinel.EmailRegistrationCandidateDao
import sentinel.params.EmailSignUpParams

fun EmailSignUpParams.toDao(clock: Clock) = EmailRegistrationCandidateDao(
    name = name,
    email = email,
    on = clock.currentJavaLocalDateTime(),
    tokens = emptyList(),
    verified = false
)