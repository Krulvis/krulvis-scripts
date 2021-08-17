package org.powbot.krulvis.blastfurnace

import org.powbot.api.Tile
import org.powbot.api.rt4.*
import org.powbot.api.script.ScriptCategory
import org.powbot.api.script.ScriptConfiguration
import org.powbot.api.script.ScriptManifest
import org.powbot.krulvis.api.script.ATScript
import org.powbot.api.script.tree.TreeComponent
import org.powbot.krulvis.api.script.painter.ATPainter
import org.powbot.krulvis.api.utils.Timer
import org.powbot.krulvis.api.utils.Utils
import org.powbot.krulvis.blastfurnace.tree.branch.ShouldPay
import java.util.function.Consumer

@ScriptManifest(
    name = "krul BlastFurnace",
    description = "Smelts bars at Blast Furnace",
    version = "1.0.0",
    markdownFileName = "BF.md",
    category = ScriptCategory.Smithing
)
@ScriptConfiguration.List(
    [
        ScriptConfiguration(
            "bar",
            "Which bar do you want to smelt?",
            allowedValues = ["IRON", "STEEL", "MITHRIL", "ADAMANTITE", "RUNITE", "GOLD"],
            defaultValue = "GOLD"
        )
    ]
)
class BlastFurnace : ATScript() {
    var filledCoalBag: Boolean = false

    init {
        hasOptionsConfigured()
    }

    val bar get() = Bar.valueOf(getOption<String>("bar")!!)
    override val painter: ATPainter<*> = BFPainter(this)

    override val rootComponent: TreeComponent<*> = ShouldPay(this)

    var waitForBars = false
    val dispenserTile = Tile(1940, 4963, 0)

    fun interact(matrix: TileMatrix, action: String): Boolean {
        if (!Menu.opened()) {
            matrix.click()
        }
        if (Utils.waitFor(Utils.short()) { Menu.opened() }) {
            if (rightMenuOpen(action)) {
                return Menu.click(Menu.filter(action))
            } else {
                Menu.click(Menu.filter("Cancel"))
            }
        }
        return false
    }

    private fun rightMenuOpen(action: String): Boolean =
        Menu.opened() && Menu.contains { it.action.equals(action, true) }

    val foremanTimer = Timer(0)

    fun shouldPayForeman() = Skills.level(Constants.SKILLS_SMITHING) < 60 && !foremanTimer.isFinished()

    fun cofferCount() = Varpbits.varpbit(795) / 2
}

fun main() {
    BlastFurnace().startScript()
}