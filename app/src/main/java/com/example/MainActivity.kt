package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.KittNavTab
import com.example.ui.KittViewModel
import com.example.ui.screens.CockpitScreen
import com.example.ui.screens.DiagnosticsScreen
import com.example.ui.screens.MissionLogScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.KittAmber
import com.example.ui.theme.KittBlack
import com.example.ui.theme.KittDarkSurface
import com.example.ui.theme.KittGreen
import com.example.ui.theme.KittRed
import com.example.ui.theme.KittRedDark
import com.example.ui.theme.KittSurfaceVariant
import com.example.ui.theme.KittTextPrimary
import com.example.ui.theme.KittTextSecondary
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  private val viewModel: KittViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        KittMainApp(viewModel = viewModel)
      }
    }
  }
}

@Composable
fun KittMainApp(viewModel: KittViewModel) {
  var currentTab by remember { mutableStateOf(KittNavTab.COCKPIT) }

  Scaffold(
    modifier = Modifier
      .fillMaxSize()
      .background(KittBlack),
    topBar = {
      // Sleek Sci-Fi Top Header
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .windowInsetsPadding(WindowInsets.statusBars)
          .background(KittBlack)
          .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          // Pulsing red indicator
          Box(
            modifier = Modifier
              .size(10.dp)
              .clip(CircleShape)
              .background(KittRed)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "K.I.T.T. 2000",
            color = KittRed,
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 2.sp
          )
        }

        // Guaranteed Free Badge
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0F2618))
            .border(1.dp, KittGreen.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag("free_badge"),
          contentAlignment = Alignment.Center
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(KittGreen)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "%100 ÜCRETSİZ",
              color = KittGreen,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace
            )
          }
        }
      }
    },
    bottomBar = {
      NavigationBar(
        modifier = Modifier
          .fillMaxWidth()
          .windowInsetsPadding(WindowInsets.navigationBars)
          .border(0.5.dp, Color(0x33FF0D34)),
        containerColor = KittDarkSurface,
        tonalElevation = 8.dp
      ) {
        KittNavTab.values().forEach { tab ->
          val isSelected = currentTab == tab
          NavigationBarItem(
            selected = isSelected,
            onClick = { currentTab = tab },
            icon = {
              Icon(
                imageVector = tab.icon,
                contentDescription = tab.title,
                modifier = Modifier.size(20.dp)
              )
            },
            label = {
              Text(
                text = tab.title,
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontFamily = FontFamily.Monospace
              )
            },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = Color.White,
              selectedTextColor = KittRed,
              indicatorColor = KittRedDark,
              unselectedIconColor = KittTextSecondary,
              unselectedTextColor = KittTextSecondary
            ),
            modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
          )
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .background(KittBlack)
    ) {
      when (currentTab) {
        KittNavTab.COCKPIT -> CockpitScreen(viewModel = viewModel)
        KittNavTab.DIAGNOSTICS -> DiagnosticsScreen(viewModel = viewModel)
        KittNavTab.LOGS -> MissionLogScreen(viewModel = viewModel)
        KittNavTab.SETTINGS -> SettingsScreen(viewModel = viewModel)
      }
    }
  }
}

