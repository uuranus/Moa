package com.moa.moa.Main

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import com.moa.moa.R
import com.moa.moa.ui.theme.MoaTheme


class ChartViewComposeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
}


@Composable
fun ProportionBar(
    data: List<Number>,
    colors: List<Color>,
    strokeWidth: Float,
    cornerRadius: CornerRadius = CornerRadius(strokeWidth),
    animate: Boolean,
    modifier: Modifier
) {
    val animationProgress: Float by animateFloatAsState(
        targetValue = if (animate) 1f else 0f,
        tween(2000)
    )
    val primaryColor  =  colorResource(id = R.color.primary)
    Canvas(modifier = modifier) {
        var start = 30f
        drawRoundRect(
            color = primaryColor,
            topLeft = Offset(start, 80f),
            size = Size(data[0].toFloat() * 20f * animationProgress, 100f),
            cornerRadius = CornerRadius(40f),
        )
    }
}

@Composable
@Preview(widthDp = 300, heightDp = 200, showBackground = false)
fun ProportionBarPreview() {
    ProportionBar(
        data = listOf(20),
        colors = listOf(Color.Black, Color.Red, Color.Green, Color.Blue),
        strokeWidth = 30f,
        modifier = Modifier.fillMaxSize(),
        animate = true
    )

}


