package com.synapse.social.studioasinc.core.ui.animation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.OverscrollEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.sign

@OptIn(ExperimentalFoundationApi::class)
class ElasticOverscrollEffect(
    private val scope: CoroutineScope
) : OverscrollEffect {
    private val overscrollAnimatable = Animatable(0f)
    private var overscrollDisplayValue by mutableFloatStateOf(0f)

    override fun applyToScroll(
        delta: Offset,
        source: NestedScrollSource,
        performScroll: (Offset) -> Offset
    ): Offset {
        val verticalDelta = delta.y
        val currentOverscroll = overscrollDisplayValue

        if (currentOverscroll != 0f && source == NestedScrollSource.UserInput) {
            val newOverscroll = currentOverscroll + verticalDelta * 0.4f

            if (sign(newOverscroll) != sign(currentOverscroll) && newOverscroll != 0f) {
                overscrollDisplayValue = 0f
                val usedDelta = (0f - currentOverscroll) / 0.4f
                val remainingDelta = verticalDelta - usedDelta
                return Offset(0f, usedDelta) + performScroll(Offset(0f, remainingDelta))
            } else {
                overscrollDisplayValue = newOverscroll
                return delta
            }
        }

        val consumed = performScroll(delta)
        val remainder = verticalDelta - consumed.y

        if (abs(remainder) > 0.5f && source == NestedScrollSource.UserInput) {
            overscrollDisplayValue = currentOverscroll + remainder * 0.4f
            return delta
        }

        return consumed
    }

    override suspend fun applyToFling(
        velocity: Velocity,
        performFling: suspend (Velocity) -> Velocity
    ) {
        overscrollAnimatable.snapTo(overscrollDisplayValue)

        if (overscrollAnimatable.value != 0f) {
            overscrollAnimatable.animateTo(
                targetValue = 0f,
                animationSpec = spring(
                    dampingRatio = 0.4f,
                    stiffness = Spring.StiffnessHigh
                )
            ) {
                overscrollDisplayValue = value
            }
        } else {
            performFling(velocity)
        }
    }

    override val node: Modifier.Node = object : Modifier.Node() {}

    // Rename to avoid conflict with supertype
    val distortionModifier: Modifier = Modifier.graphicsLayer {
        val overscroll = overscrollDisplayValue
        if (overscroll == 0f) return@graphicsLayer

        translationY = overscroll

        val stretchFactor = (abs(overscroll) / 1000f).coerceAtMost(0.15f)
        scaleY = 1f + stretchFactor
        scaleX = 1f - (stretchFactor * 0.5f)

        transformOrigin = if (overscroll > 0) {
            TransformOrigin(0.5f, 0f)
        } else {
            TransformOrigin(0.5f, 1f)
        }
    }

    override val isInProgress: Boolean
        get() = overscrollDisplayValue != 0f
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun rememberElasticOverscrollEffect(): ElasticOverscrollEffect {
    val scope = rememberCoroutineScope()
    return remember(scope) { ElasticOverscrollEffect(scope) }
}
