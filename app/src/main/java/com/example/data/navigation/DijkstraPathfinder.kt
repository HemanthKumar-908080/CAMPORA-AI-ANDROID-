package com.example.data.navigation

import com.example.data.model.MapEdge
import com.example.data.model.MapNode
import com.example.data.model.PathResult
import java.util.PriorityQueue
import kotlin.math.roundToInt

object DijkstraPathfinder {

    fun findShortestPath(
        startNodeId: String,
        targetNodeId: String,
        nodes: List<MapNode> = CampusGraph.nodes,
        edges: List<MapEdge> = CampusGraph.edges
    ): PathResult? {
        val startNode = nodes.find { it.id == startNodeId } ?: return null
        val targetNode = nodes.find { it.id == targetNodeId } ?: return null

        if (startNodeId == targetNodeId) {
            return PathResult(
                pathNodes = listOf(startNode),
                totalDistanceMeters = 0,
                estimatedWalkMinutes = 0,
                turnByTurnSteps = listOf("You are already at ${startNode.name}")
            )
        }

        // Build adjacency map
        val adjacencyMap = mutableMapOf<String, MutableList<Pair<String, MapEdge>>>()
        for (edge in edges) {
            adjacencyMap.getOrPut(edge.nodeA) { mutableListOf() }.add(Pair(edge.nodeB, edge))
            adjacencyMap.getOrPut(edge.nodeB) { mutableListOf() }.add(Pair(edge.nodeA, edge))
        }

        val distances = mutableMapOf<String, Int>().withDefault { Int.MAX_VALUE }
        val previousNodes = mutableMapOf<String, String>()
        val previousEdges = mutableMapOf<String, MapEdge>()

        // Priority Queue storing Pair(nodeId, currentDistance)
        val pq = PriorityQueue<Pair<String, Int>>(compareBy { it.second })

        distances[startNodeId] = 0
        pq.add(Pair(startNodeId, 0))

        val visited = mutableSetOf<String>()

        while (pq.isNotEmpty()) {
            val (currentNodeId, currentDist) = pq.poll() ?: break

            if (visited.contains(currentNodeId)) continue
            visited.add(currentNodeId)

            if (currentNodeId == targetNodeId) break

            val neighbors = adjacencyMap[currentNodeId] ?: emptyList()
            for ((neighborId, edge) in neighbors) {
                if (visited.contains(neighborId)) continue

                val newDist = currentDist + edge.distanceMeters
                if (newDist < distances.getValue(neighborId)) {
                    distances[neighborId] = newDist
                    previousNodes[neighborId] = currentNodeId
                    previousEdges[neighborId] = edge
                    pq.add(Pair(neighborId, newDist))
                }
            }
        }

        val pathNodeIds = mutableListOf<String>()
        var curr: String? = targetNodeId

        if (previousNodes[targetNodeId] == null && targetNodeId != startNodeId) {
            return null // No path found
        }

        while (curr != null) {
            pathNodeIds.add(0, curr)
            curr = previousNodes[curr]
        }

        val pathNodes = pathNodeIds.mapNotNull { id -> nodes.find { it.id == id } }
        val totalDistance = distances.getValue(targetNodeId)
        val estimatedMinutes = (totalDistance / 65.0).roundToInt().coerceAtLeast(1)

        // Generate Turn-by-Turn Text Instructions
        val steps = mutableListOf<String>()
        steps.add("Start at ${startNode.name} (${startNode.floor})")

        for (i in 0 until pathNodes.size - 1) {
            val fromNode = pathNodes[i]
            val toNode = pathNodes[i + 1]
            val edge = previousEdges[toNode.id]
            val walkway = edge?.walkwayLabel ?: "Campus Walkway"
            val dist = edge?.distanceMeters ?: 40

            steps.add("Walk ${dist}m along $walkway towards ${toNode.name}")
        }

        steps.add("Arrive at ${targetNode.name} (${targetNode.buildingName})")

        return PathResult(
            pathNodes = pathNodes,
            totalDistanceMeters = totalDistance,
            estimatedWalkMinutes = estimatedMinutes,
            turnByTurnSteps = steps
        )
    }
}
