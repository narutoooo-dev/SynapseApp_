package com.synapse.social.studioasinc.core.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.overscroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.synapse.social.studioasinc.core.ui.animation.rememberElasticOverscrollEffect

/**
 * A container that applies an elastic rubber-banding effect when its content is pulled
 * beyond its scroll boundaries.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ElasticContainer(
    modifier: Modifier = Modifier,
    content: @Composable (Modifier) -> Unit
) {
    val overscrollEffect = rememberElasticOverscrollEffect()

    Box(
        modifier = modifier
            .overscroll(overscrollEffect)
    ) {
        content(overscrollEffect.distortionModifier)
    }
}
