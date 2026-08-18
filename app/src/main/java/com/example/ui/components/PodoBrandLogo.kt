package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.HealthTealPrimary
import com.example.ui.theme.PodoTeal
import com.example.ui.theme.PodoTealDark

/**
 * Custom Brush-Stroke Style Logo matching "Siyah Fırça Darbesi Stili Logo"
 */
@Composable
fun PodoBrandLogo(
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    showSubtext: Boolean = true,
    isDarkText: Boolean = true
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(size),
            contentAlignment = Alignment.Center
        ) {
            // Brush Stroke Canvas Icon
            Canvas(modifier = Modifier.size(size)) {
                val w = this.size.width
                val h = this.size.height
                val mainColor = if (isDarkText) Color(0xFF121212) else Color.White

                // 1. Soft Circular Teal Glow Background
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF80CBC4).copy(alpha = 0.45f), Color.Transparent),
                        center = Offset(w / 2, h / 2),
                        radius = w * 0.52f
                    )
                )

                // 2. Artistic "P" with Brush Stroke Arch
                val pStrokePath = Path().apply {
                    // Vertical bold spine
                    moveTo(w * 0.28f, h * 0.82f)
                    cubicTo(w * 0.27f, h * 0.55f, w * 0.25f, h * 0.32f, w * 0.22f, h * 0.16f)
                    cubicTo(w * 0.28f, h * 0.14f, w * 0.48f, h * 0.12f, w * 0.62f, h * 0.25f)
                    cubicTo(w * 0.72f, h * 0.35f, w * 0.68f, h * 0.52f, w * 0.50f, h * 0.56f)
                    cubicTo(w * 0.38f, h * 0.58f, w * 0.28f, h * 0.55f, w * 0.26f, h * 0.54f)
                }

                drawPath(
                    path = pStrokePath,
                    color = mainColor,
                    style = Stroke(
                        width = w * 0.11f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )

                // 3. Dynamic Foot Arch Bottom Swoosh (Brush effect)
                val footSwoosh = Path().apply {
                    moveTo(w * 0.12f, h * 0.32f)
                    cubicTo(w * 0.18f, h * 0.22f, w * 0.45f, h * 0.28f, w * 0.76f, h * 0.48f)
                    cubicTo(w * 0.88f, h * 0.56f, w * 0.90f, h * 0.70f, w * 0.80f, h * 0.82f)
                    cubicTo(w * 0.68f, h * 0.92f, w * 0.45f, h * 0.88f, w * 0.35f, h * 0.78f)
                }

                drawPath(
                    path = footSwoosh,
                    color = mainColor,
                    style = Stroke(
                        width = w * 0.085f,
                        cap = StrokeCap.Round
                    )
                )

                // 4. Vibrant Medical Cross Badge inside the Arch
                val crossW = w * 0.09f
                val crossH = h * 0.28f
                val cx = w * 0.52f
                val cy = h * 0.52f

                drawRoundRect(
                    color = HealthTealPrimary,
                    topLeft = Offset(cx - crossW / 2, cy - crossH / 2),
                    size = Size(crossW, crossH),
                    cornerRadius = CornerRadius(6f, 6f)
                )
                drawRoundRect(
                    color = HealthTealPrimary,
                    topLeft = Offset(cx - crossH / 2, cy - crossW / 2),
                    size = Size(crossH, crossW),
                    cornerRadius = CornerRadius(6f, 6f)
                )

                // 5. Golden Amber Vital Dot
                drawCircle(
                    color = Color(0xFFFFB300),
                    center = Offset(w * 0.78f, h * 0.25f),
                    radius = w * 0.06f
                )
            }
        }

        if (showSubtext) {
            Spacer(modifier = Modifier.height(8.dp))
            PodoBrushTitle(
                fontSize = (size.value * 0.34f).sp,
                isDarkText = isDarkText
            )
        }
    }
}

/**
 * Brush-Stroke Calligraphy Typography matching "PodoAssistan"
 */
@Composable
fun PodoBrushTitle(
    modifier: Modifier = Modifier,
    fontSize: androidx.compose.ui.unit.TextUnit = 24.sp,
    isDarkText: Boolean = true
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Podo",
            fontSize = fontSize,
            fontWeight = FontWeight.Black,
            fontStyle = FontStyle.Italic,
            letterSpacing = (-0.8).sp,
            color = if (isDarkText) Color(0xFF111111) else Color.White
        )
        Text(
            text = "Assistan",
            fontSize = fontSize,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.2.sp,
            color = HealthTealPrimary
        )
    }
}

/**
 * Compact Brand Logo badge for App Bars
 */
@Composable
fun PodoBrandIconBadge(
    modifier: Modifier = Modifier,
    size: Dp = 38.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .background(Color(0xFFE0F2F1), RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size * 0.82f)) {
            val w = this.size.width
            val h = this.size.height

            // Foot curve
            val path = Path().apply {
                moveTo(w * 0.25f, h * 0.18f)
                cubicTo(w * 0.15f, h * 0.45f, w * 0.22f, h * 0.75f, w * 0.55f, h * 0.85f)
                cubicTo(w * 0.80f, h * 0.85f, w * 0.85f, h * 0.65f, w * 0.78f, h * 0.40f)
            }
            drawPath(
                path = path,
                color = Color(0xFF1A1A1A),
                style = Stroke(width = w * 0.14f, cap = StrokeCap.Round)
            )

            // Medical Cross
            val crossW = w * 0.12f
            val crossH = h * 0.34f
            val cx = w * 0.52f
            val cy = h * 0.48f

            drawRoundRect(
                color = HealthTealPrimary,
                topLeft = Offset(cx - crossW / 2, cy - crossH / 2),
                size = Size(crossW, crossH),
                cornerRadius = CornerRadius(4f, 4f)
            )
            drawRoundRect(
                color = HealthTealPrimary,
                topLeft = Offset(cx - crossH / 2, cy - crossW / 2),
                size = Size(crossH, crossW),
                cornerRadius = CornerRadius(4f, 4f)
            )
        }
    }
}
