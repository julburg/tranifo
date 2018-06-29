package de.ktl.tranifo

import de.ktl.tranifo.kvvliveapi.Departure
import de.ktl.tranifo.kvvliveapi.getDepartures
import de.ktl.tranifo.metadata.TranifoMetadataApi
import de.ktl.tranifo.metadata.TranifoMetadataService
import de.ktl.tranifo.notification.AppleNotificationManager
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter


data class StopIdPayload(val stopId: String, val route: String, val destination: String)


fun main(args: Array<String>) {
    val tranifoMetadataService = TranifoMetadataService()
    TranifoMetadataApi().initApi(tranifoMetadataService)


    val hourFromWhichToNotify = 17
    val destination = "Rintheim"

    var nextTimeToAsk = 1
    while (true) {
        val metadata = tranifoMetadataService.getMetadata()
        if (metadata != null) {

            println("StopId:" + metadata.stopId)
            if (LocalTime.now().hour >= hourFromWhichToNotify) {
                val departures = getDepartures(metadata.stopId)
                val departuresForDirection = departures.filter { departure -> departure.destination.equals(destination) && departure.realtime }
                val newestDeparture = departuresForDirection.get(0)
                val messageLessingstrasse = newestDeparture.toString() + "\n" + departuresForDirection.get(1).toString()

                val timeNextDeparture = parseTime(newestDeparture)!!
                println(timeNextDeparture)

                //the next time to ask should be when the actual tram arrives
                nextTimeToAsk = Duration.between(LocalTime.now(), timeNextDeparture).toMinutes().toInt() + 1

                AppleNotificationManager().notify(messageLessingstrasse, "Bahn Allert")
            }

            println("Waiting for " + nextTimeToAsk + " minute(s)")
        } else {
            println("Please set the stop id")
        }
        Thread.sleep(nextTimeToAsk.toLong() * 1000 * 60)
    }
}

private fun parseTime(newestDeparture: Departure): LocalTime? {
    val regexOnlyMinutes = Regex("""(\d) min""")
    val regexHoursAndMinutes = Regex("""\d{2}:\d{2}""")
    val regexZeroMinutes = Regex("""^\d${'$'}""")

    if (regexHoursAndMinutes.containsMatchIn(newestDeparture.time)) {

        return LocalTime.parse(newestDeparture.time, DateTimeFormatter.ofPattern("HH:mm"))
    }
    if (regexOnlyMinutes.containsMatchIn(newestDeparture.time)) {
        val (minutes: String) = regexOnlyMinutes.find(newestDeparture.time)!!.destructured
        return LocalTime.now().plusMinutes(minutes.toLong())
    }

    if (regexZeroMinutes.containsMatchIn(newestDeparture.time)) {
        return LocalTime.now()
    }
    throw IllegalStateException("time not parsable")
}

