package de.ktl.tranifo.ui

import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class TranifoConfiguration() : View() {

    override val root = hbox {

        val stopId = SimpleStringProperty()

        label("Stop Id") {
        }

        textfield {
            promptText = "Enter your stopId"
            textProperty().bindBidirectional(stopId)
        }

        button("Save").setOnAction {
            khttp.post(
                    url = "http://localhost:4567/stopId",
                    json = mapOf("stopId" to stopId.get()))
        }

    }
}