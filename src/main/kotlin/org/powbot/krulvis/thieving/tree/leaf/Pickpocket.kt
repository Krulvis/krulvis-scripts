package org.powbot.krulvis.thieving.tree.leaf

import org.powbot.api.rt4.Constants
import org.powbot.api.rt4.Skills
import org.powbot.krulvis.api.ATContext.interact
import org.powbot.api.script.tree.Leaf
import org.powbot.krulvis.api.utils.Random
import org.powbot.krulvis.api.utils.Utils.waitFor
import org.powbot.krulvis.thieving.Thiever
import java.util.*

class Pickpocket(script: Thiever) : Leaf<Thiever>(script, "Pickpocket") {
    override fun execute() {
        val target = script.getTarget()
        if(target != null) {
            val xp = Skills.experience(Constants.SKILLS_THIEVING)
            if (interact(target, "Pickpocket")) {
                waitFor(Random.nextInt(4000, 7000)) { xp < Skills.experience(Constants.SKILLS_THIEVING) }
            }
        }
    }
}