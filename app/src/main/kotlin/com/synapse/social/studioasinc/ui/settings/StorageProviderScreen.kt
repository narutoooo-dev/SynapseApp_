package com.synapse.social.studioasinc.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.synapse.social.studioasinc.R
import com.synapse.social.studioasinc.feature.shared.theme.Spacing
import com.synapse.social.studioasinc.shared.domain.model.StorageConfig
import com.synapse.social.studioasinc.shared.domain.model.StorageProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StorageProviderScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val storageConfig by viewModel.storageConfig.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_storage_providers_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(Spacing.Medium),
            verticalArrangement = Arrangement.spacedBy(Spacing.Large)
        ) {
            item {
                StorageSection(title = stringResource(R.string.storage_upload_preferences)) {
                    val isHighQuality = !storageConfig.compressImages
                    ListItem(
                        headlineContent = { Text(stringResource(R.string.storage_high_quality_uploads)) },
                        supportingContent = { Text(stringResource(R.string.storage_high_quality_uploads_desc)) },
                        trailingContent = {
                            Switch(
                                checked = isHighQuality,
                                onCheckedChange = { viewModel.updateCompression(!it) }
                            )
                        }
                    )
                }
            }

            item {
                StorageSection(title = stringResource(R.string.storage_provider_selection)) {
                    ProviderSelectionItem(
                        title = stringResource(R.string.storage_provider_photos),
                        icon = Icons.Default.Image,
                        selectedProvider = storageConfig.photoProvider.toDisplayName(),
                        options = listOf(
                            stringResource(R.string.storage_provider_default),
                            stringResource(R.string.storage_provider_imgbb),
                            stringResource(R.string.storage_provider_cloudinary),
                            stringResource(R.string.storage_provider_supabase),
                            stringResource(R.string.storage_provider_cloudflare_r2)
                        ),
                        onSelect = { viewModel.updatePhotoProvider(it) }
                    )

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                        modifier = Modifier.padding(horizontal = Spacing.Medium)
                    )

                    ProviderSelectionItem(
                        title = stringResource(R.string.storage_provider_videos),
                        icon = Icons.Default.Videocam,
                        selectedProvider = storageConfig.videoProvider.toDisplayName(),
                        options = listOf(
                            stringResource(R.string.storage_provider_default),
                            stringResource(R.string.storage_provider_cloudinary),
                            stringResource(R.string.storage_provider_supabase),
                            stringResource(R.string.storage_provider_cloudflare_r2)
                        ),
                        onSelect = { viewModel.updateVideoProvider(it) }
                    )

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                        modifier = Modifier.padding(horizontal = Spacing.Medium)
                    )

                    ProviderSelectionItem(
                        title = stringResource(R.string.storage_provider_other_files),
                        icon = Icons.Default.CloudUpload,
                        selectedProvider = storageConfig.otherProvider.toDisplayName(),
                        options = listOf(
                            stringResource(R.string.storage_provider_default),
                            stringResource(R.string.storage_provider_supabase),
                            stringResource(R.string.storage_provider_cloudflare_r2),
                            stringResource(R.string.storage_provider_cloudinary)
                        ),
                        onSelect = { viewModel.updateOtherProvider(it) }
                    )
                }
            }

            item {
                Text(
                    text = stringResource(R.string.storage_provider_config),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = Spacing.Small)
                )
            }

            item {
                ProviderConfigCard(
                    title = stringResource(R.string.storage_provider_imgbb),
                    isConfigured = storageConfig.isProviderConfigured(StorageProvider.IMGBB),
                    isExpanded = false,
                    onClearCredentials = { viewModel.clearProviderConfig(StorageProvider.IMGBB) }
                ) {
                    ImgBBConfigContent(
                        apiKey = storageConfig.imgBBKey,
                        onApiKeyChange = { viewModel.updateImgBBConfig(it) }
                    )
                }
            }

            item {
                ProviderConfigCard(
                    title = stringResource(R.string.storage_provider_cloudinary),
                    isConfigured = storageConfig.isProviderConfigured(StorageProvider.CLOUDINARY),
                    isExpanded = false,
                    onClearCredentials = { viewModel.clearProviderConfig(StorageProvider.CLOUDINARY) }
                ) {
                    CloudinaryConfigContent(
                        cloudName = storageConfig.cloudinaryCloudName,
                        apiKey = storageConfig.cloudinaryApiKey,
                        apiSecret = storageConfig.cloudinaryApiSecret,
                        uploadPreset = storageConfig.cloudinaryUploadPreset,
                        onConfigChange = { name, key, secret, preset ->
                            viewModel.updateCloudinaryConfig(name, key, secret, preset)
                        }
                    )
                }
            }

            item {
                ProviderConfigCard(
                    title = stringResource(R.string.storage_supabase_storage),
                    isConfigured = storageConfig.isProviderConfigured(StorageProvider.SUPABASE),
                    isExpanded = false,
                    onClearCredentials = { viewModel.clearProviderConfig(StorageProvider.SUPABASE) }
                ) {
                    SupabaseConfigContent(
                        url = storageConfig.supabaseUrl,
                        apiKey = storageConfig.supabaseKey,
                        bucketName = storageConfig.supabaseBucket,
                        onConfigChange = { url, key, bucket ->
                            viewModel.updateSupabaseConfig(url, key, bucket)
                        }
                    )
                }
            }

            item {
                ProviderConfigCard(
                    title = stringResource(R.string.storage_provider_cloudflare_r2),
                    isConfigured = storageConfig.isProviderConfigured(StorageProvider.CLOUDFLARE_R2),
                    isExpanded = false,
                    onClearCredentials = { viewModel.clearProviderConfig(StorageProvider.CLOUDFLARE_R2) }
                ) {
                    R2ConfigContent(
                        accountId = storageConfig.r2AccountId,
                        accessKeyId = storageConfig.r2AccessKeyId,
                        secretAccessKey = storageConfig.r2SecretAccessKey,
                        bucketName = storageConfig.r2BucketName,
                        onConfigChange = { accId, accKey, secret, bucket ->
                            viewModel.updateR2Config(accId, accKey, secret, bucket)
                        }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(Spacing.ExtraLarge))
            }
        }
    }
}

@Composable
private fun StorageProvider.toDisplayName(): String {
    return when (this) {
        StorageProvider.DEFAULT -> stringResource(R.string.storage_provider_default)
        StorageProvider.IMGBB -> stringResource(R.string.storage_provider_imgbb)
        StorageProvider.CLOUDINARY -> stringResource(R.string.storage_provider_cloudinary)
        StorageProvider.SUPABASE -> stringResource(R.string.storage_provider_supabase)
        StorageProvider.CLOUDFLARE_R2 -> stringResource(R.string.storage_provider_cloudflare_r2)
    }
}

@Composable
private fun StorageSection(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(Spacing.Medium),
            verticalArrangement = Arrangement.spacedBy(Spacing.SmallMedium)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            content()
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ProviderSelectionItem(
    title: String,
    icon: ImageVector,
    selectedProvider: String,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        ListItem(
            headlineContent = { Text(title) },
            supportingContent = { Text(selectedProvider) },
            leadingContent = {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            trailingContent = {
                val rotationAngle by animateFloatAsState(
                    targetValue = if (expanded) 180f else 0f,
                    animationSpec = tween(300, easing = EaseOutCubic),
                    label = "rotation"
                )
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = if (expanded) stringResource(R.string.collapse_options) else stringResource(R.string.expand_options),
                    modifier = Modifier.rotate(rotationAngle)
                )
            },
            modifier = Modifier.clickable { expanded = !expanded }
        )

        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ) + fadeIn(tween(220)),
            exit = shrinkVertically(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ) + fadeOut(tween(180))
        ) {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.Medium, vertical = Spacing.Small),
                horizontalArrangement = Arrangement.spacedBy(Spacing.Small)
            ) {
                options.forEach { option ->
                    FilterChip(
                        selected = option == selectedProvider,
                        onClick = {
                            onSelect(option)
                            expanded = false
                        },
                        label = { Text(option) },
                        leadingIcon = if (option == selectedProvider) {
                            {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                                )
                            }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            selectedLeadingIconColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun ProviderConfigCard(
    title: String,
    isConfigured: Boolean,
    isExpanded: Boolean,
    onClearCredentials: () -> Unit,
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(isExpanded) }

    ElevatedCard(
        shape = MaterialTheme.shapes.large,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            ListItem(
                headlineContent = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                supportingContent = {
                    Text(
                        text = if (isConfigured) stringResource(R.string.storage_ready_to_use) else stringResource(R.string.storage_configuration_required),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isConfigured) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.error
                        }
                    )
                },
                leadingContent = {
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = if (isConfigured) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.errorContainer,
                        contentColor = if (isConfigured) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onErrorContainer
                    ) {
                        Icon(
                            imageVector = if (isConfigured) Icons.Default.CheckCircle else Icons.Outlined.Key,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(Spacing.Small)
                                .size(Spacing.MediumLarge)
                        )
                    }
                },
                trailingContent = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isConfigured) {
                            IconButton(onClick = onClearCredentials) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = stringResource(R.string.remove_credentials, title),
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }

                        val rotationAngle by animateFloatAsState(
                            targetValue = if (expanded) 180f else 0f,
                            animationSpec = tween(300, easing = EaseOutCubic),
                            label = "rotation"
                        )

                        IconButton(onClick = { expanded = !expanded }) {
                            Icon(
                                imageVector = Icons.Default.ExpandMore,
                                contentDescription = if (expanded) stringResource(R.string.collapse_configuration, title) else stringResource(R.string.expand_configuration, title),
                                modifier = Modifier.rotate(rotationAngle)
                            )
                        }
                    }
                },
                modifier = Modifier.clickable { expanded = !expanded }
            )

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) + fadeIn(tween(220)),
                exit = shrinkVertically(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) + fadeOut(tween(180))
            ) {
                Column(modifier = Modifier.padding(start = Spacing.Large, end = Spacing.Large, bottom = Spacing.Large)) {
                    HorizontalDivider(
                        modifier = Modifier.padding(bottom = Spacing.Medium),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                    content()
                }
            }
        }
    }
}

private val EaseOutCubic = CubicBezierEasing(0.33f, 1f, 0.68f, 1f)
