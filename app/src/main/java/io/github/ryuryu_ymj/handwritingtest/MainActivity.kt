package io.github.ryuryu_ymj.handwritingtest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.ryuryu_ymj.handwritingtest.ui.theme.HandwritingTestTheme

const val TAG = "MyLog"

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      HandwritingTestTheme {
        var drawTouchPoints by remember { mutableStateOf(true) }
        Column(Modifier.fillMaxSize()) {
          Row {
            Option(
                text = "touch points",
                checked = drawTouchPoints,
                onToggle = { drawTouchPoints = !drawTouchPoints })
          }
          Text(text = "text")
          ComposableDrawSurfaceView(drawTouchPoints)
        }
      }
    }
  }
}

@Composable
fun ComposableDrawSurfaceView(drawTouchPoints: Boolean, model: DrawViewModel = viewModel()) {
  AndroidView(
      modifier = Modifier.fillMaxSize(),
      factory = { context -> DrawView(context) },
      // update is called after factory and before surfaceCreated
      update = { view ->
        view.setViewModel(model)
        view.drawTouchPoints = drawTouchPoints
      })
}

@Composable
fun Option(text: String, checked: Boolean, onToggle: (Boolean) -> Unit) {
  Row(Modifier.toggleable(value = checked, role = Role.Checkbox, onValueChange = onToggle)) {
    Text(text, Modifier.weight(1f))
    Checkbox(checked = checked, onCheckedChange = null)
  }
}

@Preview
@Composable
private fun PreviewOption() {
  HandwritingTestTheme { Option(text = "Option", checked = true, onToggle = {}) }
}
