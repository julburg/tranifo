package de.ktl.tranifo.notification

/**
 * @author  Julia Burgard - burgard@synyx.de
 */

interface NotificationManager {
    fun notify(message: String, title: String)
}