package de.ktl.tranifo.kvvliveapi

import khttp.get
import java.util.*

/**
 * @author  Julia Burgard - burgard@synyx.de
 */

fun getDepartures(route: String, stopId: String): ArrayList<Departure> {

  val jsonObject = get("https://live.kvv.de/webapp/departures/byroute/$route/$stopId" +
            "?maxInfos=10&key=$KEY").jsonObject

    val departures = jsonObject.getJSONArray("departures")
    println(jsonObject)
    var i = 0
    val numIterations = departures.length()
    val departureObjects = ArrayList<Departure>();
    while (i < numIterations) {
        val obj = departures.getJSONObject(i)
        val departure = Departure(obj.getString("route"), obj.getString("destination"), obj.getString("time"), obj.getBoolean("realtime"))
        departureObjects.add(departure)
        i++

    }
    return departureObjects
}

data class Departure(val route: String, val destination: String, val time: String, val realtime: Boolean) {

    override fun toString(): String {
        return route + " " + destination + ": " + time
    }
}