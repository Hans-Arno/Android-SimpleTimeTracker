package com.example.util.simpletimetracker.feature_change_record_type.adapter

import com.example.util.simpletimetracker.feature_base_adapter.createRecyclerBindingAdapterDelegate
import com.example.util.simpletimetracker.feature_change_record_type.databinding.ChangeRecordTypeItemIconCategoryInfoLayoutBinding as Binding
import com.example.util.simpletimetracker.core.delegates.iconSelection.viewData.ChangeRecordTypeIconCategoryInfoViewData as ViewData

fun createChangeRecordTypeIconCategoryInfoAdapterDelegate() =
    createRecyclerBindingAdapterDelegate<ViewData, Binding>(
        Binding::inflate,
    ) { binding, item, _ ->

        with(binding) {
            item as ViewData

            tvChangeRecordTypeIconCategoryInfoItem.text = item.text
        }
    }