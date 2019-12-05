package edu.mep3343.battlebuddies

import java.util.*

data class BattleBuddy(
    var owner: String? = null,
    var name: String? = null,
    var type: String? = null,
    var Speed: Int? = null,
    var Power: Int? = null,
    var speedClicks: Int? = null,
    var speedClicksNeeded: Int? = null,
    var lastFed: Date? = null
)