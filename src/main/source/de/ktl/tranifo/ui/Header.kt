package de.ktl.tranifo.ui

import tornadofx.View
import tornadofx.label
import tornadofx.vbox

/**
 * @author  Julia Burgard - burgard@synyx.de
 */
class Header() : View() {
    override val root = vbox {
        label("KVV Client")
    }

}