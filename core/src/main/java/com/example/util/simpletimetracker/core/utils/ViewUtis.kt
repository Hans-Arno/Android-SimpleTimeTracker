package com.example.util.simpletimetracker.core.utils

import androidx.cardview.widget.CardView
import com.example.util.simpletimetracker.core.R
import com.example.util.simpletimetracker.feature_views.extension.getThemedAttr

/**
 * Sets card background depending if it was clicked before (eg. opening a chooser by clicking on card).
 */
fun CardView.setChooserColor(opened: Boolean) {
    val colorAttr = if (opened) {
        R.attr.appInputFieldBorderColor
    } else {
        R.attr.appBackgroundColor
    }
    context.getThemedAttr(colorAttr).let(::setCardBackgroundColor)
}