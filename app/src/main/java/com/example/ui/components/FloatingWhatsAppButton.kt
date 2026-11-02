package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.theme.WhatsAppGreenDark

@Composable
fun FloatingWhatsAppButton(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Subtle floating vertical bounce & breathing pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "wa_pulse")

    val translateY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wa_bounce"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wa_scale"
    )

    val shadowElevation by infiniteTransition.animateFloat(
        initialValue = 6f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wa_shadow"
    )

    val whatsappUrl = "https://wa.me/923316813901?text=Hello%20SD%20BALOCH%2C%20I%20need%20help%20with%20QX%20BOT."

    Box(
        modifier = modifier
            .offset(y = translateY.dp)
            .scale(pulseScale)
            .size(60.dp)
            .shadow(
                elevation = shadowElevation.dp,
                shape = CircleShape,
                spotColor = WhatsAppGreen
            )
            .clip(CircleShape)
            .background(WhatsAppGreen)
            .border(2.dp, Color(0xFFD4AF37), CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = Color.White),
                onClick = {
                    openWhatsApp(context, whatsappUrl)
                }
            )
            .testTag("floating_whatsapp_button"),
        contentAlignment = Alignment.Center
    ) {
        // Official WhatsApp Logo Icon
        Icon(
            painter = painterResource(id = R.drawable.ic_whatsapp),
            contentDescription = "Contact SD BALOCH on WhatsApp",
            tint = Color.White,
            modifier = Modifier.size(34.dp)
        )
    }
}

/**
 * Handles launching WhatsApp mobile app or WhatsApp Web fallback seamlessly
 */
private fun openWhatsApp(context: Context, urlString: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(urlString)
            // Attempt to route directly to WhatsApp package if available
            setPackage("com.whatsapp")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        // Fallback: If WhatsApp app is not installed, open directly in web browser / WhatsApp Web
        try {
            val webFallbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse(urlString)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(webFallbackIntent)
        } catch (e2: Exception) {
            Toast.makeText(context, "Opening WhatsApp at +923316813901...", Toast.LENGTH_SHORT).show()
        }
    }
}
