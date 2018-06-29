package de.ktl.tranifo.kvvliveapi

enum class Route(val routeName: String) {
        ONE("1"),
        TWO("2"),
        THREE("3"),
        FOUR("4"),
        FIVE("5"),
        SIX("6"),
        EIGHT("8"),

        S1("S1"),
        S11("S11"),
        S2("S2"),
        S3("S3"),
        S31("S31"),
        S32("S32"),
        S4("S4"),
        S41("S41"),
        S42("S42"),
        S5("S5"),
        S51("S51"),
        S52("S52"),
        S6("S6"),
        S7("S7"),
        S71("S71"),
        S8("S8"),
        S81("S81"),
        S9("S9");

        override fun toString(): String{
                return routeName
        }


}