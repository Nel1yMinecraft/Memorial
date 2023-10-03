package me.memorial.events

open class Event

enum class EventState(val stateName: String) {
    PRE("PRE"), POST("POST")
}