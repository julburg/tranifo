package de.ktl.tranifo.ui

import com.maxmind.geoip.LookupService
import de.ktl.tranifo.kvvliveapi.Stop
import de.ktl.tranifo.kvvliveapi.getStops
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Insets
import javafx.scene.control.SelectionMode
import tornadofx.*


/**
 * @author  Julia Burgard - burgard@synyx.de
 */
class Center() : View() {

    override val root = vbox {
        val cl = LookupService("/Users/juliaburgard/Documents/GeoLiteCity.dat",
                LookupService.GEOIP_MEMORY_CACHE or LookupService.GEOIP_CHECK_CACHE)
        val location = cl.getLocation("109.109.203.66")
        println(location.longitude.toString())
        println(location.latitude.toString())

        val stops = getStops(location.latitude.toString(), location.longitude.toString())

        val stop = SimpleObjectProperty<Stop>()
        fieldset("Stop Information") {

            vbox {
                val listview = listview<Stop> {
                    items.addAll(stops)
                    selectionModel.selectionMode = SelectionMode.SINGLE
                }
                listview.setOnMouseClicked {
                    val selecteStop = listview.selectionModel.selectedItem;
                    if (selecteStop != null) {
                        stop.setValue(selecteStop)
                    }
                }

            }
            button("Save") {
                vboxConstraints { margin = Insets(5.0) }
                disableProperty().bind(stop.isNull)
            }.setOnAction {
                println("post for stop" + stop.get())
                khttp.post(
                        url = "http://localhost:4567/stopId",
                        json = mapOf("stopId" to stop.get().id))

            }


        }
    }

}
