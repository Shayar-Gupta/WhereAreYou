package com.example.whereareyou.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.whereareyou.R
import com.example.whereareyou.data.pages
import com.example.whereareyou.viewmodel.GoogleSignInViewModel

@Composable
fun OnboardingScreen(
    viewModel: GoogleSignInViewModel, // Pass the ViewModel
    navController: NavController, // Navigation controller for navigation
    context: Context
) {
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { pages.size })
    val lastPage = pages.size - 1

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)),
    ) {
        // Horizontal pager (swiping through onboarding pages)
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = pagerState.currentPage < lastPage // Disables back scroll
        ) { pageIndex ->
            val page = pages[pageIndex]
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = page.imageRes),
                    contentDescription = page.title,
                    modifier = Modifier
                        .size(180.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = page.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = page.description,
                    fontSize = 16.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp),
                    lineHeight = 22.sp
                )
            }
        }

        // Page Indicator Dots (hidden on the last page)
        AnimatedVisibility(
            visible = pagerState.currentPage < lastPage, // Hide on last page
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 100.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                pages.forEachIndexed { index, _ ->
                    Box(
                        modifier = Modifier
                            .size(if (index == pagerState.currentPage) 10.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (index == pagerState.currentPage) Color.Black
                                else Color.Gray.copy(alpha = 0.5f)
                            )
                    )
                }
            }
        }

        // Google Sign Up Button (appears only on the last page)
        AnimatedVisibility(
            visible = pagerState.currentPage == lastPage,
            enter = expandVertically(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 100.dp)
        ) {
            SwipeToGoogleLogin(
                viewModel = viewModel,
                navController = navController,
                context = context
            )
        }
    }
}

@Composable
fun SwipeToGoogleLogin(
    viewModel: GoogleSignInViewModel, // Pass the ViewModel
    navController: NavController, // Navigation controller for navigation
    context: Context // Context for operations
) {
    // Track the slider's offset
    var offsetX by remember { mutableFloatStateOf(0f) }
    val buttonWidth = 250.dp // Reduced button width
    val maxOffset = remember { mutableFloatStateOf(0f) } // Dynamically calculate maxOffset

    // Access the screen density to convert dp to px
    val density = LocalDensity.current

    LaunchedEffect(Unit) {
        maxOffset.floatValue = with(density) { (buttonWidth - 60.dp).toPx() } // Set maxOffset in pixels
    }

    //अगर यूजर halfway छोड़ देता है, तो circle 300ms में वापस slide होकर starting position पर आ जाएगा।
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
        Box(
            modifier = Modifier.padding(start = 4.dp)
        ){
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
                                // Trigger Google Sign-In via ViewModel when swipe is complete
                                viewModel.handleGoogleSignIn(context, navController)
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
                modifier = Modifier.size(30.dp).padding(start = 4.dp)
            )
        }
    }
}
