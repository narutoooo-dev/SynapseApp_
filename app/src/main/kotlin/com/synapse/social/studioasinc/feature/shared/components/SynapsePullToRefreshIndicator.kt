package com.synapse.social.studioasinc.feature.shared.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SynapsePullToRefreshIndicator(
    state: PullToRefreshState,
    isRefreshing: Boolean,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
    val infiniteTransition = rememberInfiniteTransition(label = "synapseRefresh")
    val sparkOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sparkOffset"
    )

    val path = remember { Path() }
    val pathMeasure = remember { androidx.compose.ui.graphics.PathMeasure() }

    Box(
        modifier = modifier
            .size(60.dp)
            .graphicsLayer {
                val progress = state.distanceFraction
                scaleX = min(1.2f, progress)
                scaleY = min(1.2f, progress)
                alpha = min(1f, progress)
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(40.dp)) {
            val w = size.width
            val h = size.height
            val progress = state.distanceFraction.coerceIn(0f, 1f)

            path.reset()
            path.moveTo(w * 0.2f, h * 0.5f)
            path.quadraticTo(w * 0.4f, h * 0.2f, w * 0.5f, h * 0.5f)
            path.quadraticTo(w * 0.6f, h * 0.8f, w * 0.8f, h * 0.5f)

            drawPath(
                path = path,
                color = color.copy(alpha = 0.3f),
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            pathMeasure.setPath(path, false)
            val pathLength = pathMeasure.length

            if (isRefreshing) {
                drawPath(
                    path = path,
                    color = color,
                    style = Stroke(
                        width = 3.dp.toPx(),
                        cap = StrokeCap.Round,
                        pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                            intervals = floatArrayOf(pathLength * 0.2f, pathLength),
                            phase = -sparkOffset * pathLength
                        )
                    )
                )
            } else {
                drawPath(
                    path = path,
                    color = color,
                    style = Stroke(
                        width = 3.dp.toPx(),
                        cap = StrokeCap.Round,
                        pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                            intervals = floatArrayOf(pathLength * progress, pathLength),
                            phase = 0f
                        )
                    )
                )
            }

            // Draw spark
            if (isRefreshing) {
                val pos = pathMeasure.getPosition(sparkOffset * pathLength)
                drawCircle(
                    color = color,
                    radius = 4.dp.toPx(),
                    center = pos
                )
                drawCircle(
                    color = color.copy(alpha = 0.4f),
                    radius = 8.dp.toPx(),
                    center = pos
                )
            }
        }
    }
}
