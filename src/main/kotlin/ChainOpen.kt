@file:Suppress("FunctionName")

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.desktop.Window
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import theme.fancyColors

const val count = 4

fun ChainOpen() = Window(title = "React-Spring Chain Opening", size = IntSize(900, 500)) {
    var openContent by remember { mutableStateOf(false) }
    val containerSize by remember { mutableStateOf(Animatable(Size(850f, 425f), Size.VectorConverter)) }
    val scale by remember { mutableStateOf(Animatable(1f)) }

    /* Launch side effect to run every time toggle state changes */
    LaunchedEffect(openContent) {
        val targetScale = if (openContent) 1f else 0f
        val targetSize = if (openContent) Size(850f, 425f) else Size(100f, 30f)
        val sizeAni = suspend {
            containerSize.animateTo(
                targetValue = targetSize,
                animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow)
            )
        }
        val scaleAni = suspend {
            scale.animateTo(targetValue = targetScale, animationSpec = tween(500))
        }

        /* Set animation order before running animations sequentially */
        listOf(sizeAni, scaleAni)
            .let { if (openContent) it else it.asReversed() }
            .forEach { withContext(Dispatchers.Default) { it.invoke() } }
    }

    Box(Modifier.fillMaxSize().background(Color(0xFFE5E8E8))) {
        Box(
            modifier = Modifier.padding(15.dp).align(Alignment.Center)
                .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                    openContent = !openContent
                }
                .size(containerSize.value.width.dp, containerSize.value.height.dp)
                .background(Color.White, RoundedCornerShape(10.dp))
                .animateContentSize(
                    animationSpec = tween(500),
                    finishedListener = null
                ),
            content = {
                val followTargets: Array<State<Float>>  = remember { Array(count+1) { mutableStateOf(1f) } }
                for (i in 0 until count) {
                    /* Each follower on the spring chain uses the previous follower's position as target */
                    followTargets[i] = animateFloatAsState(
                        targetValue = if (i==0) scale.value else followTargets[i - 1].value
                    )
                }

                Box(modifier = Modifier.align(Alignment.Center)) {
                    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceEvenly) {
                        (1..count).forEach { rIdx ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                (1..count).forEach { cIdx ->
                                    card(color = fancyColors[1], scale = followTargets[rIdx - 1].value)
                                }
                            }
                        }
                    }
                }
            }
        )
    }
}

@Composable
private fun card(color: Color, scale: Float) {
    Box(Modifier.size(150.dp, 70.dp).scale(scale).background(color, RoundedCornerShape(5.dp)))
}
