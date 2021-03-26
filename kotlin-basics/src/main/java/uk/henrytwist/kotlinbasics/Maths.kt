package uk.henrytwist.kotlinbasics

fun fractionBetween(value: Double, start: Double, end: Double): Double {

    return if (start < end) {

        (value - start) / (end - start)
    } else {

        1 - (value - end) / (start - end)
    }
}