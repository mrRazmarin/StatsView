package ru.netology.statsview

import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ru.netology.statsview.ui.StatsView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val view = findViewById<StatsView>(R.id.stats_view)
        view.data = listOf(500F, 500F, 500F, 500F)

        val textView = findViewById<TextView>(R.id.label)
        textView.text = "onAnimationStart"

        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 1500
            addUpdateListener { animation ->
                view.progress = animation.animatedValue as Float
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: android.animation.Animator) {
                    textView.text = "onAnimationStart"
                }
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    textView.text = "onAnimationEnd"
                }
            })
            start()
        }
    }
}