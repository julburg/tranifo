package de.ktl.tranifo.metadata

import org.dizitart.kno2.filters.eq
import org.dizitart.kno2.getRepository
import org.dizitart.kno2.nitrite
import org.dizitart.no2.Nitrite
import java.io.File

/**
 * @author  Julia Burgard - burgard@synyx.de
 */
class TranifoMetadataService {

    private var db: Nitrite

    init {
        this.db = getDb()
    }

    fun getStopConfig(): TranifoStopConfig? {
        val repository = db.getRepository<TranifoStopConfig> {}
        val find = repository.find(TranifoStopConfig::name eq "Stop")
        return find.firstOrNull();

    }

    fun getNotificationConfig(): TranifoNotificationConfig? {
        val repository = db.getRepository<TranifoNotificationConfig> {}
        val find = repository.find(TranifoStopConfig::name eq "Notification")
        return find.firstOrNull();

    }

    fun save(stopId: String, route: String, destination: String) {

        val stopConfig = getStopConfig()
        if (stopConfig == null) {
            db.getRepository<TranifoStopConfig> { insert(TranifoStopConfig("Stop", stopId, route, destination)) }
        } else {
            val repository = db.getRepository<TranifoStopConfig> {}
            repository.update(TranifoStopConfig::name eq "Stop", TranifoStopConfig("Stop", stopId, route, destination))
        }
    }

    fun save(hour: Int, intervalMinutes: Int) {
        val notificationConfig = getNotificationConfig()
        if (notificationConfig == null) {
            db.getRepository<TranifoNotificationConfig> { insert(TranifoNotificationConfig("Notification", hour, intervalMinutes)) }
        } else {
            val repository = db.getRepository<TranifoNotificationConfig> {}
            repository.update(TranifoStopConfig::name eq "Notification", TranifoNotificationConfig("Notification", hour, intervalMinutes))
        }
    }

    private fun getDb(): Nitrite {
        val db = nitrite {
            file = File("tranifo")       // or, path = fileName
            autoCommitBufferSize = 2048
            compress = true
            autoCompact = false
        }
        return db
    }

}


data class TranifoStopConfig(val name: String, val stopId: String, val route: String, val destination: String)
data class TranifoNotificationConfig(val name: String, val hour: Int, val intervalMinutes: Int)