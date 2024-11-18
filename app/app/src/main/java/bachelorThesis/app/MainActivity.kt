package bachelorThesis.app

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import bachelorThesis.app.ui.NavGraphs
import bachelorThesis.app.ui.theme.AppTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MODE_CHANGED)
        setContent {
            AppTheme {
                DestinationsNavHost(navGraph = NavGraphs.root)
            }
        }
    }
}
