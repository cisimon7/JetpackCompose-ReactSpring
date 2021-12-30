@file:Suppress("FunctionName")

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.desktop.Window
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.absoluteValue

/* Number of images minus 1 */
const val imageCount = 7

fun ViewPager() = Window(title = "ViewPager", size = IntSize(700, 700)) {
    Column(modifier = Modifier.fillMaxSize().padding(10.dp)) {
        val imgWidth = 858f

        val interpolator = Interpolate(0f to imgWidth / 2, 1f to 0.7f)
        val offset = remember { Animatable(Offset(0f, 0f), Offset.VectorConverter) }
        val scaleAnime = remember { Animatable(1f, Float.VectorConverter) }
        var scaleDist by remember { mutableStateOf(0f) }

        val onDragGesture: suspend (Offset) -> Unit = { dragOffset: Offset ->
            offset.snapTo(offset.value + dragOffset)
            scaleDist += dragOffset.x
            scaleAnime.snapTo(interpolator.interpolateBetween(abs(scaleDist)))
        }

        val onDragEnd = suspend {
            /* Launch differently so as they can run concurrently */
            coroutineScope {
                launch { scaleAnime.animateTo(1f, animationSpec = tween(durationMillis = 300)) }
                launch {
                    offset.animateTo(
                        /* use coerce to make sure it's withing bounds of number of images */
                        Offset((offset.value.x).roundTo(imgWidth).coerceIn(-imageCount * imgWidth, 0F), 0f),
                        animationSpec = tween(durationMillis = 300)
                    )
                }
            }
            scaleDist = 0f
        }

        Box(modifier = Modifier.fillMaxWidth().weight(0.95f)) {
            (0..imageCount).forEach { idx ->
                getImage(
                    index = idx,
                    scale = scaleAnime.value.absoluteValue,
                    offset = Offset(offset.value.x, 0f) + Offset(idx * imgWidth, 0f),
                    onDragGesture = onDragGesture,
                    onDragEnd = onDragEnd
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().weight(0.05f)
                .background(Color.Transparent),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom,
            content = {
                (0..imageCount).forEach { idx ->
                    val isFocus = (idx == offset.value.x.div(imgWidth).toInt().absoluteValue)
                    Icon(
                        IconCircle,
                        contentDescription = "",
                        /* using graphics layer since each Icon is independent of other composables */
                        modifier = Modifier.padding(5.dp)
                            .graphicsLayer {
                                this.alpha = if (isFocus) 1f else 0.5f
                                scaleX = if (isFocus) 0.5f else 0.3f
                                scaleY = if (isFocus) 0.5f else 0.3f
                            }.pointerInput(Unit) {
                                coroutineScope {
                                    detectTapGestures {
                                        launch {
                                            offset.animateTo(Offset(-imgWidth * idx, 0F), animationSpec = tween(300))
                                        }
                                    }
                                }
                            },
                        tint = Color.Black
                    )
                }
            }
        )
    }
}

@Composable
private fun getImage(
    index: Int,
    scale: Float,
    offset: Offset,
    onDragGesture: suspend (Offset) -> Unit,
    onDragEnd: suspend () -> Unit
) {
    Box(modifier = Modifier.size(700.dp, 700.dp)) {
        Image(
            bitmap = imageResource("pictures/viewpager/image$index.jpg"),
            contentDescription = "image$index",
            modifier = Modifier.scale(scale)
                .offset { offset.round() }
                .clip(RoundedCornerShape(20.dp))
                .pointerInput(Unit) {
                    coroutineScope {
                        detectDragGestures(
                            onDrag = { _, dragAmount ->
                                launch { onDragGesture(dragAmount) }
                            },
                            onDragEnd = { launch { onDragEnd() } }
                        )
                    }
                },
            contentScale = ContentScale.Crop
        )
    }
}

class Interpolate(private val rangeFrom: Pair<Float, Float>, private val rangeTo: Pair<Float, Float>) {
    fun interpolateBetween(value: Float): Float {
        return when {
            value <= rangeFrom.first -> rangeTo.first
            value >= rangeFrom.second -> rangeTo.second
            else -> ((rangeTo.second - rangeTo.first) * (value - rangeFrom.first) / (rangeFrom.second - rangeFrom.first)) + rangeTo.first
        }
    }
}

fun Float.roundTo(period: Float): Float {
    /**
     * Round to nearest multiple of [period]*/
    val left2Right = this.mod(period)
    val right2Left = -this.mod(-period)

    return when (left2Right >= right2Left) {
        true -> this + right2Left
        false -> this - left2Right
    }
}

private val IconCircle: ImageVector
    get() {
        return materialIcon(name = "Filled.Circle") {
            materialPath {
                moveTo(12.0f, 2.0f)
                curveTo(6.48f, 2.0f, 2.0f, 6.48f, 2.0f, 12.0f)
                reflectiveCurveToRelative(4.48f, 10.0f, 10.0f, 10.0f)
                reflectiveCurveToRelative(10.0f, -4.48f, 10.0f, -10.0f)
                reflectiveCurveTo(17.52f, 2.0f, 12.0f, 2.0f)
                close()
            }
        }

    }