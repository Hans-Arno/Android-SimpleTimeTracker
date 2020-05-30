package com.example.util.simpletimetracker.feature_statistics.adapter

import com.example.util.simpletimetracker.core.adapter.BaseRecyclerAdapter
import com.example.util.simpletimetracker.core.adapter.ViewHolderType

class StatisticsAdapter(
    onFilterClick: (() -> Unit)
) : BaseRecyclerAdapter() {

    init {
        delegates[ViewHolderType.HEADER] = StatisticsChartAdapterDelegate(onFilterClick)
        delegates[ViewHolderType.VIEW] = StatisticsAdapterDelegate()
        delegates[ViewHolderType.FOOTER] = StatisticsEmptyAdapterDelegate()
    }
}