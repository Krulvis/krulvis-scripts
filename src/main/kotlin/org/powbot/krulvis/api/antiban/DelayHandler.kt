package org.powbot.krulvis.api.antiban

import org.powbot.krulvis.api.utils.Random
import org.powbot.krulvis.api.utils.Timer

class DelayHandler(min: Int, max: Int, oddsModifier: OddsModifier) {

    constructor(time: Int, oddsModifier: OddsModifier, name: String) : this(
        (time - time * 0.2).toInt(),
        (time + time * 0.2).toInt(),
        oddsModifier,
        name
    )

    private var timer: Timer? = null
    private val min: Int = (min * oddsModifier.minModifier).toInt()
    private val max: Int = (max * oddsModifier.maxModifier).toInt()
    var name: String = " "

    /**
     * Also starts the timer
     */
    fun isFinished(): Boolean {
        if (timer == null) {
            restartTimer()
        } else if (timer!!.isFinished()) {
            return true
        }
        return false
    }

    fun restartTimer() {
        timer = Timer(
            Random.nextGaussian(
                min,
                max
            )
        )
//        println("Setting new $name delay: " + timer!!.getRemainderString())
    }

    fun started(): Boolean {
        return this.timer != null
    }

    fun getRemainder(): String {
        return Timer.formatTime(
            this.timer?.getRemainder() ?: -1
        )
    }

    constructor(min: Int, max: Int, oddsModifier: OddsModifier, name: String) : this(min, max, oddsModifier) {
        this.name = name
    }

    fun resetTimer() {
//        if (timer != null) {
//            println("Resetting $name")
//        }
        timer = null
    }

    override fun toString(): String {
        return "$name delay " + (timer?.getRemainderString() ?: "Not Started")
    }
}