package de.ktl.tranifo.ui

import de.ktl.tranifo.kvvliveapi.Route
import de.ktl.tranifo.kvvliveapi.Stop
import de.ktl.tranifo.kvvliveapi.getDestinations
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
        val stops = getStops("49.0040079", "8.3849635")

        val stop = SimpleObjectProperty<Stop>()
        val route = SimpleObjectProperty<Route>()
        val destination = SimpleObjectProperty<String>()
        val stopProperties = SortedFilteredList<Stop>()
        stopProperties.addAll(stops)
        val destinationsProperties = SortedFilteredList<String>()

        fieldset("Stop Information") {

            hbox {
                val routesView = listview<Route> {
                    items.addAll(Route.values())
                    selectionModel.selectionMode = SelectionMode.SINGLE
                }
                val stopsView = listview<Stop> {
                    selectionModel.selectionMode = SelectionMode.SINGLE
                }
                SortedFilteredList(stopProperties).bindTo(stopsView)

                val destinationsView = listview<String> {
                    selectionModel.selectionMode = SelectionMode.SINGLE
                }
                val destinationsData = SortedFilteredList(destinationsProperties).bindTo(destinationsView)


                routesView.setOnMouseClicked {
                    val selecteRoute = routesView.selectionModel.selectedItem;
                    if (selecteRoute != null) {
                        route.setValue(selecteRoute)
                        reloadDestinations(route, stop, destinationsData, destination)
                    }
                }
                stopsView.setOnMouseClicked {
                    val selecteStop = stopsView.selectionModel.selectedItem;
                    if (selecteStop != null) {
                        stop.setValue(selecteStop)
                        reloadDestinations(route, stop, destinationsData, destination)
                    }
                }
                destinationsView.setOnMouseClicked {
                    val selecteDestination = destinationsView.selectionModel.selectedItem;
                    if (selecteDestination != null) {
                        destination.setValue(selecteDestination)
                    }
                }
            }


            button("Save") {
                vboxConstraints { margin = Insets(5.0) }
                disableProperty().bind(destination.isNull)
            }.setOnAction {
                println("post for stop" + stop.get())
                khttp.post(
                        url = "http://localhost:4567/stopId",
                        json = mapOf("stopId" to stop.get().id))

            }


        }
    }

    private fun reloadDestinations(route: SimpleObjectProperty<Route>, stop: SimpleObjectProperty<Stop>,
                                   destinationsData: SortedFilteredList<String>, destination: SimpleObjectProperty<String>) {
        if (route.get() != null && stop.get() != null) {
            val destinations = getDestinations(route.get().toString(), stop.get().id)
            destinationsData.removeAll(destinationsData)
            destination.set(null)
            destinationsData.addAll(destinations)
        }
    }

}
