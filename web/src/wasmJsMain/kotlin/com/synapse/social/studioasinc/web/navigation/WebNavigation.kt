package com.synapse.social.studioasinc.web.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.synapse.social.studioasinc.web.ui.WebAuthScreen
import com.synapse.social.studioasinc.web.ui.WebFeedScreen
import com.synapse.social.studioasinc.web.ui.WebExploreScreen
import com.synapse.social.studioasinc.web.ui.WebDirectMessagesScreen
import com.synapse.social.studioasinc.web.ui.WebCreatePostScreen
import com.synapse.social.studioasinc.web.ui.WebGroupChatScreen

enum class WebScreen {
    Landing, Login, Feed, Explore, DMs, Notifications, Profile, CreatePost, PostThread, Settings, GroupChat
}

@Composable
fun WebNavigationHost() {
    var currentScreen by remember { mutableStateOf(WebScreen.Landing) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (currentScreen == WebScreen.Landing || currentScreen == WebScreen.Login) {
            when (currentScreen) {
                WebScreen.Landing -> WebPremiumLandingPage(onGetStarted = { currentScreen = WebScreen.Login })
                WebScreen.Login -> WebAuthScreen(onLoginSuccess = { currentScreen = WebScreen.Feed })
                else -> {}
            }
        } else {
            Row(modifier = Modifier.fillMaxSize()) {
                SideNavigationBar(
                    currentScreen = currentScreen,
                    onNavigate = { currentScreen = it }
                )
                Box(modifier = Modifier.fillMaxSize()) {
                    when (currentScreen) {
                        WebScreen.Feed -> WebFeedScreen()
                        WebScreen.Explore -> WebExploreScreen()
                        WebScreen.DMs -> WebDirectMessagesScreen()
                        WebScreen.CreatePost -> WebCreatePostScreen()
                        WebScreen.GroupChat -> WebGroupChatScreen()
                        // Other screens will be added here
                        else -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Screen to be implemented", color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SideNavigationBar(currentScreen: WebScreen, onNavigate: (WebScreen) -> Unit) {
    Column(
        modifier = Modifier
            .width(240.dp)
            .fillMaxHeight()
            .padding(16.dp)
    ) {
        Text(
            text = "⚡ Synapse",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        val navItems = listOf(
            "Home" to WebScreen.Feed,
            "Explore" to WebScreen.Explore,
            "Notifications" to WebScreen.Notifications,
            "Messages" to WebScreen.DMs,
            "Group Chat" to WebScreen.GroupChat,
            "Profile" to WebScreen.Profile,
            "Settings" to WebScreen.Settings
        )

        navItems.forEach { (title, screen) ->
            val isSelected = currentScreen == screen
            val backgroundColor = if (isSelected) MaterialTheme.colorScheme.surfaceContainerHigh else Color.Transparent
            val textColor = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate(screen) }
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = textColor
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onNavigate(WebScreen.CreatePost) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("New Post")
        }
    }
}

@Composable
fun WebPremiumLandingPage(onGetStarted: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Welcome to Synapse Web",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Experience premium social networking right in your browser.",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = onGetStarted) {
                Text("Get Started")
            }
        }
    }
}
