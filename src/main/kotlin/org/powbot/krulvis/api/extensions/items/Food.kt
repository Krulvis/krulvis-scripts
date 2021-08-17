package org.powbot.krulvis.api.extensions.items

import org.powbot.api.rt4.Game
import org.powbot.krulvis.api.ATContext.currentHP
import org.powbot.krulvis.api.ATContext.maxHP
import org.powbot.krulvis.api.ATContext.missingHP
import org.powbot.krulvis.api.utils.Random
import java.io.Serializable
import kotlin.math.ceil

enum class Food(val healing: Int, override vararg val ids: Int) : Item, Serializable {

    SHRIMP(3, 315),
    CAKES(5, 1891, 1893, 1895),
    TROUT(8, 333),
    SALMON(9, 329),
    PEACH(9, 6883),
    TUNA(10, 361),
    WINE(11, 1993),
    LOBSTER(12, 379),
    BASS(13, 365),
    SWORDFISH(14, 373),
    POTATO_CHEESE(16, 6705),
    MONKFISH(16, 7946),
    SHARK(20, 385),
    KARAMBWAN(16, 3144);

    override fun toString(): String {
        return name
    }

    fun canEat(): Boolean = missingHP() >= healing

    fun eat(): Boolean {
        nextEatPercent = Random.nextInt(25, 55)
        val item = getInvItem()
        Game.tab(Game.Tab.INVENTORY)
        return item.isPresent
                && item.get()
            .interact(if (this == WINE) "Drink" else "Eat")
    }

    fun requiredAmount(): Int {
        val currHealth = currentHP()
        val missingHealth = maxHP() - currHealth
        return ceil(missingHealth.toDouble() / healing.toDouble()).toInt()
    }

    override fun getCount(countNoted: Boolean): Int {
        return getInventoryCount(countNoted)
    }

    override fun hasWith(): Boolean {
        return inInventory()
    }

    companion object {

        var nextEatPercent = Random.nextInt(25, 55)

        fun getFirstFood(): Food? {
            for (f in values()) {
                if (f.inInventory()) {
                    return f
                }
            }
            return null
        }
    }

}