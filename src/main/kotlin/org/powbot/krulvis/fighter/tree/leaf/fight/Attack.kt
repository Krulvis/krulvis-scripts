package org.powbot.krulvis.fighter.tree.leaf.fight

import org.powbot.krulvis.api.ATContext.interact
import org.powbot.krulvis.api.ATContext.me
import org.powbot.krulvis.api.script.tree.Leaf
import org.powbot.krulvis.api.utils.Utils.waitFor
import org.powbot.krulvis.fighter.Fighter
import org.powerbot.script.rt4.Npc
import java.util.*

class Attack(script: Fighter) : Leaf<Fighter>(script, "Attacking") {
    override fun execute() {
        val target = getTarget()
        if (target.isPresent && interact(target.get(), "Attack")) {
            if (waitFor { me.interacting() == target }) {
                waitFor { me.interacting().healthBarVisible() }
            }
        }
    }

    fun getTarget(): Optional<Npc> {
        return ctx.npcs.toStream().name(*script.profile.names.toTypedArray()).filter { it.interacting() == Npc.NIL }
            .nearest()
            .findFirst()
    }
}