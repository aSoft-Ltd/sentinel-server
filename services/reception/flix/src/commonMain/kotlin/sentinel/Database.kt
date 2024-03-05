package sentinel

import com.mongodb.kotlin.client.coroutine.MongoDatabase

class ReceptionDatabase(
    val registration: MongoDatabase,
    val authentication: MongoDatabase,
)