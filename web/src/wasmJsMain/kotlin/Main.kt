import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.CanvasBasedWindow
import com.synapse.social.studioasinc.shared.di.storageModule
import com.synapse.social.studioasinc.web.di.webModule
import com.synapse.social.studioasinc.web.navigation.WebNavigationHost
import com.synapse.social.studioasinc.web.theme.WebTheme
import org.koin.core.context.startKoin

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    startKoin {
        modules(storageModule, webModule)
    }

    CanvasBasedWindow(canvasElementId = "ComposeTarget") {
        WebTheme {
            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                WebNavigationHost()
            }
        }
    }
}
