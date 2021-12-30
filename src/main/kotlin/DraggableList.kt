@file:Suppress("FunctionName")

import androidx.compose.animation.core.*
import androidx.compose.desktop.Window
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.zIndex
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import theme.customTypography
import theme.fancyColors
import theme.shapes
import kotlin.math.roundToInt

val list = (0..6)

fun DraggableList() = Window(title = "Draggable List", size = IntSize(500, 800)) {
    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {

        var order by remember { mutableStateOf(list.toMutableList()) }
        var focus /* Current Card Focused on */: Int? by remember { mutableStateOf(null) }
        var focusOffset /* Current Card Focused on */ by remember { mutableStateOf(Offset(0f, 0f)) }

        val setIndexOnClick = { ref: Int? -> focus = order.indexOf(ref) }

        val setOrderOnDrag = { offset: Offset ->
            val newIdx /* New index */ = focus?.let {
                (order.indexOf(it) + ((offset - focusOffset).y / 100).roundToInt()).coerceIn(list)
            }
            focus?.let {
                if (newIdx != order.indexOf(it)) focusOffset = offset
            }
            order = if (focus != null && newIdx != null) order.swap(newIdx, order.indexOf(focus)) else order
        }

        val resetFocusOffset = { focusOffset = Offset(0f, 0f) }

        Column(
            modifier = Modifier.fillMaxSize().align(Alignment.Center),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            content = {
                order.forEach { ref ->
                    DraggableCard(ref, setIndexOnClick, setOrderOnDrag, resetFocusOffset, ref == focus)
                }
            }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize().align(Alignment.Center)
                .background(color = Color.Black, shape = shapes.medium),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            content = {
                println(order)
                order.forEach { ref ->
                    item(ref) { DraggableCard(ref, setIndexOnClick, setOrderOnDrag, resetFocusOffset, ref == focus) }
                }
            }
        )
    }
}

@Composable
private fun DraggableCard(
    index: Int,
    setIndexOnClick: (Int?) -> Unit,
    setOrderOnDrag: (Offset) -> Unit,
    resetFocusOffset: () -> Unit,
    enlarge: Boolean
) {
    val scaleAnimation by animateFloatAsState(
        targetValue = if (enlarge) 1.2f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )
    val offset = remember { Animatable(Offset(0f, 0f), Offset.VectorConverter) }
    Box(
        modifier = Modifier.size(400.dp, 100.dp)
            .offset { Offset(0f, offset.value.y).round() }
            .scale(scaleAnimation)
            .padding(10.dp)
            .shadow(elevation = if (enlarge) 10.dp else 3.dp)
            .zIndex(if (scaleAnimation == 1f) -1f else 1f)
            .background(fancyColors.reversed()[index], shape = RoundedCornerShape(5.dp))
            .pointerInput(Unit) {
                coroutineScope {
                    launch {
                        detectDragGestures(
                            onDragEnd = { launch { offset.animateTo(Offset(0f, 0f)) } },
                            onDrag = { _, drag ->
//                                offset.value += drag
                                setOrderOnDrag(offset.value + drag)
                                launch { offset.snapTo(offset.value + drag) }
                            }
                        )
                    }
                    launch {
                        while (isActive)
                            awaitPointerEventScope {
                                val down = awaitPointerEvent(PointerEventPass.Main).changes.first()
                                if (down.pressed) setIndexOnClick(index)
                                if (down.changedToUp()) {
                                    setIndexOnClick(null)
                                    resetFocusOffset()
                                }
                            }
                    }
                }
            },
        contentAlignment = Alignment.CenterStart,
        content = { Text(text = "$index: ${texts[index]}", Modifier.padding(10.dp), style = customTypography.h6) }
    )
}

val texts =
    ("Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
            "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.")
        .split(" ")
        .filter { string -> string.length > 5 }

fun <T> MutableList<T>.swap(i: Int, j: Int): MutableList<T> {
    if (i == j) return this
    with(this[i]) {
        this@swap[i] = this@swap[j]
        this@swap[j] = this
    }
    return this
}