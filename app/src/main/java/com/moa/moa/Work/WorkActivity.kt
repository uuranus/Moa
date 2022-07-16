package com.moa.moa.Work

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import com.google.android.material.slider.Slider
import com.moa.moa.R

class WorkActivity : AppCompatActivity() {

    private val backButton:ImageButton by lazy{
        findViewById(R.id.backButton)
    }

    private val saveButton:TextView by lazy{
        findViewById(R.id.saveButton)
    }

    private val titleEditText:EditText by lazy{
        findViewById(R.id.workTitleEditText)
    }

    private val descriptionEditText:EditText by lazy{
        findViewById(R.id.workDescriptionEditText)
    }

    private val periodSlider: Slider by lazy{
        findViewById(R.id.workPeriodSlider)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_work)
    }
}