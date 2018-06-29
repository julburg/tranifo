package de.ktl.tranifo.kvvliveapi

/**
 * @author  Julia Burgard - burgard@synyx.de
 */

fun getDestinations(route: String, stopId: String): Set<String> {

    val departures1 = getDepartures(route, stopId);
    return departures1.map { departure -> departure.destination }.toSet()
}

