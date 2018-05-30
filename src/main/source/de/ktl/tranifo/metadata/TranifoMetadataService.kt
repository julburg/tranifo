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

    fun getMetadata(): TranifoMetadata? {
        val repository = db.getRepository<TranifoMetadata> {}
        val find = repository.find(TranifoMetadata::name eq "Stop")
        return find.firstOrNull();

    }

    fun save(stopId: String) {

        val metadata = getMetadata()
        if (metadata == null) {
            db.getRepository<TranifoMetadata> { insert(TranifoMetadata("Stop", stopId)) }
        } else {
            val repository = db.getRepository<TranifoMetadata> {}
            repository.update(TranifoMetadata::name eq "Stop", TranifoMetadata("Stop", stopId))
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


data class TranifoMetadata(val name: String, val stopId: String)