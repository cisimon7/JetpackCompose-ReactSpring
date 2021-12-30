@file:Suppress("FunctionName")

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.desktop.Window
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.DefaultStrokeLineWidth
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.*

const val scale = 2.0 / 3.0
fun BallSpring() = Window(title = "React-Spring Ball", size = IntSize(700, 700)) {
    Box(Modifier.drawBackground(Color(0xFF3F3747), Color(0xFF494646), 30)) {
        val anim = remember { Animatable(Offset(0f, 0f), Offset.VectorConverter) }
        val center = Offset(425f, 420f)
        val animateToCenter = suspend {
            anim.animateTo(
                targetValue = Offset(0f, 0f),
                animationSpec = spring(
                    dampingRatio = 0.04f,
                    stiffness = 200f
                )
            )
        }
        Box(
            Modifier
                .fillMaxSize()
                .drawSpringBall(anim.value, center = center).align(Alignment.BottomEnd)
                .pointerInput(Unit) {
                    coroutineScope {
                        launch {
                            detectDragGestures(
                                onDragEnd = { launch { animateToCenter() } },
                                onDrag = { _, dragAmount ->
                                    when {
                                        euclideanDistance(anim.value + dragAmount) <= scale * 100 -> {
                                            launch { anim.snapTo(anim.value + dragAmount) }
                                        }
                                        else -> {
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
        )
    }
}

private fun Modifier.drawSpringBall(ballOffset: Offset, center: Offset): Modifier = drawWithContent {
    drawCircle(
        brush = Brush.radialGradient(
            0.0f to Color.White,
            0.9f to Color(0xFFFA8072),
            center = ballOffset + center + Offset(-30f, -30f),
            radius = (scale * 100f).toFloat(),
        ),
        radius = (scale * 100f).toFloat(),
        center = ballOffset + center
    )
}

private fun Modifier.drawBackground(backgroundColor: Color, gridColor: Color, gridStep: Int): Modifier =
    this.background(color = backgroundColor)
        .drawBehind {
            (0..size.width.toInt() step gridStep).forEach { idx ->
                drawLine(
                    color = gridColor,
                    start = Offset(idx.toFloat(), 0f),
                    end = Offset(idx.toFloat(), size.height)
                )
            }
            (0..size.height.toInt() step gridStep).forEach { idx ->
                drawLine(
                    color = gridColor,
                    start = Offset(0f, idx.toFloat()),
                    end = Offset(size.width, idx.toFloat())
                )
            }
            drawCircle(color = Color.White, radius = (scale * 300f).toFloat(), center = center)
            drawCircle(
                color = Color.Blue,
                radius = (scale * 200f).toFloat(),
                center = center,
                style = Stroke(width = Stroke.HairlineWidth)
            )
        }

fun euclideanDistance(offset: Offset): Float {
    val (x, y) = offset
    return sqrt(x.pow(2) + y.pow(2))
}