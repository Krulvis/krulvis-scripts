package org.powbot.krulvis.tempoross.tree.branch

import org.powbot.api.rt4.Inventory
import org.powbot.api.script.tree.Branch
import org.powbot.api.script.tree.TreeComponent
import org.powbot.krulvis.api.ATContext.debug
import org.powbot.krulvis.api.ATContext.getCount
import org.powbot.krulvis.tempoross.Data.COOKED
import org.powbot.krulvis.tempoross.Data.DOUBLE_FISH_ID
import org.powbot.krulvis.tempoross.Data.RAW
import org.powbot.krulvis.tempoross.Tempoross
import org.powbot.krulvis.tempoross.tree.leaf.Cook
import org.powbot.krulvis.tempoross.tree.leaf.Fish
import org.powbot.krulvis.tempoross.tree.leaf.Shoot
import kotlin.math.roundToInt

class ShouldShoot(script: Tempoross) : Branch<Tempoross>(script, "Should Shoot") {
    override fun validate(): Boolean {

        val cooked = Inventory.getCount(COOKED)
        val raw = Inventory.getCount(RAW)

        val fish = if (script.solo) cooked else cooked + raw
        //Forced shooting happens after a tether attempt
        if (script.forcedShooting) {
            if (fish == 0) {
                script.forcedShooting = false
            }
            return script.forcedShooting
        }

        val energy = script.getEnergy()
        val hp = script.getHealth()
        val lowEnoughEnergy = energy / 2.5 < fish

        //If we are close to the ammo-box and have some fish, shoot em
        val ammoCrate = script.getAmmoCrate()
        val hasShootableFish = fish > 0
        if (hasShootableFish) {
            if ((ammoCrate?.distance()?.roundToInt() ?: 8) < 7) {
                script.log.info("ForcedShooting because close and hasShootableFish")
                script.forcedShooting = true
            } else if (script.solo) {
                val requiredFish = when (energy) {
                    100 -> 16
                    10 -> 19
                    else -> (energy - 10) / 11
                }
                if (cooked >= requiredFish) {
                    script.log.info("Shooting fish energy=$energy, requiredFish=$requiredFish cooked in inventory")
                    return true
                }
            } else if (hp <= 75 && lowEnoughEnergy && energy > 13) {
                script.log.info("ForcedShooting to empty inventory before last group harpoon")
                script.forcedShooting = true
            } else if (Inventory.isFull() && (!script.cookFish || raw <= 0)) {
                script.log.info("Shooting fish because inventory is full")
                return true
            }
        }

        return false
    }

    override val successComponent: TreeComponent<Tempoross> = Shoot(script)
    override val failedComponent: TreeComponent<Tempoross> = ShouldCook(script)
}


class ShouldCook(script: Tempoross) : Branch<Tempoross>(script, "Should Cook") {
    override fun validate(): Boolean {
        val raw = Inventory.getCount(RAW)
        val cooked = Inventory.getCount(COOKED)
        val fish = raw + cooked
        script.collectFishSpots()
        script.bestFishSpot = script.getClosestFishSpot(script.fishSpots)

        if (!script.cookFish)
            return false

        val doubleSpot = script.bestFishSpot?.id() == DOUBLE_FISH_ID
        val cookLocation = script.side.cookLocation
        val canFish = if (script.solo) fish < 19 else !Inventory.isFull()

        if (doubleSpot && canFish) {
            debug("Fishing because double spot available")
            return false
        } else if (raw > 0 && cookLocation.distance() <= 1.5) {
            debug("Cooking because already there are no good fishing spots available...")
            return true
        } else if (raw >= 8 && !script.hasDangerousPath(cookLocation)) {
            debug("Start early cooking since there is no double spot!")
            return true
        }

        val energy = script.getEnergy()
        val lowEnergy = energy / 4 < Inventory.getCount(true, RAW, COOKED)
        val fullHealth = script.getHealth() == 100
        script.log.info("fullHealth=$fullHealth, energy=$energy, lowEnergy=$lowEnergy, rawCount=$raw")
        return raw > 0 && (Inventory.isFull() || (lowEnergy && !fullHealth) || script.bestFishSpot == null)
    }

    override val successComponent: TreeComponent<Tempoross> = Cook(script)
    override val failedComponent: TreeComponent<Tempoross> = Fish(script)
}
