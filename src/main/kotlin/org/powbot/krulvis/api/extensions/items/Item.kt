package org.powbot.krulvis.api.extensions.items

import org.powbot.api.rt4.Bank
import org.powbot.api.rt4.Inventory
import org.powbot.api.rt4.Item
import org.powbot.krulvis.api.ATContext.getCount
import org.powbot.krulvis.api.utils.Utils.waitFor

interface Item {

    val ids: IntArray

    val id: Int
        get() = ids[0]

    fun getNotedIds(): IntArray = ids.map { it + 1 }.toIntArray()

    fun notedInBank(): Boolean = Bank.stream().id(*getNotedIds()).isNotEmpty()

    fun inInventory(): Boolean = Inventory.stream().id(*ids).isNotEmpty()

    fun hasWith(): Boolean

    fun inBank(): Boolean = Bank.stream().id(*ids).isNotEmpty()

    fun getBankId(worse: Boolean = false): Int {
        val ids = if (worse) ids.reversed().toIntArray() else ids
        val bankIds = Bank.stream().filtered { it.id() in ids }.map { it.id }
        val bankItem = ids.firstOrNull { it in bankIds }
        return bankItem ?: -1
    }

    fun getInvItem(worse: Boolean = true): Item? {
        return if (worse) {
            val items = Inventory.stream().list()
            ids.reversed().forEach { id ->
                if (items.any { it.id == id }) {
                    return items.firstOrNull { it.id == id }
                }
            }
            return null
        } else
            Inventory.stream().id(*ids).firstOrNull()
    }

    fun getInventoryCount(countNoted: Boolean = true): Int {
        return if (countNoted) Inventory.stream()
            .filtered { ids.contains(it.id()) || getNotedIds().contains(it.id()) }
            .sumOf { if (it.stack <= 0) 1 else it.stack }
        else Inventory.stream().id(*ids).count().toInt()
    }

    fun getCount(countNoted: Boolean = true): Int

    fun withdrawExact(amount: Int, worse: Boolean = false, wait: Boolean = true): Boolean {
        val currentAmount = Inventory.getCount(*ids)
        if (currentAmount == amount) {
            return true
        } else if (currentAmount > amount) {
            if (Bank.deposit(Inventory.stream().id(*ids).first().id, currentAmount - amount)) {
                return !wait || waitFor { Inventory.getCount(*ids) == amount }
            }
        } else if (currentAmount < amount) {
            val id = getBankId(worse)
            if (Bank.withdraw(id, amount - currentAmount)) {
                return !wait || waitFor { Inventory.getCount(*ids) == amount }
            }
        }
        return false
    }

//    fun itemName(): String = CacheItemConfig.load(id).name

    companion object {
        val HAMMER = 2347
        val CANNONBALL = 2
        val AMMO_MOULD = 4
        val AMMO_MOULD_DOUBLE = 27012
        val GRIMY_GUAM = 199
        val SAW = 8794
        val BOND_CHARGED = 13190
        val BOND_UNCHARGED = 13192
        val BRONZE_AXE = 1351
        val MITHRIL_AXE = 1355
        val EMPTY_BUCKET = 1925
        val FISHING_NET = 303
        val COOKED_SHRIMP = 315
        val EMPTY_POT = 1931
        val BREAD = 2309
        val BRONZE_PICKAXE = 1265
        val BRONZE_DAGGER = 1205
        val BRONZE_SWORD = 1277
        val WOODEN_SHIELD = 1171
        val SHORTBOW = 841
        val BRONZE_ARROW = 882
        val BALL_OF_WOOL = 1759
        val REDBERRIES = 1951
        val POT_OF_FLOUR = 1933
        val PINK_SKIRT = 1013
        val BEER = 1917
        val BIRD_SNARE = 10006
        val BOX_TRAP = 10008
        val BUCKET_OF_WATER = 1929
        val ASHES = 592
        val BRONZE_BAR = 2349
        val RING_OF_FORGING = 2568
        val SOFT_CLAY = 1791
        val YELLOW_DYE = 1765
        val ROPE = 954
        val COINS = 995
        val TINDERBOX = 590
        val BAG_OF_SALT = 4161
        val WILLOW_LOG = 1519
        val OAK_LOGS = 1521
        val BUCKET_OF_SAP = 4687
        val RAW_KARAMBWAN = 3142
        val RAW_KARAMBWANJI = 3150
        val TRADING_STICK = 6306
        val JADE = 1611
        val OPAL = 1609
        val VIAL = 229
        val PIE_DISH = 2313
        val JUG = 1935
        val TAI_BWO_WANNAI_TELEPORT = 12409
        val ANTI_DRAGON_SHIELD = 1540
        val IRON_PLATELEGS = 1067
        val IRON_PLATEBODY = 1115
        val IRON_FULLHELM = 1153
        val IRON_KITESHIELD = 1191
        val GOLD_BRACELET = 11069
        val GOBLIN_STAFF = 11709
        val ADAMANT_PLATELEGS = 1073
        val ADAMANT_PLATESKIRT = 1091
        val ADAMANT_PLATEBODY = 1123
        val ADAMANT_FULLHELM = 1161
        val ADAMANT_KITESHIELD = 1199
        val ADAMANT_SCIMITAR = 1331
        val ADAMANT_BOOTS = 4129
        val STEEL_NAIL = 1539
        val DRAMEN_STAFF = 772
        val SILVER_SICKLE_B = 2963
        val SALVE_TELEPORT = 19619
        val HOUSE_TELEPORT = 8013
        val CAMELOT_TELEPORT = 8010
        val BOLT_OF_CLOTH = 8790
        val MORT_MYRE_FUNGUS = 2970
        val BURNT_PAGE = 20718
        val EMPTY_TOME = 20716
        val EMPTY_SEAS = 11908
        val KNIFE = 946
        val DARK_KEY = 25244
        val SHIELD_LEFT_HALF = 2366
        val DRAGON_MED_HELM = 1149
        val DRAGON_SPEAR = 1249
        val HERB_SACK_OPEN = 24478
        val RUNE_POUCH = 12791
        val SEED_BOX_OPEN = 24482
    }

}