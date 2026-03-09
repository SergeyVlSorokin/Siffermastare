package com.siffermastare.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Math Helpers for Beta Distribution PDF (From Prototype)

private fun logGamma(z: Double): Double {
    val p = doubleArrayOf(
        0.99999999999980993,
        676.5203681218851,
        -1259.1392167224028,
        771.32342877765313,
        -176.61502916214059,
        12.507343278224757,
        -0.13857109526572012,
        9.9843695780195716e-6,
        1.5056327351493116e-7
    )
    val g = 7.0
    if (z < 0.5) return Math.PI - Math.log(Math.sin(Math.PI * z)) - logGamma(1 - z)
    val zCalc = z - 1.0
    var x = p[0]
    for (i in 1..8) {
        x += p[i] / (zCalc + i)
    }
    val t = zCalc + g + 0.5
    return 0.5 * Math.log(2 * Math.PI) + (zCalc + 0.5) * Math.log(t) - t + Math.log(x)
}

private fun logBeta(a: Double, b: Double): Double {
    return logGamma(a) + logGamma(b) - logGamma(a + b)
}

private fun betaPdf(x: Double, a: Double, b: Double): Double {
    if (x < 0.0 || x > 1.0) return 0.0
    if (x == 0.0) return if (a < 1.0) Double.POSITIVE_INFINITY else if (a == 1.0) b else 0.0
    if (x == 1.0) return if (b < 1.0) Double.POSITIVE_INFINITY else if (b == 1.0) a else 0.0
    
    val logPdf = (a - 1.0) * Math.log(x) + (b - 1.0) * Math.log(1.0 - x) - logBeta(a, b)
    return Math.exp(logPdf)
}

fun getMasteryColor(mu: Float, alpha: Float, beta: Float): Color {
    if (alpha == 1f && beta == 1f) return Color.LightGray // Untested prior
    
    return when {
        mu >= 0.85f -> Color(0xFF4CAF50) // Green (Mastered)
        mu >= 0.50f -> Color(0xFFFFC107) // Amber/Yellow (Learning, including exactly equal success/failure)
        else -> Color(0xFFF44336)        // Red (Needs Work, < 50%)
    }
}

/**
 * A horizontal bar that visualizes the Beta Distribution (proficiency and uncertainty)
 * for a specific learning atom or global aggregate.
 */
@Composable
fun BetaHeatmapBar(
    alpha: Float,
    beta: Float,
    mu: Float,
    modifier: Modifier = Modifier,
    height: Dp = 32.dp
) {
    val baseColor = getMasteryColor(mu, alpha, beta)

    // Inverted gradient: transparent at PDF peak, opaque at tails (shows uncertainty region)
    val MIN_GRADIENT_ALPHA = 0.0f  // peak
    val MAX_GRADIENT_ALPHA = 0.6f  // tails
    val MU_LINE_ALPHA = 1.0f
    
    Box(
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.LightGray.copy(alpha = 0.15f))
            .border(1.dp, Color.LightGray.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
    ) {
        val brush = remember(alpha, beta) {
            if (alpha == 1f && beta == 1f) {
                // Untested atomic — flat gray
                Brush.horizontalGradient(
                    listOf(
                        Color.Gray.copy(alpha = 0.25f),
                        Color.Gray.copy(alpha = 0.25f)
                    )
                )
            } else {
                val steps = 60
                // Find the peak (mode) position
                val modeX = when {
                    alpha > 1f && beta > 1f -> (alpha - 1f) / (alpha + beta - 2f)
                    alpha <= 1f && beta > 1f -> 0.01f
                    alpha > 1f && beta <= 1f -> 0.99f
                    else -> 0.5f
                }

                val maxPdf = betaPdf(modeX.toDouble(), alpha.toDouble(), beta.toDouble()).coerceAtLeast(1.0)

                // Gradient: knowledge color, lower opacity at tails, higher at mode.
                // Gives 'more color fills bar = more concentrated = more certain'.
                val colors = mutableListOf<Color>()
                for (i in 0..steps) {
                    val xPercent = i.toDouble() / steps.toDouble()
                    val xClamped = xPercent.coerceIn(0.005, 0.995)
                    val pdf = betaPdf(xClamped, alpha.toDouble(), beta.toDouble())
                    val normalizedPdf = (pdf / maxPdf).toFloat().coerceIn(0f, 1f)
                    
                    // Inverted: tails are opaque, peak is transparent
                    val gradientAlpha = MAX_GRADIENT_ALPHA * (1f - normalizedPdf)
                    colors.add(baseColor.copy(alpha = gradientAlpha))
                }
                Brush.horizontalGradient(colors)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush)
        )

        // Draw the mu line in the knowledge color at full opacity (always stands out)
        if (alpha != 1f || beta != 1f) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val muPx = mu * size.width
                // Draw a slightly wider glow line for visual weight
                drawLine(
                    color = baseColor.copy(alpha = 0.35f),
                    start = Offset(muPx, 0f),
                    end = Offset(muPx, size.height),
                    strokeWidth = 6.dp.toPx()
                )
                // Sharp core line at full color
                drawLine(
                    color = baseColor.copy(alpha = MU_LINE_ALPHA),
                    start = Offset(muPx, 0f),
                    end = Offset(muPx, size.height),
                    strokeWidth = 2.dp.toPx()
                )
            }
        }
    }
}
