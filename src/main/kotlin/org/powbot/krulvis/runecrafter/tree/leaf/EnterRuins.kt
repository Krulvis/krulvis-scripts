package org.powbot.krulvis.runecrafter.tree.leaf

import org.powbot.krulvis.api.ATContext.interact
import org.powbot.krulvis.api.script.tree.Leaf
import org.powbot.krulvis.api.utils.Utils.waitFor
import org.powbot.krulvis.runecrafter.Runecrafter

class EnterRuins(script: Runecrafter) : Leaf<Runecrafter>(script, "Entering Ruins") {
    override fun execute() {
        val ruins = ctx.objects.toStream(25).name("Mysterious ruins").findFirst()
        ruins.ifPresent {
            val interaction = if (script.hasTalisman()) {
                interact(it, "Use", selectItem = script.profile.type.talisman)
            } else {
                interact(it, "Enter")
            }
            if (interaction) {
                waitFor { script.atAltar() }
            }
        }
    }
}