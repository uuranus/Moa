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
import androidx.compose.ui.tooling.preview.Preview
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
    val sumOfData = remember(data) { data.map { it.toFloat() }.sum() }
    Canvas(
        modifier = modifier
    ) {
        val lineStart = size.width * 0.05f
        val lineEnd = size.width * 0.95f

        //차트 길이
        val lineLength = (lineEnd - lineStart)
        //(canvas높이 - 차트 높이) * 0.5 를 하면 차트를 그릴 위쪽 오프셋을 구할 수 있습니다.
        val lineHeightOffset = (size.height - strokeWidth) * 0.5f
        val path = Path().apply {
            addRoundRect(
                RoundRect(
                    Rect(
                        offset = Offset(lineStart, lineHeightOffset),
                        size = Size(lineLength, strokeWidth)
                    ),
                    cornerRadius
                )
            )
        }
        val dataAndColor = data.zip(colors)
        clipPath(
            path
        ) {
            var dataStart = lineStart
            dataAndColor.forEach { (number, color) ->
                //끝점은 시작점 + (변량의 비율 * 전체 길이)
                val dataEnd =
                    dataStart + ((number.toFloat() / sumOfData) * lineLength)
                drawRect(
                    color = color,
                    topLeft = Offset(dataStart, lineHeightOffset),
                    size = Size(dataEnd - dataStart, strokeWidth)
                )
                //다음 사각형의 시작점은 현재의 끝점
                dataStart = dataEnd
            }
        }

    }
}

@Composable
@Preview(widthDp = 300, heightDp = 200, showBackground = true)
fun ProportionBarPreview() {
    ProportionBar(
        data = listOf(1),
        colors = listOf(Color.Black, Color.Red, Color.Green, Color.Blue),
        strokeWidth = 180f,
        modifier = Modifier.fillMaxSize(),
        animate = true
    )

}


