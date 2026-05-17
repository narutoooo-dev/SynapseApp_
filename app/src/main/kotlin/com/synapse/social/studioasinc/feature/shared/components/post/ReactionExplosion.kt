package com.synapse.social.studioasinc.feature.shared.components.post

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.synapse.social.studioasinc.domain.model.ReactionType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun ReactionExplosion(
    reaction: ReactionType,
    onAnimationEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    val particles = remember { List(10) { ReactionParticle(reaction.emoji) } }

    Box(
        modifier = modifier.size(48.dp),
        contentAlignment = Alignment.Center
    ) {
        particles.forEach { particle ->
            AnimatedParticle(particle)
        }
    }

    LaunchedEffect(Unit) {
        delay(1000)
        onAnimationEnd()
    }
}

private data class ReactionParticle(
    val emoji: String,
    val angle: Float = Random.nextFloat() * 360f,
    val distance: Float = Random.nextFloat() * 40f + 20f,
    val rotation: Float = Random.nextFloat() * 360f
)

@Composable
private fun AnimatedParticle(particle: ReactionParticle) {
    val animatable = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        animatable.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    val progress = animatable.value
    val x = (kotlin.math.cos(Math.toRadians(particle.angle.toDouble())) * particle.distance * progress).toFloat()
    val y = (kotlin.math.sin(Math.toRadians(particle.angle.toDouble())) * particle.distance * progress).toFloat()
    val scale = (1f - progress * 0.5f) * progress
    val alpha = 1f - progress

    Text(
        text = particle.emoji,
        fontSize = 24.sp,
        modifier = Modifier
            .offset(x = x.dp, y = y.dp)
            .scale(scale)
            .rotate(particle.rotation * progress)
            .alpha(alpha)
    )
}
