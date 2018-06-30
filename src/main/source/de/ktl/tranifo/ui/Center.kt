package de.ktl.tranifo.ui

import de.ktl.tranifo.kvvliveapi.Route
import de.ktl.tranifo.kvvliveapi.Stop
import de.ktl.tranifo.kvvliveapi.destinations
import de.ktl.tranifo.kvvliveapi.stops
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Insets
import javafx.scene.control.SelectionMode
import tornadofx.*

/**
 * @author  Julia Burgard - burgard@synyx.de
 */
class Center() : View() {

    override val root = vbox {
        val stops = stops("49.0040079", "8.3849635")
        val hour = SimpleStringProperty();
        hour.set("17")
        val interval = SimpleStringProperty();
        interval.set("10")
        val stop = SimpleObjectProperty<Stop>()
        val route = SimpleObjectProperty<Route>()
        val destination = SimpleObjectProperty<String>()
        val stopProperties = SortedFilteredList<Stop>()
        stopProperties.addAll(stops)
        val destinationsProperties = SortedFilteredList<String>()

        fieldset("Stop Information") {
            hbox {
                label("Uhrzeit")
                val hourField = textfield {
                    filterInput { it.controlNewText.isHour() }
                    bind(hour)
                }
                val context = ValidationContext()
                context.addValidator(hourField, hourField.textProperty()) {
                    if (it.isNullOrBlank()) error("The hour is required") else null
                }
            }
            hbox {
                label("Interval")
                val intervalField = textfield {
                    filterInput { it.controlNewText.isMinutes() }
                    bind(interval)
                }
                val context = ValidationContext()
                context.addValidator(intervalField, intervalField.textProperty()) {
                    if (it.isNullOrBlank()) error("The interval is required") else null
                }
            }
            button("Save") {
                vboxConstraints { margin = Insets(5.0) }
                disableProperty().bind(hour.isEmpty.or(interval.isEmpty))
            }.setOnAction {
                khttp.post(
                        url = "http://localhost:4567/notification",
                        json = mapOf("hour" to hour.get(), "interval" to interval.get()))

            }
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
                println("post for stop" + stop.get() + " " + route.get() + " " + destination.get())
                khttp.post(
                        url = "http://localhost:4567/stopInformation",
                        json = mapOf("stopId" to stop.get().id, "route" to route.get().routeName, "destination" to destination.get()))

            }


        }
    }

    private fun reloadDestinations(route: SimpleObjectProperty<Route>, stop: SimpleObjectProperty<Stop>,
                                   destinationsData: SortedFilteredList<String>, destination: SimpleObjectProperty<String>) {
        if (route.get() != null && stop.get() != null) {
            val destinations = destinations(route.get().toString(), stop.get().id)
            destinationsData.removeAll(destinationsData)
            destination.set(null)
            destinationsData.addAll(destinations)
        }
    }

}

fun String.isHour(): Boolean = this.isInt() && this.toInt() in 0..23
fun String.isMinutes(): Boolean = this.isInt() && this.toInt() in 1..60