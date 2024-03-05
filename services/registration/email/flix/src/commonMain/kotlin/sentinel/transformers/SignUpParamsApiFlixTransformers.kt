package sentinel.transformers

import krono.Clock
import krono.currentJavaLocalDateTime
import org.bson.types.ObjectId
import sentinel.EmailRegistrationCandidateDao
import sentinel.params.EmailSignUpParams

fun EmailSignUpParams.toDao(clock: Clock, scope: ObjectId?) = EmailRegistrationCandidateDao(
    name = name,
    email = email,
    on = clock.currentJavaLocalDateTime(),
    tokens = emptyList(),
    scope = scope,
    verified = false
)