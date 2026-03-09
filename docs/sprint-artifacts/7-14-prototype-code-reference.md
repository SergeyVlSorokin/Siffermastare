# Prototype Code Reference (Story 7.14)

This file contains the experimental Jetpack Compose prototype code used to evaluate different visualizations for the Knowledge Dashboard. 
Specifically, **Variant 2 (Beta Curve Heatmap Gradient)** was selected as the final design to be implemented on the Home Screen.

Please refer to the mathematical mappings and Compose API usage (especially `Brush.horizontalGradient`) as inspiration when building the final `BetaHeatmapBar` component.

```kotlin
package com.siffermastare.ui.prototype

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.coerceAtLeast
import kotlin.math.roundToInt

// Mock Data Model
data class MockAtom(
    val id: String,
    val alpha: Float,
    val beta: Float,
    val n: Float = alpha + beta,
    val mu: Float = alpha / (alpha + beta)
)

// Generate some sample data with different states
val mockData = listOf(
    // High proficiency, high certainty
    MockAtom("0", alpha = 15f, beta = 1f),
    MockAtom("1", alpha = 20f, beta = 2f),
    // High proficiency, low certainty (new)
    MockAtom("2", alpha = 4f, beta = 1f),
    // Medium proficiency, high certainty
    MockAtom("3", alpha = 20f, beta = 8f),
    MockAtom("4", alpha = 12f, beta = 5f),
    // Low proficiency, high certainty (struggling)
    MockAtom("5", alpha = 5f, beta = 25f),
    MockAtom("10", alpha = 2f, beta = 10f),
    // Low proficiency, low certainty
    MockAtom("11", alpha = 1f, beta = 3f),
    // Untested (Prior)
    MockAtom("12", alpha = 1f, beta = 1f),
    MockAtom("13", alpha = 1f, beta = 1f),
    // Text atoms
    MockAtom("kvart", alpha = 18f, beta = 2f),
    MockAtom("halv", alpha = 8f, beta = 12f)
)

// ----------------------------------------------------------------------
// HELPER METHODS
// ----------------------------------------------------------------------

fun getMuColor(mu: Float): Color {
    // 0.5 is the untrained baseline.
    return when {
        mu >= 0.85f -> Color(0xFF4CAF50) // Green
        mu >= 0.60f -> Color(0xFFFFC107) // Amber/Yellow
        mu == 0.5f -> Color.LightGray
        else -> Color(0xFFF44336) // Red
    }
}

fun getCertaintyScore(n: Float): Float {
    // Arbitrary scaling: N=2 is untested (0%). N>20 is 100% certain.
    val nMapped = (n - 2f) / 18f
    return nMapped.coerceIn(0.2f, 1.0f) // Never completely invisible
}


// ----------------------------------------------------------------------
// MATH HELPERS FOR BETA DISTRIBUTION
// ----------------------------------------------------------------------

// Log Gamma Approximation (Lanczos approximation) to avoid overflow
fun logGamma(z: Double): Double {
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
    var zCalc = z - 1.0
    var x = p[0]
    for (i in 1..8) {
        x += p[i] / (zCalc + i)
    }
    val t = zCalc + g + 0.5
    return 0.5 * Math.log(2 * Math.PI) + (zCalc + 0.5) * Math.log(t) - t + Math.log(x)
}

// Log Beta function: ln(B(a,b)) = ln(Gamma(a)) + ln(Gamma(b)) - ln(Gamma(a+b))
fun logBeta(a: Double, b: Double): Double {
    return logGamma(a) + logGamma(b) - logGamma(a + b)
}

// Beta Probability Density Function (PDF)
fun betaPdf(x: Double, a: Double, b: Double): Double {
    if (x < 0.0 || x > 1.0) return 0.0
    if (x == 0.0) return if (a < 1.0) Double.POSITIVE_INFINITY else if (a == 1.0) b else 0.0
    if (x == 1.0) return if (b < 1.0) Double.POSITIVE_INFINITY else if (b == 1.0) a else 0.0
    
    // PDF = x^(a-1) * (1-x)^(b-1) / B(a,b)
    // We use EXP(LOG()) to prevent massive numerical overflow with large alphas/betas
    val logPdf = (a - 1.0) * Math.log(x) + (b - 1.0) * Math.log(1.0 - x) - logBeta(a, b)
    return Math.exp(logPdf)
}

// ----------------------------------------------------------------------
// VARIANT 2: Beta Distribution Heatmap Bar (WINNER)
// ----------------------------------------------------------------------
@Composable
fun Variant2_BetaCurve() {
    Text("Beta Distribution Heatmap\nX-Axis = Proficiency (0-100%)\nOpacity = PDF Density (Uncertainty)", fontSize = 14.sp, modifier = Modifier.padding(bottom = 16.dp))

    LazyVerticalGrid(
        columns = GridCells.Fixed(2), // Wider cells for bars
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(mockData) { atom ->
            val color = getMuColor(atom.mu)
            
            Column {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(atom.id, fontWeight = FontWeight.Bold)
                    Text("${(atom.mu * 100).roundToInt()}%", fontSize = 12.sp, color = color)
                }
                Spacer(modifier = Modifier.height(4.dp))
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray.copy(alpha = 0.2f))
                        .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                ) {
                    val brush = remember(atom) {
                        if (atom.alpha == 1f && atom.beta == 1f) {
                            // Uniform baseline (no data)
                            Brush.horizontalGradient(listOf(Color.Gray.copy(alpha=0.3f), Color.Gray.copy(alpha=0.3f)))
                        } else {
                            val steps = 50
                            // Find the peak to normalize heights
                            val modeX = when {
                                atom.alpha > 1f && atom.beta > 1f -> (atom.alpha - 1f) / (atom.alpha + atom.beta - 2f)
                                atom.alpha <= 1f && atom.beta > 1f -> 0.01f
                                atom.alpha > 1f && atom.beta <= 1f -> 0.99f
                                else -> 0.5f 
                            }
                            
                            val maxPdf = betaPdf(modeX.toDouble(), atom.alpha.toDouble(), atom.beta.toDouble()).coerceAtLeast(1.0)
                            
                            val colors = mutableListOf<Color>()
                            for (i in 0..steps) {
                                val xPercent = i.toDouble() / steps.toDouble()
                                val xClamped = xPercent.coerceIn(0.005, 0.995)
                                val pdf = betaPdf(xClamped, atom.alpha.toDouble(), atom.beta.toDouble())
                                val normalizedPdf = (pdf / maxPdf).toFloat().coerceIn(0f, 1f)
                                
                                colors.add(color.copy(alpha = normalizedPdf))
                            }
                            // Compose Brush handles mapping color lists cleanly from 0..1 x-axis
                            Brush.horizontalGradient(colors)
                        }
                    }
                    
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                             .background(brush)
                    )
                    
                    // Optional: Draw a subtle vertical line mapping exact Mu
                    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                        val muPx = atom.mu * size.width
                        drawLine(
                            color = Color.White.copy(alpha = 0.5f),
                            start = androidx.compose.ui.geometry.Offset(muPx, 0f),
                            end = androidx.compose.ui.geometry.Offset(muPx, size.height),
                            strokeWidth = 2.dp.toPx()
                        )
                    }
                }
            }
        }
    }
}
```
