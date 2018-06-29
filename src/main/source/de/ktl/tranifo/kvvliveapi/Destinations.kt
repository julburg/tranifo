package de.ktl.tranifo.kvvliveapi

import khttp.get

/**
 * @author  Julia Burgard - burgard@synyx.de
 */

fun getDestinations(route:String, stopId: String): Set<String> {

    val jsonObject = get("https://live.kvv.de/webapp/departures/byroute/$route/$stopId" +
            "?maxInfos=10&key=$KEY").jsonObject
    val departures = jsonObject.getJSONArray("departures")
    println(jsonObject)
    var i = 0
    val numIterations = departures.length()
    val destinations = HashSet<String>();
    while (i < numIterations) {
        destinations.add(departures.getJSONObject(i).getString("destination"))
        i++

    }
    return destinations
}

