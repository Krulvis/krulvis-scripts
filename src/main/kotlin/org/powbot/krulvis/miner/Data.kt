package org.powbot.krulvis.miner

import org.powbot.api.Area
import org.powbot.api.Tile
import org.powbot.api.rt4.Inventory
import org.powbot.krulvis.api.extensions.items.Item

object Data {
    val TOOLS = intArrayOf(1265, 1267, 1269, 1271, 1273, 1275, 12297, Item.HAMMER, 11920, 12797, 13243, 13244, 1823)

    var TOP_POLY = Area(
        Tile(3748, 5685),
        Tile(3766, 5685),
        Tile(3766, 5678),
        Tile(3765, 5657),
        Tile(3760, 5657),
        Tile(3761, 5666),
        Tile(3761, 5667),
        Tile(3760, 5672),
        Tile(3758, 5672),
        Tile(3753, 5675),
        Tile(3752, 5675)
    )

    val WATERSKINS = intArrayOf(1823, 1825, 1827, 1829)
    val EMPTY_WATERSKIN = 1831

    val SANDSTONE = intArrayOf(6971, 6973, 6975, 6977)

    fun hasWaterSkins() = Inventory.stream().id(*WATERSKINS).isNotEmpty()

    fun hasEmptyWaterSkin() = Inventory.stream().id(EMPTY_WATERSKIN).isNotEmpty()

}