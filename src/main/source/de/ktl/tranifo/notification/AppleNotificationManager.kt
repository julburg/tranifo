package de.ktl.tranifo.notification

/**
 * @author  Julia Burgard - burgard@synyx.de
 */
class AppleNotificationManager : NotificationManager {

    override fun notify(message: String, title: String) {
        Runtime.getRuntime().exec(arrayOf("osascript", "-e", " display notification \"" + message + "\"" +
                " with title \"" + title + "\""));


    }
}