package org.powbot.krulvis.walking

import org.powbot.krulvis.api.ATContext.distance
import org.powbot.krulvis.api.extensions.walking.local.LocalPath.Companion.getNext
import org.powbot.krulvis.api.extensions.walking.local.LocalPathFinder
import org.powbot.krulvis.api.extensions.walking.local.nodes.LocalEdgeType
import org.powbot.krulvis.api.extensions.walking.local.nodes.StartEdge
import org.powbot.krulvis.api.utils.Random
import org.powbot.krulvis.api.utils.Timer
import org.powbot.walking.model.Edge
import org.powbot.walking.model.EdgeType
import org.powbot.walking.model.TileInteraction
import org.powerbot.script.ClientContext.*
//import org.powerbot.gui.controller.ShellController
import org.powerbot.script.Condition
import org.powerbot.script.Tile
import org.powerbot.script.rt4.ClientContext
import org.slf4j.LoggerFactory
import java.util.concurrent.Callable

object Walking {

    val logger = LoggerFactory.getLogger(javaClass)

    private fun findNext(path: List<Edge<*>>, visited: Set<Edge<*>>, destination: Tile): Edge<*>? {
        if (path.isEmpty()) {
            logger.info("Path is empty")
            return null
        }

        /**
         * Filter only edges that are a feasible candidate
         */
        val filteredEdges = path.filter { edge ->
            val from = edge.from
            !visited.contains(edge) && (from == null || from.toRegularTile().distance() <= 20)
        }

        /**
         * Check if there is any edge within the feasible edges that requires a special interaction
         */
        val specialEdge = filteredEdges.firstOrNull { it.type != EdgeType.Tile }
        if (specialEdge != null) {
            return specialEdge
        }

        /**
         * If the final destination is close, make a new edge with the destination as `to`
         */
        return if (destination.distance() < 25) object : Edge<TileInteraction>(
            from = path.last().to,
            to = destination.toWebTile(),
            type = EdgeType.Tile,
            interaction = TileInteraction()
        ) {} else filteredEdges.lastOrNull { it.to.toRegularTile().distance() <= 25 }
    }

    private fun waitTillIdle() {
        Condition.wait({
            ClientContext.ctx().movement.destination() == Tile.NIL &&
                    !ClientContext.ctx().players.local().inMotion()
        }, 600, 15)
    }

    private fun atDestination(destination: Tile, waitForIdle: Boolean = true): Boolean {
        if (waitForIdle) {
            waitTillIdle()
        }
        val myDest = ClientContext.ctx().movement.destination() ?: Tile.NIL
        val myTile = if (myDest != Tile.NIL) myDest else ClientContext.ctx().players.local().tile()
        val a = myTile.distanceTo(destination).toInt() <= 1
//        println("AtDestination: $a, myTile: $myTile, destination: $destination")
        return a
    }

    fun walkPath(
        _path: List<Edge<*>>,
        destination: Tile,
        walkUntil: Callable<Boolean>,
        runMin: Int,
        runMax: Int
    ): PBWebWalkingResult {
        var path = _path
        var attempts = 0
        val visited = mutableSetOf<Edge<*>>()
        var nextEdge: Edge<*>? = null

        logger.info("Path is [$_path]")
        while (
            !atDestination(destination, false)
            && ctx().controller.script() != null
            && attempts < 5
        ) {
            if (walkUntil.call()) {
                break
            }

            nextEdge = findNext(path, visited, destination)
            if (nextEdge == null) {
                logger.info("Could not find next Edge")
                break
            }

            logger.info("Next step is $nextEdge, distance: ${nextEdge.to.toRegularTile().distance()}")

            val result = if (nextEdge.type == EdgeType.Tile) {
                traverseLocally(nextEdge.to.toRegularTile(), walkUntil, runMin, runMax)
            } else {
                if (nextEdge.from != null && ClientContext.ctx().players.local().tile()
                        .distanceTo(nextEdge.from?.toRegularTile()!!) > 2
                ) {
                    traverseLocally(nextEdge.from?.toRegularTile()!!, walkUntil, runMin, runMax)
                }
                logger.info("Handling special edge: $nextEdge")

                nextEdge.interaction.handle()
            }

            if (result) {
                visited.add(nextEdge)
                attempts = 0
                if (path.indexOf(nextEdge) + 1 >= path.size) {
                    path = path.subList(path.indexOf(nextEdge), path.size)
                } else {
                    path = path.subList(path.indexOf(nextEdge) + 1, path.size)
                }
            } else {
                attempts++
            }
        }

        if (atDestination(destination, true) || walkUntil.call()) {
            return PBWebWalkingResult(true, true, null)
        }

        val failureReason = if (nextEdge == null) {
            logger.info("Next node is null")
            FailureReason.CantReachNextNode
        } else {
            if (nextEdge.type != EdgeType.Tile) {
                FailureReason.FailedInteract
            } else if (!ClientContext.ctx().movement.reachable(
                    ClientContext.ctx().players.local().tile(),
                    nextEdge.to.toRegularTile()
                )
            ) {
                logger.info("Can't react next to tile [$nextEdge]")

                FailureReason.CantReachNextNode
            } else {
                FailureReason.Unknown
            }
        }
        return PBWebWalkingResult(true, false, failureReason)
    }

    fun traverseLocally(edgeDest: Tile, walkUntil: Callable<Boolean>, runMin: Int, runMax: Int): Boolean {
        if (edgeDest.floor() != ClientContext.ctx().client().floor) {
            return false
        }

        if (atDestination(edgeDest, false)) {
            return true
        }

        var path = LocalPathFinder.findPath(edgeDest)
        var attempts = 0
        while (
            path.isNotEmpty() &&
            !atDestination(edgeDest, false) &&
            !ctx().controller.isStopping &&
            attempts <= 5
        ) {
            if (walkUntil.call()) {
                return true
            }

//                path.tilePath.setRunMin(runMin)
//                path.tilePath.setRunMin(runMax)
            val next = path.actions.getNext() ?: break
            logger.info("Next tile in localwalker: $next")
            if (next is StartEdge && next.destination.distance() <= 1) {
                return true
            }
            if (next.execute()) {
                Condition.wait {
                    next.destination.distance() <= 7
                }
            }
            path = LocalPathFinder.findPath(edgeDest)
            attempts++
        }

        return atDestination(edgeDest, false)
    }
}