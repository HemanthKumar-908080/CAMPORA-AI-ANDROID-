package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MapEdge
import com.example.data.model.MapNode
import com.example.data.model.PathResult
import com.example.data.model.PoiCategory

@Composable
fun CampusMapCanvas(
    nodes: List<MapNode>,
    edges: List<MapEdge>,
    pathResult: PathResult?,
    selectedStartNodeId: String?,
    selectedEndNodeId: String?,
    selectedCategoryFilter: PoiCategory,
    onNodeSelected: (MapNode) -> Unit,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    var tappedNode by remember { mutableStateOf<MapNode?>(null) }
    var showLegend by remember { mutableStateOf(false) }

    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f

    // Route Drawing Animation (0.0f to 1.0f)
    val pathAnimProgress = remember { Animatable(0f) }
    LaunchedEffect(pathResult) {
        if (pathResult != null) {
            pathAnimProgress.snapTo(0f)
            pathAnimProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing)
            )
        } else {
            pathAnimProgress.snapTo(0f)
        }
    }

    // Pulsing animation for path & pins
    val infiniteTransition = rememberInfiniteTransition(label = "map_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .background(if (isDark) Color(0xFF0C1929) else Color(0xFFEDF4FC))
            .testTag("campus_map_canvas")
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = (scale * zoom).coerceIn(0.6f, 3.5f)
                        offsetX += pan.x
                        offsetY += pan.y
                    }
                }
                .pointerInput(nodes) {
                    detectTapGestures { tapOffset ->
                        val canvasWidth = size.width.toFloat()
                        val canvasHeight = size.height.toFloat()
                        val scaleX = canvasWidth / 1000f
                        val scaleY = canvasHeight / 1000f

                        var closestNode: MapNode? = null
                        var minDistanceSq = 1800f * scale * scale

                        for (node in nodes) {
                            val cx = node.x * scaleX
                            val cy = node.y * scaleY
                            val tx = (cx - canvasWidth / 2f) * scale + canvasWidth / 2f + offsetX
                            val ty = (cy - canvasHeight / 2f) * scale + canvasHeight / 2f + offsetY

                            val dx = tapOffset.x - tx
                            val dy = tapOffset.y - ty
                            val distSq = dx * dx + dy * dy

                            if (distSq < minDistanceSq) {
                                minDistanceSq = distSq
                                closestNode = node
                            }
                        }

                        if (closestNode != null) {
                            tappedNode = closestNode
                            onNodeSelected(closestNode)
                        } else {
                            tappedNode = null
                        }
                    }
                }
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            val scaleX = canvasWidth / 1000f
            val scaleY = canvasHeight / 1000f

            fun mapToCanvas(x: Float, y: Float): Offset {
                val cx = x * scaleX
                val cy = y * scaleY
                val transformedX = (cx - canvasWidth / 2f) * scale + canvasWidth / 2f + offsetX
                val transformedY = (cy - canvasHeight / 2f) * scale + canvasHeight / 2f + offsetY
                return Offset(transformedX, transformedY)
            }

            // 1. Draw Campus Background Layout (Lawns & Zones)
            drawCampusZones(this, ::mapToCanvas, isDark)

            // 2. Draw Walkway Edges
            for (edge in edges) {
                val nodeA = nodes.find { it.id == edge.nodeA }
                val nodeB = nodes.find { it.id == edge.nodeB }
                if (nodeA != null && nodeB != null) {
                    val posA = mapToCanvas(nodeA.x, nodeA.y)
                    val posB = mapToCanvas(nodeB.x, nodeB.y)

                    drawLine(
                        color = if (isDark) Color(0xFF1E3854) else Color(0xFFCBD5E1),
                        start = posA,
                        end = posB,
                        strokeWidth = 6f * scale,
                        cap = StrokeCap.Round
                    )
                }
            }

            // 3. Draw Computed Shortest Path Polyline (Animated Drawing)
            if (pathResult != null && pathResult.pathNodes.size > 1) {
                val fullPath = Path()
                val firstPos = mapToCanvas(pathResult.pathNodes.first().x, pathResult.pathNodes.first().y)
                fullPath.moveTo(firstPos.x, firstPos.y)

                for (i in 1 until pathResult.pathNodes.size) {
                    val pos = mapToCanvas(pathResult.pathNodes[i].x, pathResult.pathNodes[i].y)
                    fullPath.lineTo(pos.x, pos.y)
                }

                val pathMeasure = PathMeasure()
                pathMeasure.setPath(fullPath, false)
                val totalLen = pathMeasure.length
                val animatedLen = totalLen * pathAnimProgress.value

                val animatedPath = Path()
                pathMeasure.getSegment(0f, animatedLen, animatedPath, true)

                // Outer Glowing Stroke
                drawPath(
                    path = animatedPath,
                    color = Color(0xFF00B4D8).copy(alpha = 0.5f),
                    style = Stroke(width = 20f * scale, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )

                // Inner Bright Cyan Path
                drawPath(
                    path = animatedPath,
                    color = Color(0xFF06B6D4),
                    style = Stroke(width = 11f * scale, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
            }

            // 4. Draw Building Footprints
            drawBuildingFootprints(this, ::mapToCanvas, isDark)

            // 5. Draw Node Pins, User Radar & Destination Marker
            for (node in nodes) {
                if (selectedCategoryFilter != PoiCategory.ALL && node.category != selectedCategoryFilter) {
                    continue
                }

                val pos = mapToCanvas(node.x, node.y)
                val isStart = node.id == selectedStartNodeId
                val isEnd = node.id == selectedEndNodeId
                val isPathNode = pathResult?.pathNodes?.any { it.id == node.id } == true
                val isTapped = node.id == tappedNode?.id

                val pinColor = when {
                    isStart -> Color(0xFF10B981) // Green User Radar
                    isEnd -> Color(0xFFEF4444)   // Red Destination
                    isPathNode -> Color(0xFF00B4D8) // Cyan Route Node
                    else -> getCategoryColor(node.category, isDark)
                }

                val radius = when {
                    isStart || isEnd -> 14f * scale * pulseScale
                    isTapped -> 14f * scale
                    node.isMainLandmark -> 11f * scale
                    else -> 8f * scale
                }

                // Radar ripple ring for Start / User Location
                if (isStart) {
                    drawCircle(
                        color = Color(0xFF10B981).copy(alpha = 0.35f),
                        radius = radius * 1.8f,
                        center = pos
                    )
                    drawCircle(
                        color = Color(0xFF10B981).copy(alpha = 0.15f),
                        radius = radius * 2.6f,
                        center = pos
                    )
                }

                // Destination ring
                if (isEnd) {
                    drawCircle(
                        color = Color(0xFFEF4444).copy(alpha = 0.35f),
                        radius = radius * 1.8f,
                        center = pos
                    )
                }

                // Highlight tapped selection ring
                if (isTapped) {
                    drawCircle(
                        color = Color(0xFFF59E0B).copy(alpha = 0.4f),
                        radius = radius * 2.0f,
                        center = pos
                    )
                }

                // Pin circle
                drawCircle(
                    color = pinColor,
                    radius = radius,
                    center = pos
                )

                // Inner dot
                drawCircle(
                    color = Color.White,
                    radius = radius * 0.45f,
                    center = pos
                )

                // Node Name Label
                if (scale > 0.8f || node.isMainLandmark || isStart || isEnd || isTapped) {
                    val paint = android.graphics.Paint().apply {
                        color = if (isDark) android.graphics.Color.WHITE else android.graphics.Color.DKGRAY
                        textSize = (11 * scale).coerceIn(10f, 22f)
                        isFakeBoldText = isStart || isEnd || isTapped || node.isMainLandmark
                        textAlign = android.graphics.Paint.Align.CENTER
                    }

                    val prefix = when {
                        isStart -> "📍 "
                        isEnd -> "🏁 "
                        else -> ""
                    }

                    drawContext.canvas.nativeCanvas.drawText(
                        "$prefix${node.name.take(18)}",
                        pos.x,
                        pos.y + radius + (14f * scale),
                        paint
                    )
                }
            }
        }

        // --- POI CATEGORY MINI-MAP LEGEND OVERLAY ---
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                shape = RoundedCornerShape(12.dp),
                tonalElevation = 4.dp
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { showLegend = !showLegend }
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Map,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Map Legend",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = if (showLegend) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    AnimatedVisibility(visible = showLegend) {
                        Column(
                            modifier = Modifier.padding(top = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            LegendItem(color = Color(0xFF10B981), label = "You Are Here (Start)")
                            LegendItem(color = Color(0xFFEF4444), label = "Destination Pin")
                            LegendItem(color = Color(0xFF3B82F6), label = "Academic Classrooms")
                            LegendItem(color = Color(0xFF8B5CF6), label = "Labs & Workshops")
                            LegendItem(color = Color(0xFFEC4899), label = "Canteen & Food")
                            LegendItem(color = Color(0xFF6366F1), label = "Student Hostels")
                            LegendItem(color = Color(0xFFF59E0B), label = "Admin & Offices")
                            LegendItem(color = Color(0xFF14B8A6), label = "Auditorium & Sports")
                        }
                    }
                }
            }
        }

        // --- HOVER / TAP TOOLTIP CARD OVERLAY ---
        if (tappedNode != null) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                    .fillMaxWidth(0.9f)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Place,
                        contentDescription = null,
                        tint = getCategoryColor(tappedNode!!.category, isDark),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = tappedNode!!.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "${tappedNode!!.buildingName} • ${tappedNode!!.floor}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Button(
                        onClick = {
                            onNodeSelected(tappedNode!!)
                            tappedNode = null
                        },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("Select", fontSize = 12.sp)
                    }
                }
            }
        }

        // Map Control Floating Actions (Zoom in / Zoom out / Recenter)
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SmallFloatingActionButton(
                onClick = { scale = (scale + 0.35f).coerceAtMost(3.5f) },
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.testTag("map_zoom_in")
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Zoom In")
            }

            SmallFloatingActionButton(
                onClick = { scale = (scale - 0.35f).coerceAtLeast(0.6f) },
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.testTag("map_zoom_out")
            ) {
                Icon(Icons.Filled.Remove, contentDescription = "Zoom Out")
            }

            SmallFloatingActionButton(
                onClick = {
                    scale = 1f
                    offsetX = 0f
                    offsetY = 0f
                    tappedNode = null
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("map_recenter")
            ) {
                Icon(Icons.Filled.CenterFocusStrong, contentDescription = "Recenter Map")
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface)
    }
}

private fun drawCampusZones(
    scope: DrawScope,
    mapToCanvas: (Float, Float) -> Offset,
    isDark: Boolean
) {
    // Green Lawns
    val quadPos = mapToCanvas(500f, 500f)
    val grassColor = if (isDark) Color(0xFF0F2D20) else Color(0xFFDCFCE7)
    scope.drawCircle(
        color = grassColor,
        radius = 180f,
        center = quadPos
    )

    val sportsPos = mapToCanvas(500f, 140f)
    scope.drawCircle(
        color = if (isDark) Color(0xFF133827) else Color(0xFFBBF7D0),
        radius = 120f,
        center = sportsPos
    )
}

private fun drawBuildingFootprints(
    scope: DrawScope,
    mapToCanvas: (Float, Float) -> Offset,
    isDark: Boolean
) {
    val bColor = if (isDark) Color(0xFF172B42) else Color(0xFFDBEFEF)
    val strokeColor = if (isDark) Color(0xFF274263) else Color(0xFF94A3B8)

    // Turing CS Block
    val cs = mapToCanvas(720f, 620f)
    scope.drawRoundRect(
        color = bColor,
        topLeft = Offset(cs.x - 45f, cs.y - 30f),
        size = Size(90f, 60f),
        cornerRadius = CornerRadius(8f),
        style = androidx.compose.ui.graphics.drawscope.Fill
    )
    scope.drawRoundRect(
        color = strokeColor,
        topLeft = Offset(cs.x - 45f, cs.y - 30f),
        size = Size(90f, 60f),
        cornerRadius = CornerRadius(8f),
        style = Stroke(width = 2f)
    )

    // Central Library
    val lib = mapToCanvas(280f, 620f)
    scope.drawRoundRect(
        color = bColor,
        topLeft = Offset(lib.x - 50f, lib.y - 35f),
        size = Size(100f, 70f),
        cornerRadius = CornerRadius(10f)
    )

    // Admin Block
    val adm = mapToCanvas(500f, 780f)
    scope.drawRoundRect(
        color = bColor,
        topLeft = Offset(adm.x - 60f, adm.y - 30f),
        size = Size(120f, 60f),
        cornerRadius = CornerRadius(8f)
    )
}

private fun getCategoryColor(category: PoiCategory, isDark: Boolean): Color {
    return when (category) {
        PoiCategory.CLASSROOM -> Color(0xFF3B82F6) // Blue
        PoiCategory.LAB -> Color(0xFF8B5CF6)       // Purple
        PoiCategory.OFFICE -> Color(0xFFF59E0B)    // Amber
        PoiCategory.CANTEEN -> Color(0xFFEC4899)   // Pink
        PoiCategory.LIBRARY -> Color(0xFF10B981)   // Emerald
        PoiCategory.HOSTEL -> Color(0xFF6366F1)    // Indigo
        PoiCategory.AUDITORIUM -> Color(0xFF14B8A6)// Teal
        PoiCategory.PARKING -> Color(0xFF64748B)   // Slate
        PoiCategory.RESTROOM -> Color(0xFF0EA5E9)  // Sky
        PoiCategory.MEDICAL -> Color(0xFFEF4444)   // Red
        PoiCategory.ALL -> Color(0xFF00B4D8)
    }
}

