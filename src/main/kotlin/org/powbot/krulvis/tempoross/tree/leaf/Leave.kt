package org.powbot.krulvis.tempoross.tree.leaf

import org.powbot.api.rt4.Npc
import org.powbot.api.rt4.Npcs
import org.powbot.api.script.tree.Leaf
import org.powbot.krulvis.api.ATContext.walkAndInteract
import org.powbot.krulvis.api.utils.Utils.waitFor
import org.powbot.krulvis.tempoross.Tempoross


class Leave(script: Tempoross) : Leaf<Tempoross>(script, "Leaving") {
    override fun execute() {
        script.burningTiles.clear()
        script.triedPaths.clear()
        val leaveNpc = getLeaveNpc()
        if (leaveNpc != null && walkAndInteract(leaveNpc, "Leave")) {
            waitFor(10000) { getLeaveNpc() == null }
        }
    }

    fun getLeaveNpc(): Npc? = Npcs.stream().action("Leave").nearest(script.side.totemLocation).firstOrNull()

}