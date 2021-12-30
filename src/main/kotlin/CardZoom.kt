@file:Suppress("FunctionName")

import androidx.compose.desktop.Window
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import kotlinx.coroutines.coroutineScope

fun CardZoom() = Window(title = "ViewPager", size = IntSize(700, 700)) {
    Box(modifier = Modifier.fillMaxSize()) {
        var panState by remember { mutableStateOf(Offset(0F, 0F)) }
        var zoomState by remember { mutableStateOf(1F) }
        var rotationState by remember { mutableStateOf(0F) }

        Box(
            modifier = Modifier.size(350.dp).align(Alignment.Center).scale(zoomState).rotate(rotationState)
                .offset { panState.round() }
                .background(color = Color.Black, shape = RoundedCornerShape(10.dp))
                .pointerInput(Unit) {
                    coroutineScope {
                        detectTransformGestures { centroid, pan, zoom, rotation ->
                            panState += pan
                            zoomState = zoom
                            rotationState = rotation
                        }
                    }
                },
            content = {
                Image(
                    bitmap = imageResource("pictures/cardzoom/card.jpg"),
                    contentDescription = "Card image",
                    modifier = Modifier.padding(10.dp)
                        .background(Color.Transparent, shape = RoundedCornerShape(5.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        )
    }
}