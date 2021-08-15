package org.powbot.krulvis.api.extensions.walking

import org.powbot.api.Tile
import org.powbot.api.rt4.Constants
import org.powbot.api.rt4.GameObject
import org.powbot.api.rt4.Objects
import org.powbot.api.rt4.Skills
import java.util.*

interface PathFinder<P : Path> {

    fun findPath(end: Tile?): P

    companion object {
        val actions = listOf("Open", "Use", "Enter", "Pay")
        val doors = listOf(
            "Door",
            "Glass door",
            "Gate",
            "Large door",
            "Castle door",
            "Gate of War",
            "Rickety door",
            "Oozing barrier",
            "Portal of Death",
            "Magic guild door",
            "Prison door",
            "Barbarian door"
        )
        val blockedDoors = listOf(Tile(2515, 9575, 0), Tile(2568, 9893, 0), Tile(2566, 9901, 0), Tile(2924, 9803, 0))

        fun GameObject.canUseDoor(): Boolean {
            if (tile() == Tile(2611, 3394, 0)) {
                return Skills.level(Constants.SKILLS_ATTACK) >= 68
            }
            return !blockedDoors.contains(tile())
        }

        fun GameObject.isDoor(): Boolean {
            return doors.contains(name())
        }

        fun GameObject.isRockfall(): Boolean {
            return name() == "Rockfall" && actions().contains("Mine")
        }

        fun Tile.getPassableObject(orientation: Int): Optional<GameObject> {
            return Objects.stream().at(this).action(actions).filter {
                it.name().isNotEmpty() &&
                        ((it.isRockfall()) || (it.isDoor() && it.orientation() == orientation && it.canUseDoor()))

            }.findFirst()
        }

        fun Tile.rockfallBlock(flags: Array<IntArray>): Boolean {
            return collisionFlag(flags) in intArrayOf(Flag.ROCKFALL, Flag.ROCKFALL2)
        }
    }

}