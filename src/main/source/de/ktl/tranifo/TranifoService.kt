package de.ktl.tranifo

import de.ktl.tranifo.kvvliveapi.Departure
import de.ktl.tranifo.kvvliveapi.departures
import de.ktl.tranifo.metadata.TranifoMetadataApi
import de.ktl.tranifo.metadata.TranifoMetadataService
import de.ktl.tranifo.metadata.TranifoNotificationConfig
import de.ktl.tranifo.metadata.TranifoStopConfig
import de.ktl.tranifo.notification.AppleNotificationManager
import java.time.Duration
import java.time.LocalTime


data class StopIdPayload(val stopId: String, val route: String, val destination: String)

data class NotficationConfig(val hour: Int, val interval: Int)

fun main(args: Array<String>) {
    val tranifoMetadataService = TranifoMetadataService()
    TranifoMetadataApi().initApi(tranifoMetadataService)


    var wait = 1
    while (true) {

        val stopConfig = tranifoMetadataService.getStopConfig()
        val notificationConfig = tranifoMetadataService.getNotificationConfig();

        if (stopConfig != null && notificationConfig != null) {

            println("StopId:" + stopConfig.stopId)
            if (LocalTime.now().hour >= notificationConfig.hour) {
                val departures = departures(stopConfig)
                val message = buildMessage(departures)

                wait = calcurateWaitingTime(departures, wait, notificationConfig)

                AppleNotificationManager().notify(message, "Bahn Allert")
            }

            println("Waiting for $wait minute(s)")
        } else {
            println("Please set the stop id and notification config")
        }
        Thread.sleep(wait.toLong() * 1000 * 60)
    }
}

private fun buildMessage(departures: List<Departure>) =
        departures.get(0).toString() + "\n" + departures.get(1).toString()

private fun departures(stopConfig: TranifoStopConfig): List<Departure> {
    val departures = departures(stopConfig.route, stopConfig.stopId)
    val departuresForDirection = departures.filter { departure -> departure.destination.equals(stopConfig.destination) && departure.realtime }
    return departuresForDirection
}

private fun calcurateWaitingTime(departures: List<Departure>, nextTimeToAsk: Int, notificationConfig: TranifoNotificationConfig): Int {
    var nextTimeToAsk1 = nextTimeToAsk
    val timeNextDeparture = departures.get(0).parseTime()!!
    //the next time to ask should be when the actual tram arrives
    val durationToNextTram = Duration.between(LocalTime.now(), timeNextDeparture).toMinutes().toInt()
    nextTimeToAsk1 = durationToNextTram + 1 + notificationConfig.intervalMinutes
    return nextTimeToAsk1
}

