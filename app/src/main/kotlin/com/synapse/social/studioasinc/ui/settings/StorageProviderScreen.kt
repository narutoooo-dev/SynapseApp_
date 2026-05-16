package com.synapse.social.studioasinc.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.synapse.social.studioasinc.R
import com.synapse.social.studioasinc.shared.domain.model.StorageProvider
import com.synapse.social.studioasinc.feature.shared.theme.Spacing
import androidx.compose.animation.core.CubicBezierEasing

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun StorageProviderScreen(
    navController: NavController,
    viewModel: SettingsViewModel
) {
    val storageConfig by viewModel.storageConfig.collectAsState()
    val isHighQuality = storageConfig.compressImages.not()

    // Pre-resolve strings for provider mapping to avoid Composable calls in lambdas
    val imgbbName = stringResource(R.string.storage_provider_imgbb)
    val cloudinaryName = stringResource(R.string.storage_provider_cloudinary)
    val supabaseName = stringResource(R.string.storage_provider_supabase)
    val cloudflareR2Name = stringResource(R.string.storage_provider_cloudflare_r2)

    Scaffold(
        containerColor = SettingsColors.screenBackground,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_storage_providers_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(SettingsSpacing.screenPadding),
            verticalArrangement = Arrangement.spacedBy(Spacing.ExtraLarge)
        ) {
            // Preferences Section
            item {
                StorageSection(title = stringResource(R.string.storage_upload_preferences)) {
                    ListItem(
                        headlineContent = { Text(stringResource(R.string.storage_high_quality_uploads)) },
                        supportingContent = {
                            Text(
                                text = stringResource(R.string.storage_high_quality_uploads_desc),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        trailingContent = {
                            Switch(
                                checked = isHighQuality,
                                onCheckedChange = { viewModel.updateCompression(!it) }
                            )
                        }
                    )
                }
            }

            // Selection Section
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
                        isConfigured = { option ->
                            val provider = when (option) {
                                imgbbName -> StorageProvider.IMGBB
                                cloudinaryName -> StorageProvider.CLOUDINARY
                                supabaseName -> StorageProvider.SUPABASE
                                cloudflareR2Name -> StorageProvider.CLOUDFLARE_R2
                                else -> StorageProvider.DEFAULT
                            }
                            storageConfig.isProviderConfigured(provider)
                        },
                        onSelect = { viewModel.updatePhotoProvider(it) }
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
                        isConfigured = { option ->
                            val provider = when (option) {
                                imgbbName -> StorageProvider.IMGBB
                                cloudinaryName -> StorageProvider.CLOUDINARY
                                supabaseName -> StorageProvider.SUPABASE
                                cloudflareR2Name -> StorageProvider.CLOUDFLARE_R2
                                else -> StorageProvider.DEFAULT
                            }
                            storageConfig.isProviderConfigured(provider)
                        },
                        onSelect = { viewModel.updateVideoProvider(it) }
                    )

                    ProviderSelectionItem(
                        title = stringResource(R.string.storage_provider_other_files),
                        icon = Icons.Default.Storage,
                        selectedProvider = storageConfig.otherProvider.toDisplayName(),
                        options = listOf(
                            stringResource(R.string.storage_provider_default),
                            stringResource(R.string.storage_provider_supabase),
                            stringResource(R.string.storage_provider_cloudflare_r2),
                            stringResource(R.string.storage_provider_cloudinary)
                        ),
                        isConfigured = { option ->
                            val provider = when (option) {
                                imgbbName -> StorageProvider.IMGBB
                                cloudinaryName -> StorageProvider.CLOUDINARY
                                supabaseName -> StorageProvider.SUPABASE
                                cloudflareR2Name -> StorageProvider.CLOUDFLARE_R2
                                else -> StorageProvider.DEFAULT
                            }
                            storageConfig.isProviderConfigured(provider)
                        },
                        onSelect = { viewModel.updateOtherProvider(it) }
                    )
                }
            }

            // Configuration Section
            item {
                Text(
                    text = stringResource(R.string.storage_provider_config),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = Spacing.Medium)
                )
            }

            item {
                ProviderConfigCard(
                    title = stringResource(R.string.storage_provider_imgbb),
                    isConfigured = storageConfig.isProviderConfigured(StorageProvider.IMGBB),
                    isExpanded = !storageConfig.isProviderConfigured(StorageProvider.IMGBB),
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
                    title = stringResource(R.string.storage_provider_supabase),
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
                        onConfigChange = { account, access, secret, bucket ->
                            viewModel.updateR2Config(account, access, secret, bucket)
                        }
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
    isConfigured: (String) -> Boolean,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var showConfigPrompt by remember { mutableStateOf<String?>(null) }

    if (showConfigPrompt != null) {
        AlertDialog(
            onDismissRequest = { showConfigPrompt = null },
            title = { Text(stringResource(R.string.storage_config_required_title)) },
            text = { Text(stringResource(R.string.storage_config_required_message)) },
            confirmButton = {
                TextButton(onClick = { showConfigPrompt = null }) {
                    Text(stringResource(R.string.action_ok))
                }
            }
        )
    }

    Column {
        ListItem(
            headlineContent = { Text(title) },
            supportingContent = {
                Text(
                    text = selectedProvider,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = Spacing.Medium, vertical = Spacing.Small),
                horizontalArrangement = Arrangement.spacedBy(Spacing.Small)
            ) {
                options.forEach { option ->
                    val configured = isConfigured(option)
                    FilterChip(
                        selected = option == selectedProvider,
                        onClick = {
                            if (configured) {
                                onSelect(option)
                                expanded = false
                            } else {
                                showConfigPrompt = option
                            }
                        },
                        label = {
                            Text(
                                text = option,
                                modifier = Modifier.alpha(if (configured) 1f else 0.5f)
                            )
                        },
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
                        color = if (isConfigured) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                },
                leadingContent = {
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = if (isConfigured) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (isConfigured) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
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
                                    imageVector = Icons.Default.Delete,
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
