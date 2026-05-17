package com.synapse.social.studioasinc.ui.createpost

import android.net.Uri
import androidx.compose.foundation.layout.*
import com.synapse.social.studioasinc.feature.shared.components.LinkPreviewCard
import com.synapse.social.studioasinc.feature.shared.components.LinkPreviewViewModel
import com.synapse.social.studioasinc.feature.shared.utils.UrlUtils
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.material3.TextButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.clickable

import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.outlined.Image
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.height
import com.synapse.social.studioasinc.R
import com.synapse.social.studioasinc.ui.createpost.CreatePostSearchUiState
import com.synapse.social.studioasinc.feature.shared.theme.Spacing
import com.synapse.social.studioasinc.feature.shared.theme.Sizes

@Composable
fun TimeCapsuleToggle(
    isTimeCapsule: Boolean,
    unlocksAt: Long?,
    onToggle: (Boolean) -> Unit,
    onSelectUnlockDate: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(Spacing.Medium)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.time_capsule),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = stringResource(R.string.time_capsule_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = isTimeCapsule,
                    onCheckedChange = onToggle
                )
            }

            if (isTimeCapsule) {
                Spacer(modifier = Modifier.height(Spacing.Small))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectUnlockDate() }
                        .padding(vertical = Spacing.Small),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.unlock_date),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = if (unlocksAt != null) {
                            java.text.DateFormat.getDateTimeInstance().format(java.util.Date(unlocksAt))
                        } else {
                            "Select date"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun CreatePostContent(
    linkPreviewViewModel: LinkPreviewViewModel = hiltViewModel(),
    uiState: CreatePostUiState,
    searchUiState: CreatePostSearchUiState,
    focusRequester: FocusRequester,
    focusManager: FocusManager,
    padding: PaddingValues,
    onPrivacyClick: () -> Unit,
    onUpdateText: (String) -> Unit,
    onRemoveMedia: (Int) -> Unit,
    onEditMedia: (Int) -> Unit,
    onRemovePoll: () -> Unit,
    onRemoveYoutube: () -> Unit,
    onRemoveLocation: () -> Unit,
    onAddThreadPost: () -> Unit,
    onUpdateThreadText: (Int, String) -> Unit,
    onRemoveThreadMedia: (Int, Int) -> Unit,
    onEditThreadMedia: (Int, Int) -> Unit,
    onAddThreadMedia: (Int) -> Unit,
    onTimeCapsuleToggle: (Boolean) -> Unit,
    onSelectUnlockDate: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentPadding = PaddingValues(horizontal = Spacing.Medium, vertical = Spacing.Small),
        verticalArrangement = Arrangement.spacedBy(Spacing.Medium)
    ) {

        item {
            UserHeader(
                user = uiState.currentUserProfile,
                privacy = uiState.privacy,
                onPrivacyClick = onPrivacyClick,
                taggedPeople = uiState.taggedPeople,
                feeling = uiState.feeling,
                location = uiState.location
            )
        }


        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.Transparent
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = Sizes.Height120),
                    contentAlignment = Alignment.TopStart
                ) {
                    if (uiState.postText.isEmpty()) {
                        Text(
                            text = stringResource(R.string.hint_whats_on_your_mind),
                            style = if (uiState.mediaItems.isEmpty()) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }


                    var dismissedPreviewUrl by remember { mutableStateOf<String?>(null) }
    val textSize = if (uiState.postText.length < 80 && uiState.mediaItems.isEmpty() && uiState.pollData == null) {
                        MaterialTheme.typography.headlineSmall
                    } else {
                        MaterialTheme.typography.bodyLarge
                    }

                    BasicTextField(
                        value = uiState.postText,
                        onValueChange = onUpdateText,
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        textStyle = textSize.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = textSize.lineHeight * 1.2,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Start
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                    )

                    val firstUrl = UrlUtils.extractFirstUrl(uiState.postText)
                    if (firstUrl != null && firstUrl != dismissedPreviewUrl && uiState.mediaItems.isEmpty()) {
                        Spacer(modifier = Modifier.height(Spacing.Small))
                        LinkPreviewCard(
                            url = firstUrl,
                            useCase = linkPreviewViewModel.getLinkMetadataUseCase,
                            onRemove = { dismissedPreviewUrl = firstUrl },
                            modifier = Modifier.padding(top = Spacing.Small)
                        )
                    }
                }
            }
        }

        item {
            TimeCapsuleToggle(
                isTimeCapsule = uiState.isTimeCapsule,
                unlocksAt = uiState.unlocksAt,
                onToggle = onTimeCapsuleToggle,
                onSelectUnlockDate = onSelectUnlockDate
            )
        }



        item {

             if (uiState.mediaItems.isNotEmpty()) {
                 MediaPreviewGrid(
                     mediaItems = uiState.mediaItems,
                     onRemove = onRemoveMedia,
                     onEdit = onEditMedia
                 )
             }


             uiState.pollData?.let { poll ->
                 PollPreviewCard(poll = poll, onDelete = onRemovePoll)
             }


             uiState.youtubeUrl?.let { url ->
                  YoutubePreviewCard(url = url, onDelete = onRemoveYoutube)
             }


             uiState.location?.let { loc ->
                 LocationPreviewCard(location = loc, onDelete = onRemoveLocation)
             }
        }


        itemsIndexed(uiState.threadPosts) { index, threadPost ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = Spacing.Medium).height(IntrinsicSize.Min)
            ) {
                // Thread connector visual
                Box(
                    modifier = Modifier
                        .padding(end = Spacing.Medium)
                        .width(Spacing.Tiny)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))
                )

                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.Transparent
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = Sizes.HeightLarge),
                            contentAlignment = Alignment.TopStart
                        ) {
                            if (threadPost.postText.isEmpty()) {
                                Text(
                                    text = stringResource(R.string.hint_add_another_post),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }

                            BasicTextField(
                                value = threadPost.postText,
                                onValueChange = { onUpdateThreadText(index, it) },
                                modifier = Modifier
                                    .fillMaxWidth(),
                                textStyle = MaterialTheme.typography.bodyLarge.copy(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.2,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Start
                                ),
                                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default),
                                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                            )
                        }
                    }

                    if (threadPost.mediaItems.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(Spacing.Small))
                        MediaPreviewGrid(
                            mediaItems = threadPost.mediaItems,
                            onRemove = { mediaIndex -> onRemoveThreadMedia(index, mediaIndex) },
                            onEdit = { mediaIndex -> onEditThreadMedia(index, mediaIndex) }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        IconButton(onClick = { onAddThreadMedia(index) }) {
                            Icon(
                                imageVector = Icons.Outlined.Image,
                                contentDescription = "Add Media",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        item {
            Box(
                modifier = Modifier.fillMaxWidth().padding(top = Spacing.Medium),
                contentAlignment = Alignment.CenterStart
            ) {
                TextButton(onClick = onAddThreadPost) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add to thread",
                        modifier = Modifier.padding(end = Spacing.Small)
                    )
                    Text(stringResource(R.string.action_add_to_thread))
                }
            }
        }




        item {
            Spacer(modifier = Modifier.height(Sizes.Height100))
        }
    }
}
