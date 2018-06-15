package de.ktl.tranifo.ui

import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Insets
import tornadofx.*

/**
 * @author  Julia Burgard - burgard@synyx.de
 */
class Center() : View() {

    override val root = vbox {
        val stopid = khttp.get(
                url = "http://localhost:4567/stopId")
        val stopId = SimpleStringProperty()
        fieldset("Stop Information") {

            vbox {
                label("Stop id: " + stopid.text) {
                    vboxConstraints { margin = Insets(5.0) }

                }
                hbox {
                    label("Stop Id:") {
                        hboxConstraints { margin = Insets(5.0) }

                    }

                    textfield {
                        promptText = "Enter your stopId"
                        textProperty().bindBidirectional(stopId)
                        hboxConstraints { margin = Insets(5.0) }

                    }
                }

            }
            button("Save") {
                vboxConstraints { margin = Insets(5.0) }
                disableProperty().bind(stopId.isNull.or(stopId.isEmpty))
            }.setOnAction {
                khttp.post(
                        url = "http://localhost:4567/stopId",
                        json = mapOf("stopId" to stopId.get()))

            }


        }
    }

}
