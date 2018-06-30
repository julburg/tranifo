package de.ktl.tranifo

import de.ktl.tranifo.kvvliveapi.Departure
import de.ktl.tranifo.kvvliveapi.departures
import de.ktl.tranifo.metadata.TranifoMetadataApi
import de.ktl.tranifo.metadata.TranifoMetadataService
import de.ktl.tranifo.notification.AppleNotificationManager
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter


data class StopIdPayload(val stopId: String, val route: String, val destination: String)

data class NotficationConfig(val hour: Int, val interval: Int)

fun main(args: Array<String>) {
    val tranifoMetadataService = TranifoMetadataService()
    TranifoMetadataApi().initApi(tranifoMetadataService)


    var nextTimeToAsk = 1
    while (true) {
        val stopConfig = tranifoMetadataService.getStopConfig()
        val notificationConfig = tranifoMetadataService.getNotificationConfig();
        if (stopConfig != null && notificationConfig != null) {

            println("StopId:" + stopConfig.stopId)
            if (LocalTime.now().hour >= notificationConfig.hour) {
                val departures = departures(stopConfig.route, stopConfig.stopId)
                val departuresForDirection = departures.filter { departure -> departure.destination.equals(stopConfig.destination) && departure.realtime }
                val newestDeparture = departuresForDirection.get(0)
                val messageLessingstrasse = newestDeparture.toString() + "\n" + departuresForDirection.get(1).toString()

                val timeNextDeparture = parseTime(newestDeparture)!!
                println(timeNextDeparture)

                //the next time to ask should be when the actual tram arrives
                nextTimeToAsk = Duration.between(LocalTime.now(), timeNextDeparture).toMinutes().toInt() + 1 + notificationConfig.intervalMinutes

                AppleNotificationManager().notify(messageLessingstrasse, "Bahn Allert")
            }

            println("Waiting for $nextTimeToAsk minute(s)")
        } else {
            println("Please set the stop id and notification config")
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

