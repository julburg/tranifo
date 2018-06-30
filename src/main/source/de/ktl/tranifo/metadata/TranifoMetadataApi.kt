package de.ktl.tranifo.metadata

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import de.ktl.tranifo.NotficationConfig
import de.ktl.tranifo.StopIdPayload
import spark.Spark.get
import spark.Spark.post

/**
 * @author  Julia Burgard - burgard@synyx.de
 */

class TranifoMetadataApi {

    fun initApi(tranifoMetadataService: TranifoMetadataService) {

        get("/stopInformation", { _, _ ->

            val metadata = tranifoMetadataService.getStopConfig()
            if (metadata == null) {
                "Keine StopId gesetzt"
            } else {
                metadata.stopId
            }
        })

        post("/stopInformation") { req, res ->
            val body = req.body()
            println(body)
            res.status(201)
            val creation = jacksonObjectMapper().readValue(body, StopIdPayload::class.java)
            println(creation)
            tranifoMetadataService.save(creation.stopId, creation.route, creation.destination)
            "ok"
        }


        post("/notification") { req, res ->
            val body = req.body()
            println(body)
            res.status(201)
            val creation = jacksonObjectMapper().readValue(body, NotficationConfig::class.java)
            println(creation)
            println("Received notificaction config ${creation.hour} and ${creation.interval}")
            tranifoMetadataService.save(creation.hour, creation.interval)
            "ok"
        }


    }
}