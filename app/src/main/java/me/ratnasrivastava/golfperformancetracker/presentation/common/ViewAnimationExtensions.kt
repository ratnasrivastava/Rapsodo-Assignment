package me.ratnasrivastava.golfperformancetracker.presentation.common

import android.animation.ValueAnimator
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.ratnasrivastava.golfperformancetracker.R

fun TextView.animateNumber(
    target: Double,
    durationMs: Long = 700L,
    format: (Double) -> String
) {
    // Cancel any in-flight animation tagged on this view to avoid overlap.
    (getTag(R.id.tag_value_animator) as? ValueAnimator)?.cancel()

    val animator = ValueAnimator.ofFloat(0f, target.toFloat()).apply {
        duration = durationMs
        addUpdateListener { anim ->
            val value = (anim.animatedValue as Float).toDouble()
            text = format(value)
        }
    }
    setTag(R.id.tag_value_animator, animator)
    animator.start()
}

fun RecyclerView.runLayoutAnimation() {
    val controller = AnimationUtils.loadLayoutAnimation(
        context,
        R.anim.layout_animation_fall_down
    )
    layoutAnimation = controller
    adapter?.notifyDataSetChanged()
    scheduleLayoutAnimation()
}