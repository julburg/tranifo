package de.ktl.tranifo.kvvliveapi

import khttp.get
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * @author  Julia Burgard - burgard@synyx.de
 */
fun departures(route: String, stopId: String): List<Departure> {

    val jsonObject = get("https://live.kvv.de/webapp/departures/byroute/$route/$stopId" +
            "?maxInfos=10&key=$KEY").jsonObject

    val departures = jsonObject.getJSONArray("departures")
    return departures.toList().map { obj -> Departure(obj.getString("route"), obj.getString("destination"), obj.getString("time"), obj.getBoolean("realtime")) };
}

fun destinations(route: String, stopId: String): Set<String> {

    val departures1 = departures(route, stopId);
    return departures1.map { departure -> departure.destination }.toSet()
}


fun stops(lat: String, lon: String): List<Stop> {

    val jsonObject = get("https://live.kvv.de/webapp/stops/bylatlon/$lat/$lon?key=$KEY").jsonObject

    val stops = jsonObject.getJSONArray("stops")
    return stops.toList().map { obj -> Stop(obj.getString("id"), obj.getString("name"), obj.getInt("distance")) };
}

data class Departure(val route: String, val destination: String, val time: String, val realtime: Boolean) {

    override fun toString(): String {
        return "$route $destination: $time"
    }

    fun parseTime(): LocalTime? {

        val regexOnlyMinutes = Regex("""(\d) min""")
        val regexHoursAndMinutes = Regex("""\d{2}:\d{2}""")
        val regexZeroMinutes = Regex("""^\d${'$'}""")

        if (regexHoursAndMinutes.containsMatchIn(time)) {

            return LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"))
        }
        if (regexOnlyMinutes.containsMatchIn(time)) {
            val (minutes: String) = regexOnlyMinutes.find(time)!!.destructured
            return LocalTime.now().plusMinutes(minutes.toLong())
        }

        if (regexZeroMinutes.containsMatchIn(time)) {
            return LocalTime.now()
        }
        throw IllegalStateException("time not parsable")
    }
}


data class Stop(val id: String, val name: String, val distance: Int) {

    override fun toString(): String {
        return name
    }

}

fun JSONArray.toList(): List<JSONObject> = (0 until length()).asSequence().map { get(it) as JSONObject }.toList()