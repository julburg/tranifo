package de.ktl.tranifo.kvvliveapi

import khttp.get
import org.json.JSONArray
import org.json.JSONObject

/**
 * @author  Julia Burgard - burgard@synyx.de
 */

fun getDepartures(route: String, stopId: String): List<Departure> {

    val jsonObject = get("https://live.kvv.de/webapp/departures/byroute/$route/$stopId" +
            "?maxInfos=10&key=$KEY").jsonObject

    val departures = jsonObject.getJSONArray("departures")
    return departures.toList().map { obj -> Departure(obj.getString("route"), obj.getString("destination"), obj.getString("time"), obj.getBoolean("realtime")) };
}

data class Departure(val route: String, val destination: String, val time: String, val realtime: Boolean) {

    override fun toString(): String {
        return route + " " + destination + ": " + time
    }
}

fun JSONArray.toList(): List<JSONObject> = (0 until length()).asSequence().map { get(it) as JSONObject }.toList()