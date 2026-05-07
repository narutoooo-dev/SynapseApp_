package com.synapse.social.studioasinc.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.synapse.social.studioasinc.R
import com.synapse.social.studioasinc.feature.shared.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StorageProviderConfigScreen(
    onBackClick: () -> Unit,
    viewModel: StorageProviderConfigViewModel = viewModel()
) {
    val photoProviders by viewModel.photoProviders.collectAsState()
    val videoProviders by viewModel.videoProviders.collectAsState()
    val fileProviders by viewModel.fileProviders.collectAsState()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                title = { Text(stringResource(R.string.settings_storage_providers_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Spacing.Medium),
            verticalArrangement = Arrangement.spacedBy(Spacing.Large),
            contentPadding = PaddingValues(vertical = Spacing.Medium)
        ) {
            item {
                ProviderConfigSection(title = stringResource(R.string.storage_provider_photos)) {
                    val options = listOf(
                        stringResource(R.string.storage_provider_imgbb),
                        stringResource(R.string.storage_provider_cloudinary),
                        stringResource(R.string.storage_provider_supabase)
                    )
                    SettingsFilterChipGroup(
                        options = options,
                        selectedOptions = photoProviders,
                        onCheckedChange = { provider, checked ->
                            viewModel.togglePhotoProvider(provider, checked)
                        }
                    )
                }
            }

            item {
                ProviderConfigSection(title = stringResource(R.string.storage_provider_videos)) {
                    val options = listOf(
                        stringResource(R.string.storage_provider_cloudinary),
                        stringResource(R.string.storage_provider_supabase)
                    )
                    SettingsFilterChipGroup(
                        options = options,
                        selectedOptions = videoProviders,
                        onCheckedChange = { provider, checked ->
                            viewModel.toggleVideoProvider(provider, checked)
                        }
                    )
                }
            }

            item {
                ProviderConfigSection(title = stringResource(R.string.storage_files_audio)) {
                    val options = listOf(
                        stringResource(R.string.storage_provider_supabase),
                        stringResource(R.string.storage_provider_cloudflare_r2)
                    )
                    SettingsFilterChipGroup(
                        options = options,
                        selectedOptions = fileProviders,
                        onCheckedChange = { provider, checked ->
                            viewModel.toggleFileProvider(provider, checked)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProviderConfigSection(
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
private fun SettingsFilterChipGroup(
    options: List<String>,
    selectedOptions: Set<String>,
    onCheckedChange: (String, Boolean) -> Unit
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.Small)
    ) {
        options.forEach { provider ->
            val isSelected = selectedOptions.contains(provider)
            FilterChip(
                selected = isSelected,
                onClick = { onCheckedChange(provider, !isSelected) },
                label = { Text(provider) },
                leadingIcon = if (isSelected) {
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
