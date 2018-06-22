package de.ktl.tranifo.kvvliveapi

import khttp.get
import java.util.*

/**
 * @author  Julia Burgard - burgard@synyx.de
 */
fun getStops(lat: String, lon: String): ArrayList<Stop> {
    val jsonObject = get("https://live.kvv.de/webapp/stops/bylatlon/$lat/$lon" +
            "?key=$KEY").jsonObject
    val stops = jsonObject.getJSONArray("stops")
    println(jsonObject)

    var i = 0
    val numIterations = stops.length()
    val stopObjects = ArrayList<Stop>();
    while (i < numIterations) {
        val obj = stops.getJSONObject(i)
        val stop = Stop(obj.getString("id"), obj.getString("name"), obj.getInt("distance"))
        stopObjects.add(stop)
        i++

    }

    return stopObjects;

}

data class Stop(val id: String, val name: String, val distance: Int) {

    override fun toString(): String {
        return name
    }

}

