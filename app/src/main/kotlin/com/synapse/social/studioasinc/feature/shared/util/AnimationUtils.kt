package com.synapse.social.studioasinc.feature.shared.util

import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.dp
import kotlin.random.Random

fun Modifier.liquidSplashEffect(
    trigger: Boolean,
    color: Color,
    onAnimationEnd: () -> Unit = {}
): Modifier = composed {
    val progress = remember { Animatable(0f) }
    val particles = remember { List(12) { SplashParticle() } }

    LaunchedEffect(trigger) {
        if (trigger) {
            progress.snapTo(0f)
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
            )
            onAnimationEnd()
        }
    }

    if (progress.value > 0f && progress.value < 1f) {
        this.drawBehind {
            val center = size.center
            val maxRadius = size.minDimension

            particles.forEach { particle ->
                val p = progress.value
                val angle = particle.angle
                val distance = particle.maxDistance * maxRadius * p
                val x = center.x + kotlin.math.cos(angle) * distance
                val y = center.y + kotlin.math.sin(angle) * distance

                val alpha = 1f - p
                val radius = 2.dp.toPx() * (1f - p) * particle.sizeMult

                drawCircle(
                    color = color.copy(alpha = alpha),
                    radius = radius,
                    center = Offset(x, y)
                )
            }
        }
    } else {
        this
    }
}

private data class SplashParticle(
    val angle: Float = Random.nextFloat() * 2f * kotlin.math.PI.toFloat(),
    val maxDistance: Float = Random.nextFloat() * 0.5f + 0.5f,
    val sizeMult: Float = Random.nextFloat() * 0.5f + 0.5f
)
