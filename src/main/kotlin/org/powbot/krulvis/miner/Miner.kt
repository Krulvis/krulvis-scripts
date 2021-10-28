package org.powbot.krulvis.miner

import org.powbot.api.Tile
import org.powbot.api.event.GameObjectActionEvent
import org.powbot.api.rt4.*
import org.powbot.api.rt4.walking.local.LocalPathFinder
import org.powbot.api.script.*
import org.powbot.api.script.selectors.GameObjectOption
import org.powbot.api.script.tree.TreeComponent
import org.powbot.krulvis.api.antiban.DelayHandler
import org.powbot.krulvis.api.script.ATScript
import org.powbot.krulvis.api.script.painter.ATPaint
import org.powbot.krulvis.api.utils.Utils
import org.powbot.krulvis.miner.Data.TOP_POLY
import org.powbot.krulvis.miner.tree.branch.ShouldFixStrut

@ScriptManifest(
    name = "krul Miner",
    description = "Mines & banks anything, anywhere (supports motherlode)",
    author = "Krulvis",
    version = "1.3.1",
    scriptId = "04f61d39-3abc-420d-84f6-f39243cdf584",
    markdownFileName = "Miner.md",
    category = ScriptCategory.Mining
)
@ScriptConfiguration.List(
    [
        ScriptConfiguration(
            "Rocks",
            "Which rocks do you want to mine?",
            optionType = OptionType.GAMEOBJECT_ACTIONS,
            //Motherlode < 72
            defaultValue = "[{\"name\":\"Rock\",\"tile\":{\"x\":3728,\"y\":5674,\"floor\":0,\"p\":{\"x\":3728,\"y\":5674,\"z\":0}},\"interaction\":\"\"},{\"name\":\"Rock\",\"tile\":{\"x\":3727,\"y\":5672,\"floor\":0,\"p\":{\"x\":3727,\"y\":5672,\"z\":0}},\"interaction\":\"\"},{\"name\":\"Rock\",\"tile\":{\"x\":3727,\"y\":5671,\"floor\":0,\"p\":{\"x\":3727,\"y\":5671,\"z\":0}},\"interaction\":\"\"},{\"name\":\"Rock\",\"tile\":{\"x\":3727,\"y\":5670,\"floor\":0,\"p\":{\"x\":3727,\"y\":5670,\"z\":0}},\"interaction\":\"\"},{\"name\":\"Rock\",\"tile\":{\"x\":3727,\"y\":5668,\"floor\":0,\"p\":{\"x\":3727,\"y\":5668,\"z\":0}},\"interaction\":\"\"},{\"name\":\"Rock\",\"tile\":{\"x\":3727,\"y\":5667,\"floor\":0,\"p\":{\"x\":3727,\"y\":5667,\"z\":0}},\"interaction\":\"\"},{\"name\":\"Rock\",\"tile\":{\"x\":3727,\"y\":5666,\"floor\":0,\"p\":{\"x\":3727,\"y\":5666,\"z\":0}},\"interaction\":\"\"},{\"name\":\"Rock\",\"tile\":{\"x\":3728,\"y\":5664,\"floor\":0,\"p\":{\"x\":3728,\"y\":5664,\"z\":0}},\"interaction\":\"\"},{\"name\":\"Rock\",\"tile\":{\"x\":3728,\"y\":5663,\"floor\":0,\"p\":{\"x\":3728,\"y\":5663,\"z\":0}},\"interaction\":\"\"},{\"name\":\"Rock\",\"tile\":{\"x\":3727,\"y\":5661,\"floor\":0,\"p\":{\"x\":3727,\"y\":5661,\"z\":0}},\"interaction\":\"\"},{\"name\":\"Rock\",\"tile\":{\"x\":3728,\"y\":5659,\"floor\":0,\"p\":{\"x\":3728,\"y\":5659,\"z\":0}},\"interaction\":\"\"},{\"name\":\"Rock\",\"tile\":{\"x\":3728,\"y\":5658,\"floor\":0,\"p\":{\"x\":3728,\"y\":5658,\"z\":0}},\"interaction\":\"\"},{\"name\":\"Rock\",\"tile\":{\"x\":3723,\"y\":5662,\"floor\":0,\"p\":{\"x\":3723,\"y\":5662,\"z\":0}},\"interaction\":\"\"},{\"name\":\"Rock\",\"tile\":{\"x\":3723,\"y\":5663,\"floor\":0,\"p\":{\"x\":3723,\"y\":5663,\"z\":0}},\"interaction\":\"\"},{\"name\":\"Rock\",\"tile\":{\"x\":3723,\"y\":5664,\"floor\":0,\"p\":{\"x\":3723,\"y\":5664,\"z\":0}},\"interaction\":\"\"},{\"name\":\"Rock\",\"tile\":{\"x\":3723,\"y\":5666,\"floor\":0,\"p\":{\"x\":3723,\"y\":5666,\"z\":0}},\"interaction\":\"\"},{\"name\":\"Rock\",\"tile\":{\"x\":3723,\"y\":5667,\"floor\":0,\"p\":{\"x\":3723,\"y\":5667,\"z\":0}},\"interaction\":\"\"},{\"name\":\"Rock\",\"tile\":{\"x\":3724,\"y\":5669,\"floor\":0,\"p\":{\"x\":3724,\"y\":5669,\"z\":0}},\"interaction\":\"\"},{\"name\":\"Rock\",\"tile\":{\"x\":3724,\"y\":5670,\"floor\":0,\"p\":{\"x\":3724,\"y\":5670,\"z\":0}},\"interaction\":\"\"},{\"name\":\"Rock\",\"tile\":{\"x\":3725,\"y\":5674,\"floor\":0,\"p\":{\"x\":3725,\"y\":5674,\"z\":0}},\"interaction\":\"\"},{\"name\":\"Rock\",\"tile\":{\"x\":3726,\"y\":5681,\"floor\":0,\"p\":{\"x\":3726,\"y\":5681,\"z\":0}},\"interaction\":\"\"},{\"name\":\"Rock\",\"tile\":{\"x\":3730,\"y\":5679,\"floor\":0,\"p\":{\"x\":3730,\"y\":5679,\"z\":0}},\"interaction\":\"\"},{\"name\":\"Rock\",\"tile\":{\"x\":3730,\"y\":5678,\"floor\":0,\"p\":{\"x\":3730,\"y\":5678,\"z\":0}},\"interaction\":\"\"},{\"name\":\"Rock\",\"tile\":{\"x\":3730,\"y\":5677,\"floor\":0,\"p\":{\"x\":3730,\"y\":5677,\"z\":0}},\"interaction\":\"\"},{\"name\":\"Rock\",\"tile\":{\"x\":3725,\"y\":5675,\"floor\":0,\"p\":{\"x\":3725,\"y\":5675,\"z\":0}},\"interaction\":\"\"}]"
            //Motherlode >= 72
//            defaultValue = "[{\"interaction\":\"Mine\",\"name\":\"Ore vein\",\"tile\":{\"floor\":0,\"p\":{\"x\":3756,\"y\":5678,\"z\":0},\"x\":3756,\"y\":5678}}, {\"interaction\":\"Mine\",\"name\":\"Ore vein\",\"tile\":{\"floor\":0,\"p\":{\"x\":3756,\"y\":5679,\"z\":0},\"x\":3756,\"y\":5679}}, {\"interaction\":\"Mine\",\"name\":\"Ore vein\",\"tile\":{\"floor\":0,\"p\":{\"x\":3758,\"y\":5680,\"z\":0},\"x\":3758,\"y\":5680}}, {\"interaction\":\"Mine\",\"name\":\"Ore vein\",\"tile\":{\"floor\":0,\"p\":{\"x\":3758,\"y\":5681,\"z\":0},\"x\":3758,\"y\":5681}}, {\"interaction\":\"Examine\",\"name\":\"Depleted vein\",\"tile\":{\"floor\":0,\"p\":{\"x\":3755,\"y\":5681,\"z\":0},\"x\":3755,\"y\":5681}}, {\"interaction\":\"Examine\",\"name\":\"Depleted vein\",\"tile\":{\"floor\":0,\"p\":{\"x\":3755,\"y\":5682,\"z\":0},\"x\":3755,\"y\":5682}}, {\"interaction\":\"Examine\",\"name\":\"Depleted vein\",\"tile\":{\"floor\":0,\"p\":{\"x\":3755,\"y\":5683,\"z\":0},\"x\":3755,\"y\":5683}}, {\"interaction\":\"Examine\",\"name\":\"Depleted vein\",\"tile\":{\"floor\":0,\"p\":{\"x\":3756,\"y\":5684,\"z\":0},\"x\":3756,\"y\":5684}}, {\"interaction\":\"Mine\",\"name\":\"Ore vein\",\"tile\":{\"floor\":0,\"p\":{\"x\":3757,\"y\":5684,\"z\":0},\"x\":3757,\"y\":5684}}, {\"interaction\":\"Mine\",\"name\":\"Ore vein\",\"tile\":{\"floor\":0,\"p\":{\"x\":3758,\"y\":5685,\"z\":0},\"x\":3758,\"y\":5685}}, {\"interaction\":\"Mine\",\"name\":\"Ore vein\",\"tile\":{\"floor\":0,\"p\":{\"x\":3759,\"y\":5682,\"z\":0},\"x\":3759,\"y\":5682}}, {\"interaction\":\"Mine\",\"name\":\"Ore vein\",\"tile\":{\"floor\":0,\"p\":{\"x\":3761,\"y\":5681,\"z\":0},\"x\":3761,\"y\":5681}}, {\"interaction\":\"Mine\",\"name\":\"Ore vein\",\"tile\":{\"floor\":0,\"p\":{\"x\":3762,\"y\":5682,\"z\":0},\"x\":3762,\"y\":5682}}, {\"interaction\":\"Mine\",\"name\":\"Ore vein\",\"tile\":{\"floor\":0,\"p\":{\"x\":3762,\"y\":5683,\"z\":0},\"x\":3762,\"y\":5683}}]",
//            defaultValue = "[{\"id\":-1,\"interaction\":\"Mine\",\"mouseX\":-1,\"mouseY\":-1,\"rawEntityName\":\"Ore vein\",\"rawOpcode\":3,\"var0\":-1,\"widgetId\":-1,\"tile\":{\"floor\":0,\"x\":3756,\"y\":5678,\"rendered\":true},\"name\":\"Ore vein\",\"strippedName\":\"Ore vein\"},{\"id\":-1,\"interaction\":\"Mine\",\"mouseX\":-1,\"mouseY\":-1,\"rawEntityName\":\"Ore vein\",\"rawOpcode\":3,\"var0\":-1,\"widgetId\":-1,\"tile\":{\"floor\":0,\"x\":3756,\"y\":5679,\"rendered\":true},\"name\":\"Ore vein\",\"strippedName\":\"Ore vein\"},{\"id\":-1,\"interaction\":\"Mine\",\"mouseX\":-1,\"mouseY\":-1,\"rawEntityName\":\"Ore vein\",\"rawOpcode\":3,\"var0\":-1,\"widgetId\":-1,\"tile\":{\"floor\":0,\"x\":3758,\"y\":5680,\"rendered\":true},\"name\":\"Ore vein\",\"strippedName\":\"Ore vein\"},{\"id\":-1,\"interaction\":\"Mine\",\"mouseX\":-1,\"mouseY\":-1,\"rawEntityName\":\"Ore vein\",\"rawOpcode\":3,\"var0\":-1,\"widgetId\":-1,\"tile\":{\"floor\":0,\"x\":3758,\"y\":5681,\"rendered\":true},\"name\":\"Ore vein\",\"strippedName\":\"Ore vein\"},{\"id\":-1,\"interaction\":\"Examine\",\"mouseX\":-1,\"mouseY\":-1,\"rawEntityName\":\"Depleted vein\",\"rawOpcode\":3,\"var0\":-1,\"widgetId\":-1,\"tile\":{\"floor\":0,\"x\":3755,\"y\":5681,\"rendered\":true},\"name\":\"Depleted vein\",\"strippedName\":\"Depleted vein\"},{\"id\":-1,\"interaction\":\"Examine\",\"mouseX\":-1,\"mouseY\":-1,\"rawEntityName\":\"Depleted vein\",\"rawOpcode\":3,\"var0\":-1,\"widgetId\":-1,\"tile\":{\"floor\":0,\"x\":3755,\"y\":5682,\"rendered\":true},\"name\":\"Depleted vein\",\"strippedName\":\"Depleted vein\"},{\"id\":-1,\"interaction\":\"Examine\",\"mouseX\":-1,\"mouseY\":-1,\"rawEntityName\":\"Depleted vein\",\"rawOpcode\":3,\"var0\":-1,\"widgetId\":-1,\"tile\":{\"floor\":0,\"x\":3755,\"y\":5683,\"rendered\":true},\"name\":\"Depleted vein\",\"strippedName\":\"Depleted vein\"},{\"id\":-1,\"interaction\":\"Examine\",\"mouseX\":-1,\"mouseY\":-1,\"rawEntityName\":\"Depleted vein\",\"rawOpcode\":3,\"var0\":-1,\"widgetId\":-1,\"tile\":{\"floor\":0,\"x\":3756,\"y\":5684,\"rendered\":true},\"name\":\"Depleted vein\",\"strippedName\":\"Depleted vein\"},{\"id\":-1,\"interaction\":\"Mine\",\"mouseX\":-1,\"mouseY\":-1,\"rawEntityName\":\"Ore vein\",\"rawOpcode\":3,\"var0\":-1,\"widgetId\":-1,\"tile\":{\"floor\":0,\"x\":3757,\"y\":5684,\"rendered\":true},\"name\":\"Ore vein\",\"strippedName\":\"Ore vein\"},{\"id\":-1,\"interaction\":\"Mine\",\"mouseX\":-1,\"mouseY\":-1,\"rawEntityName\":\"Ore vein\",\"rawOpcode\":3,\"var0\":-1,\"widgetId\":-1,\"tile\":{\"floor\":0,\"x\":3758,\"y\":5685,\"rendered\":true},\"name\":\"Ore vein\",\"strippedName\":\"Ore vein\"},{\"id\":-1,\"interaction\":\"Mine\",\"mouseX\":-1,\"mouseY\":-1,\"rawEntityName\":\"Ore vein\",\"rawOpcode\":3,\"var0\":-1,\"widgetId\":-1,\"tile\":{\"floor\":0,\"x\":3759,\"y\":5682,\"rendered\":true},\"name\":\"Ore vein\",\"strippedName\":\"Ore vein\"},{\"id\":-1,\"interaction\":\"Mine\",\"mouseX\":-1,\"mouseY\":-1,\"rawEntityName\":\"Ore vein\",\"rawOpcode\":3,\"var0\":-1,\"widgetId\":-1,\"tile\":{\"floor\":0,\"x\":3761,\"y\":5681,\"rendered\":true},\"name\":\"Ore vein\",\"strippedName\":\"Ore vein\"},{\"id\":-1,\"interaction\":\"Mine\",\"mouseX\":-1,\"mouseY\":-1,\"rawEntityName\":\"Ore vein\",\"rawOpcode\":3,\"var0\":-1,\"widgetId\":-1,\"tile\":{\"floor\":0,\"x\":3762,\"y\":5682,\"rendered\":true},\"name\":\"Ore vein\",\"strippedName\":\"Ore vein\"},{\"id\":-1,\"interaction\":\"Mine\",\"mouseX\":-1,\"mouseY\":-1,\"rawEntityName\":\"Ore vein\",\"rawOpcode\":3,\"var0\":-1,\"widgetId\":-1,\"tile\":{\"floor\":0,\"x\":3762,\"y\":5683,\"rendered\":true},\"name\":\"Ore vein\",\"strippedName\":\"Ore vein\"}]"
            //Alkharid 2 ores
//            defaultValue = "[{\"interaction\":\"Mine\",\"name\":\"Rocks\",\"tile\":{\"floor\":0,\"p\":{\"x\":3303,\"y\":3284,\"z\":0},\"x\":3303,\"y\":3284}}, {\"interaction\":\"Mine\",\"name\":\"Rocks\",\"tile\":{\"floor\":0,\"p\":{\"x\":3302,\"y\":3285,\"z\":0},\"x\":3302,\"y\":3285}}]"
        ),
        ScriptConfiguration(
            "Bank ores",
            "Bank the ores you mine?",
            optionType = OptionType.BOOLEAN,
            defaultValue = "true"
        ),
        ScriptConfiguration(
            "Hop",
            "Hop from players?",
            optionType = OptionType.BOOLEAN,
            defaultValue = "false"
        ),
        ScriptConfiguration(
            "Fast mine",
            "Don't sleep",
            optionType = OptionType.BOOLEAN,
            defaultValue = "true"
        ),
    ]
)
class Miner : ATScript() {

    val rockLocations by lazy {
        val o = getOption<List<GameObjectActionEvent>>("Rocks")
        log.info(o.toString())
        o?.map { it.tile } ?: emptyList()
    }

    val bankOres by lazy { getOption<Boolean>("Bank ores") ?: true }
    val fastMine by lazy { getOption<Boolean>("Fast mine") ?: true }
    val hopFromPlayers by lazy { getOption<Boolean>("Hop") ?: false }

    val mineDelay = DelayHandler(2000, oddsModifier, "MineDelay")
    var lastPayDirtDrop = 0L
    var waterskins = false

    override fun createPainter(): ATPaint<*> = MinerPainter(this)

    override val rootComponent: TreeComponent<*> = ShouldFixStrut(this)

    /**
     * Used to get the amount of loot in the motherload sack
     */
    fun getMotherloadCount(): Int = (Varpbits.varpbit(375) and 65535) / 256

    var shouldEmptySack = false

    /**
     * Get the sack in the motherload mine
     */
    fun getSack() = Objects.stream().name("Sack").action("Search").findFirst()

    fun getBrokenStrut() = Objects.stream().name("Broken strut").nearest().firstOrNull()

    fun inTopFloorAreas(): Boolean {
        return inTopFloorAreas(Players.local().tile())
    }

    fun inTopFloorAreas(t: Tile): Boolean {
        return TOP_POLY.contains(t)
    }

    val nearHopper = Tile(3748, 5673, 0)
    val northOfLadder = Tile(3755, 5675, 0)
    val ladderTile = Tile(3755, 5674, 0)
    fun escapeTopFloor(): Boolean {
        val pos = Players.local().tile()
        if (inTopFloorAreas() && LocalPathFinder.findPath(pos, nearHopper, true).isEmpty()) {
            if (northOfLadder.distance() <= 5 && LocalPathFinder.findPath(pos, northOfLadder, true).isNotEmpty()) {
                if (ladderTile.matrix().interact("Climb")) {
                    return Utils.waitFor {
                        LocalPathFinder.findPath(Players.local().tile(), nearHopper, true).isNotEmpty()
                    }
                }
            } else {
                log.info("Walking to top of ladder first...")
                Movement.walkTo(northOfLadder)
            }
            return false
        }
        return true
    }
}

fun main() {
    Miner().startScript("127.0.0.1", "krullieman", false)
}