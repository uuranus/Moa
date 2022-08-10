package com.moa.moa.Main

import android.content.Context
import android.util.AttributeSet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.AbstractComposeView

class ProportionBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : AbstractComposeView(context, attrs, defStyle) {

    var data by mutableStateOf<List<Number>>(listOf())
    var colors by mutableStateOf<List<Color>>(listOf())
    var strokeWidth by mutableStateOf<Float>(0f)
    var animate by mutableStateOf<Boolean>(false)
    var modifier by mutableStateOf<Modifier>(Modifier)

    @Composable
    override fun Content() {
        ProportionBar(
            data = data,
            colors = colors,
            strokeWidth = strokeWidth,
            animate = animate,
            modifier = modifier
        )
    }
}