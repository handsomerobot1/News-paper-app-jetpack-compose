package com.handsomerobot.newsapp

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// DataStore for saving last site index
val ComponentActivity.dataStore by preferencesDataStore("settings")

class MainActivity : ComponentActivity() {

    private val websites = listOf(
        "https://www.aljazeera.com",
        "https://www.arabnews.com",
        "https://www.middleeastmonitor.com",
        "https://www.islamicnewsdaily.com",
        "https://muslimnews.co.uk"
    )

    private val SITE_INDEX = intPreferencesKey("site_index")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val scope = rememberCoroutineScope()
            var currentIndex by remember { mutableStateOf(0) }

            // Load last site index from DataStore
            LaunchedEffect(Unit) {
                val pref = dataStore.data.first()
                currentIndex = pref[SITE_INDEX] ?: 0
            }

            Scaffold(
                floatingActionButton = {
                    FloatingActionButton(onClick = {
                        currentIndex = (currentIndex + 1) % websites.size
                        scope.launch {
                            dataStore.edit { prefs ->
                                prefs[SITE_INDEX] = currentIndex
                            }
                        }
                    }) {
                        Text("→") // simple icon, you can replace with Icon
                    }
                }
            ) { padding ->
                Box(modifier = Modifier.fillMaxSize()) {
                    WebPage(url = websites[currentIndex])
                }
            }
        }
    }
}

@Composable
fun WebPage(url: String) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                webViewClient = WebViewClient()
                loadUrl(url)
            }
        },
        update = { it.loadUrl(url) },
        modifier = Modifier.fillMaxSize()
    )
}
