package com.example.util.simpletimetracker.feature_base_adapter.info

import com.example.util.simpletimetracker.feature_base_adapter.ViewHolderType

data class InfoViewData(
    val text: String,
) : ViewHolderType {

    // Only one in recycler, add id if needed, but don't do text hashcode,
    // otherwise tag recycler items on change type will disappear after selecting all and
    // removing all (same in type selection in change category).
    override fun getUniqueId(): Long = 1L

    override fun isValidType(other: ViewHolderType): Boolean = other is InfoViewData
}