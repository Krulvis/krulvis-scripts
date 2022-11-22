package org.powbot.krulvis.blastfurnace.tree.leaf

import org.powbot.api.rt4.*
import org.powbot.api.script.tree.Leaf
import org.powbot.krulvis.api.ATContext.containsOneOf
import org.powbot.krulvis.api.ATContext.debug
import org.powbot.krulvis.api.ATContext.walkAndInteract
import org.powbot.krulvis.api.extensions.items.Bar
import org.powbot.krulvis.api.extensions.items.Ore
import org.powbot.krulvis.api.utils.Utils.long
import org.powbot.krulvis.api.utils.Utils.waitFor
import org.powbot.krulvis.blastfurnace.*
import org.powbot.mobile.script.ScriptManager

class HandleBank(script: BlastFurnace) : Leaf<BlastFurnace>(script, "Handle bank") {

    override fun execute() {
        //Reset waiting at dispenser
        script.waitForBars = false
        if (Bank.opened()) {
            debug("Bank is open")
            if (Inventory.containsOneOf(*Bar.values().map { it.id }.toIntArray())) {
                debug("Depositing bars")
                Bank.depositAllExcept(COAL_BAG_CLOSED, COAL_BAG_OPENED, ICE_GLOVES, GOLD_GLOVES)
            } else if (!Inventory.isFull()) {
                if (!Inventory.containsOneOf(ICE_GLOVES)
                    && !Equipment.containsOneOf(ICE_GLOVES)
                    && Bank.containsOneOf(ICE_GLOVES)
                ) {
                    debug("Withdrawing ice gloves")
                    Bank.withdraw(ICE_GLOVES, 1)
                }
                val bankBag = Bank.stream().id(COAL_BAG_OPENED, COAL_BAG_CLOSED).firstOrNull()
                if (canUseCoalBag() && !Inventory.containsOneOf(COAL_BAG_OPENED, COAL_BAG_CLOSED)) {
                    if (bankBag != null && Bank.withdraw(bankBag.id, 1)) {
                        waitFor { Inventory.containsOneOf(bankBag.id) }
                    }
                }
                if (script.bar == Bar.GOLD
                    && !Inventory.containsOneOf(GOLD_GLOVES)
                    && !Equipment.containsOneOf(GOLD_GLOVES)
                    && Bank.containsOneOf(GOLD_GLOVES)
                ) {
                    debug("Withdrawing gold gloves")
                    Bank.withdraw(GOLD_GLOVES, 1)
                }
                Inventory.stream().id(COAL_BAG_OPENED, COAL_BAG_CLOSED).findFirst().ifPresent {
                    it.click("Fill")
                    script.filledCoalBag = true
                }

                val nextOre = nextOre()
                debug("Withdrawing ore=$nextOre")
                if (Bank.withdraw(nextOre, Bank.Amount.ALL)) {
                    val waited = waitFor { Inventory.containsOneOf(nextOre) }
                    debug("Waited for ore in inv=$waited")
                } else {
                    debug("failed to withdraw ore")
                }

                if (!Inventory.isFull() && !Bank.containsOneOf(nextOre)) {
                    debug("No more ores in bank, stopping script")
                    ScriptManager.stop()
                }
            }
        } else if (Chat.clickContinue()) {
            debug("Opening bank...")
            Objects.stream().name("Bank chest").action("Use").findFirst().ifPresent {
                if (walkAndInteract(it, "Use")) {
                    waitFor(long()) { Bank.opened() }
                }
            }
        } else {
            debug("Stuck can't click continue")
        }

    }

    fun canUseCoalBag(): Boolean = script.bar.secondaryMultiplier > 0

    fun nextOre(): Int {
        val coal = Ore.COAL.blastFurnaceCount
        val hasCoalBag = Inventory.containsOneOf(COAL_BAG_CLOSED)
        return when (script.bar) {
            Bar.GOLD -> Ore.GOLD.id
            Bar.IRON -> Ore.IRON.id
            Bar.STEEL -> if (coal >= if (hasCoalBag) 27 else 28) Ore.IRON.id else Ore.COAL.id
            Bar.MITHRIL -> if (coal >= if (hasCoalBag) 27 else 56) Ore.MITHRIL.id else Ore.COAL.id
            Bar.ADAMANTITE -> if (coal >= if (hasCoalBag) 54 else 84) Ore.ADAMANTITE.id else Ore.COAL.id
            Bar.RUNITE -> if (coal >= if (hasCoalBag) 108 else 112) Ore.RUNITE.id else Ore.COAL.id
            else -> Ore.COAL.id
        }
    }
}