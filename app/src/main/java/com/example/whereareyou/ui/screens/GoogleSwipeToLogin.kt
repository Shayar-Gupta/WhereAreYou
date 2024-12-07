package com.example.whereareyou.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whereareyou.R

@Composable
fun CompactSwipeToLogin(onSignIn: () -> Unit) {
    // Track the slider's offset
    var offsetX by remember { mutableFloatStateOf(0f) }
    val buttonWidth = 250.dp // Reduced button width
    val maxOffset = remember { mutableFloatStateOf(0f) } // Dynamically calculate maxOffset

    // Access the screen density to convert dp to px
    val density = LocalDensity.current

    LaunchedEffect(Unit) {
        maxOffset.floatValue = with(density) { (buttonWidth - 60.dp).toPx() } // Set maxOffset in pixels
    }

    // Animate the offset to smoothly return to start if not fully swiped
    val animatedOffsetX by animateFloatAsState(
        targetValue = if (offsetX >= maxOffset.floatValue) maxOffset.floatValue else offsetX,
        animationSpec = tween(durationMillis = 300), label = ""
    )

    Box(
        modifier = Modifier
            .width(buttonWidth)
            .height(60.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(
                if (offsetX >= maxOffset.floatValue) Color(0xFF34A853) // Green when swipe is complete
                else Color.LightGray.copy(alpha = 0.3f)
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        // Show text only when swipe is incomplete
        if (offsetX < maxOffset.floatValue) {
            Text(
                text = "Swipe to Login with Google",
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            Text(
                text = "Logging In...",
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // Draggable circle with Google logo
        Box(
            modifier = Modifier
                .size(50.dp)
                .offset { IntOffset(animatedOffsetX.toInt(), 0) }
                .clip(CircleShape)
                .background(Color.White)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDrag = { change, dragAmount ->
                            change.consume()
                            offsetX = (offsetX + dragAmount.x).coerceIn(0f, maxOffset.floatValue)
                        },
                        onDragEnd = {
                            if (offsetX >= maxOffset.floatValue - 10) {
                                // Trigger Sign-In on full swipe
                                onSignIn()
                            } else {
                                // Reset to initial position
                                offsetX = 0f
                            }
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.google_logo),
                contentDescription = "Google Logo",
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

