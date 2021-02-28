package uk.henrytwist.kotlinbasics

open class Event<T>(private val content: T) {

    var consumed = false
        private set

    fun peek() = content

    fun consume(): T {

        consumed = true
        return content
    }
}

class Trigger : Event<Unit>(Unit)