package de.ktl.tranifo.kvvliveapi

import khttp.get

/**
 * @author  Julia Burgard - burgard@synyx.de
 */
fun getStops(lat: String, lon: String): List<Stop> {

    val jsonObject = get("https://live.kvv.de/webapp/stops/bylatlon/$lat/$lon?key=$KEY").jsonObject

    val stops = jsonObject.getJSONArray("stops")
    return stops.toList().map { obj -> Stop(obj.getString("id"), obj.getString("name"), obj.getInt("distance")) };
}

data class Stop(val id: String, val name: String, val distance: Int) {

    override fun toString(): String {
        return name
    }

}

