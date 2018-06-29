package de.ktl.tranifo.kvvliveapi

enum class Route(val routeName: String) {
        TWO("2"),
        FIVE("5"),
        S2("S2");

        override fun toString(): String{
                return routeName
        }


}