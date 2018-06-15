package de.ktl.tranifo.ui

import tornadofx.View
import tornadofx.borderpane

class TranifoConfiguration() : View() {

    override val root =borderpane{
        top(Header::class)
        center(Center::class)
    }


}