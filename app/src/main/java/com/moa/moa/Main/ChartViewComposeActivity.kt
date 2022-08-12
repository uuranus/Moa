package com.moa.moa.Main

import android.content.Context
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.os.Bundle
import android.util.AttributeSet
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.moa.moa.R
import com.moa.moa.Utility


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
    val context = LocalContext.current
    val starImg = BitmapFactory.decodeResource(context.resources,R.drawable.chart_star).asImageBitmap()
    val padding= 10.dp
    val animationProgress: Float by animateFloatAsState(
        targetValue = if (animate) 1f else 0f,
        tween(2000)
    )
    val primaryColor  =  colorResource(id = R.color.colorPrimary)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Spacer(Modifier.size(padding))
        Canvas(
            modifier = Modifier.size(pxToDp(data[0].toFloat() * 15f).dp, pxToDp(80f).dp)
        ) {
            var start = 30f
            drawRoundRect(
                color = primaryColor,
                //topLeft = Offset(start, 80f),
                size = Size(data[0].toFloat() * 15f, 80f),
                cornerRadius = CornerRadius(40f),
                alpha = 0.5f
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(starImg,"star")
            Text(text = data[0].toString())
        }
    }

}

@Composable
@Preview(widthDp = 300, heightDp = 100, showBackground = false)
fun ProportionBarPreview() {
    ProportionBar(
        data = listOf(20),
        colors = listOf(Color.Black, Color.Red, Color.Green, Color.Blue),
        strokeWidth = 30f,
        modifier = Modifier.fillMaxSize(),
        animate = true
    )

}

fun pxToDp(px: Float): Int {
    return (px/ Resources.getSystem().displayMetrics.density).toInt()
}

