package com.example.whereareyou.data

import com.example.whereareyou.R

data class OnboardingPage(val title: String, val description: String, val imageRes: Int)

val pages = listOf(
    OnboardingPage("Welcome!", "This app shows your location on the map.", R.drawable.map_icon),
    OnboardingPage("Connect", "Find and connect with nearby users.", R.drawable.connect_icon),
    OnboardingPage("Stay Safe", "Share your live location securely.", R.drawable.safe_icon)
)
