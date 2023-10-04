package sentinel.transformers

import krono.Clock
import sentinel.SignUpCandidate
import sentinel.params.SignUpParams

fun SignUpParams.toDao(clock: Clock) = SignUpCandidate(
    name = name,
    email = email,
    on = clock.currentMillisAsLong(),
    sent = emptyList(),
    verified = false
)