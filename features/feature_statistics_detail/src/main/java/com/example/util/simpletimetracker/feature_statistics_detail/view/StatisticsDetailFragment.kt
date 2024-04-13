package com.example.util.simpletimetracker.feature_statistics_detail.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.util.simpletimetracker.core.base.BaseFragment
import com.example.util.simpletimetracker.core.dialog.CustomRangeSelectionDialogListener
import com.example.util.simpletimetracker.core.dialog.DateTimeDialogListener
import com.example.util.simpletimetracker.core.dialog.RecordsFilterListener
import com.example.util.simpletimetracker.core.extension.observeOnce
import com.example.util.simpletimetracker.core.extension.setSharedTransitions
import com.example.util.simpletimetracker.core.extension.toViewData
import com.example.util.simpletimetracker.core.utils.fragmentArgumentDelegate
import com.example.util.simpletimetracker.core.viewData.RangesViewData
import com.example.util.simpletimetracker.domain.model.Range
import com.example.util.simpletimetracker.feature_base_adapter.BaseRecyclerAdapter
import com.example.util.simpletimetracker.feature_base_adapter.ViewHolderType
import com.example.util.simpletimetracker.feature_base_adapter.hint.createHintAdapterDelegate
import com.example.util.simpletimetracker.feature_base_adapter.statisticsTag.createStatisticsTagAdapterDelegate
import com.example.util.simpletimetracker.feature_statistics_detail.adapter.createStatisticsPreviewAdapterDelegate
import com.example.util.simpletimetracker.feature_statistics_detail.adapter.createStatisticsPreviewCompareAdapterDelegate
import com.example.util.simpletimetracker.feature_statistics_detail.customView.BarChartView
import com.example.util.simpletimetracker.feature_statistics_detail.viewData.StatisticsDetailCardViewData
import com.example.util.simpletimetracker.feature_statistics_detail.viewData.StatisticsDetailChartCompositeViewData
import com.example.util.simpletimetracker.feature_statistics_detail.viewData.StatisticsDetailChartViewData
import com.example.util.simpletimetracker.feature_statistics_detail.viewData.StatisticsDetailPreviewCompositeViewData
import com.example.util.simpletimetracker.feature_statistics_detail.viewData.StatisticsDetailPreviewViewData
import com.example.util.simpletimetracker.feature_statistics_detail.viewData.StatisticsDetailStatsViewData
import com.example.util.simpletimetracker.feature_statistics_detail.viewData.StatisticsDetailStreaksViewData
import com.example.util.simpletimetracker.feature_statistics_detail.viewModel.StatisticsDetailViewModel
import com.example.util.simpletimetracker.feature_views.extension.setOnClick
import com.example.util.simpletimetracker.feature_views.extension.setOnLongClick
import com.example.util.simpletimetracker.feature_views.extension.visible
import com.example.util.simpletimetracker.navigation.params.screen.RecordsFilterResultParams
import com.example.util.simpletimetracker.navigation.params.screen.StatisticsDetailParams
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import dagger.hilt.android.AndroidEntryPoint
import com.example.util.simpletimetracker.feature_statistics_detail.databinding.StatisticsDetailFragmentBinding as Binding

@AndroidEntryPoint
class StatisticsDetailFragment :
    BaseFragment<Binding>(),
    DateTimeDialogListener,
    CustomRangeSelectionDialogListener,
    RecordsFilterListener {

    override val inflater: (LayoutInflater, ViewGroup?, Boolean) -> Binding =
        Binding::inflate

    private val viewModel: StatisticsDetailViewModel by viewModels()

    private val previewAdapter: BaseRecyclerAdapter by lazy {
        BaseRecyclerAdapter(
            createStatisticsPreviewCompareAdapterDelegate(),
            createStatisticsPreviewAdapterDelegate(),
        )
    }
    private val nextActivitiesAdapter: BaseRecyclerAdapter by lazy {
        BaseRecyclerAdapter(
            createHintAdapterDelegate(),
            createStatisticsPreviewAdapterDelegate(),
        )
    }
    private val dataSplitAdapter: BaseRecyclerAdapter by lazy {
        BaseRecyclerAdapter(
            createHintAdapterDelegate(),
            createStatisticsTagAdapterDelegate(),
        )
    }
    private val params: StatisticsDetailParams by fragmentArgumentDelegate(
        key = ARGS_PARAMS, default = StatisticsDetailParams(),
    )

    override fun initUi(): Unit = with(binding) {
        setPreview()

        setSharedTransitions(
            additionalCondition = { params.transitionName.isNotEmpty() },
            transitionName = params.transitionName,
            sharedView = viewStatisticsDetailItem,
        )

        rvStatisticsDetailPreviewItems.apply {
            layoutManager = FlexboxLayoutManager(requireContext()).apply {
                flexDirection = FlexDirection.ROW
                justifyContent = JustifyContent.FLEX_START
                flexWrap = FlexWrap.WRAP
            }
            adapter = previewAdapter
        }
        rvStatisticsDetailNextActivities.apply {
            layoutManager = FlexboxLayoutManager(requireContext()).apply {
                flexDirection = FlexDirection.ROW
                justifyContent = JustifyContent.CENTER
                flexWrap = FlexWrap.WRAP
            }
            adapter = nextActivitiesAdapter
        }
        rvStatisticsDetailSplit.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = dataSplitAdapter
        }
    }

    override fun initUx() = with(binding) {
        cardStatisticsDetailFilter.setOnClick(viewModel::onFilterClick)
        cardStatisticsDetailCompare.setOnClick(viewModel::onCompareClick)
        buttonsStatisticsDetailStreaksType.listener = viewModel::onStreaksTypeClick
        buttonsStatisticsDetailGrouping.listener = viewModel::onChartGroupingClick
        buttonsStatisticsDetailLength.listener = viewModel::onChartLengthClick
        buttonsStatisticsDetailSplitGrouping.listener = viewModel::onSplitChartGroupingClick
        cardStatisticsDetailRecords.listener = throttle(viewModel::onCardClick)
        cardStatisticsDetailAverage.listener = throttle(viewModel::onCardClick)
        chartStatisticsStreaksCalendar.setClickListener(throttle(viewModel::onStreaksCalendarClick))
        chartStatisticsComparisonStreaksCalendar.setClickListener(throttle(viewModel::onComparisonStreaksCalendarClick))
        spinnerStatisticsDetail.onItemSelected = viewModel::onRangeSelected
        btnStatisticsDetailPrevious.setOnClick(viewModel::onPreviousClick)
        btnStatisticsDetailNext.setOnClick(viewModel::onNextClick)
        btnStatisticsDetailToday.setOnClick { spinnerStatisticsDetail.performClick() }
        btnStatisticsDetailToday.setOnLongClick(viewModel::onTodayClick)
    }

    override fun onDateTimeSet(timestamp: Long, tag: String?) {
        viewModel.onDateTimeSet(timestamp, tag)
    }

    override fun onCustomRangeSelected(range: Range) {
        viewModel.onCustomRangeSelected(range)
    }

    override fun initViewModel(): Unit = with(viewModel) {
        initialize(params)

        previewViewData.observe(::setPreviewViewData)
        emptyRangeAveragesData.observeOnce(viewLifecycleOwner, ::setEmptyRangeAveragesData)
        statsViewData.observe(::setStatsViewData)
        streaksViewData.observe(::setStreaksViewData)
        streaksTypeViewData.observe(binding.buttonsStatisticsDetailStreaksType.adapter::replace)
        chartViewData.observe(::updateChartViewData)
        splitChartViewData.observe(::updateSplitChartViewData)
        comparisonSplitChartViewData.observe(::updateComparisonSplitChartViewData)
        durationSplitChartViewData.observe(::updateDurationSplitChartViewData)
        comparisonDurationSplitChartViewData.observe(::updateComparisonDurationSplitChartViewData)
        splitChartGroupingViewData.observe(::updateSplitChartGroupingData)
        nextActivitiesViewData.observe(::updateNextActivitiesViewData)
        title.observe(binding.btnStatisticsDetailToday::setText)
        rangeItems.observe(::updateRangeItems)
        rangeButtonsVisibility.observe(::updateRangeButtonsVisibility)
    }

    override fun onResume() {
        super.onResume()
        viewModel.onVisible()
    }

    override fun onFilterChanged(result: RecordsFilterResultParams) {
        viewModel.onTypesFilterSelected(result)
    }

    override fun onFilterDismissed(tag: String) {
        viewModel.onTypesFilterDismissed(tag)
    }

    private fun setPreview() = params.preview?.run {
        val preview = StatisticsDetailPreviewViewData(
            id = 0L,
            type = StatisticsDetailPreviewViewData.Type.FILTER,
            name = name,
            iconId = iconId?.toViewData(),
            color = color,
        )

        StatisticsDetailPreviewCompositeViewData(
            data = preview,
            additionalData = emptyList(),
            comparisonData = emptyList(),
        ).let(::setPreviewViewData)
    }

    private fun setPreviewViewData(viewData: StatisticsDetailPreviewCompositeViewData) = with(binding) {
        val first = viewData.data ?: return
        val rest: List<ViewHolderType> = viewData.additionalData + viewData.comparisonData

        viewStatisticsDetailItem.itemName = first.name
        viewStatisticsDetailItem.itemColor = first.color
        if (first.iconId != null) {
            viewStatisticsDetailItem.itemIconVisible = true
            viewStatisticsDetailItem.itemIcon = first.iconId
        } else {
            viewStatisticsDetailItem.itemIconVisible = false
        }

        chartStatisticsDetail.setBarColor(first.color)
        chartStatisticsDetailSplit.setBarColor(first.color)
        chartStatisticsDetailDurationSplit.setBarColor(first.color)
        chartStatisticsStreaks.setBarColor(first.color)
        chartStatisticsStreaksCalendar.setCellColor(first.color)

        viewData.comparisonData
            .filterIsInstance<StatisticsDetailPreviewViewData>()
            .firstOrNull()
            ?.let {
                chartStatisticsDetailCompare.setBarColor(it.color)
                chartStatisticsDetailComparisonSplit.setBarColor(it.color)
                chartStatisticsDetailDurationSplitCompare.setBarColor(it.color)
                chartStatisticsComparisonStreaks.setBarColor(it.color)
                chartStatisticsComparisonStreaksCalendar.setCellColor(it.color)
            }

        previewAdapter.replace(rest)
    }

    private fun setStatsViewData(
        statsViewData: StatisticsDetailStatsViewData,
    ) = with(binding) {
        cardStatisticsDetailTotal.items = statsViewData.totalDuration
        cardStatisticsDetailRecords.items = statsViewData.timesTracked
        cardStatisticsDetailAverage.items = statsViewData.averageRecord
        cardStatisticsDetailDates.items = statsViewData.datesTracked
        rvStatisticsDetailSplit.visible = statsViewData.splitData.isNotEmpty()
        dataSplitAdapter.replace(statsViewData.splitData)
    }

    private fun setStreaksViewData(
        viewData: StatisticsDetailStreaksViewData,
    ) = with(binding) {
        cardStatisticsDetailStreaks.items = viewData.streaks

        chartStatisticsStreaks.setData(viewData.data)
        chartStatisticsStreaks.isVisible = viewData.showData
        chartStatisticsComparisonStreaks.setData(viewData.compareData)
        chartStatisticsComparisonStreaks.isVisible = viewData.showComparison
        buttonsStatisticsDetailStreaksType.isVisible = viewData.showData

        chartStatisticsStreaksCalendar.isVisible = viewData.showCalendar
        chartStatisticsStreaksCalendar.setData(viewData.calendarData)
        chartStatisticsComparisonStreaksCalendar.isVisible = viewData.showComparisonCalendar
        chartStatisticsComparisonStreaksCalendar.setData(viewData.compareCalendarData)
    }

    private fun updateChartViewData(
        viewData: StatisticsDetailChartCompositeViewData,
    ) = with(binding) {
        val comparisonChartIsVisible = viewData.showComparison &&
            viewData.chartData.visible &&
            viewData.compareChartData.visible

        chartStatisticsDetail.setViewData(viewData.chartData)
        chartStatisticsDetailCompare.setViewData(viewData.compareChartData)
        chartStatisticsDetailCompare.visible = comparisonChartIsVisible

        val chartGroupingData = viewData.chartGroupingViewData
        buttonsStatisticsDetailGrouping.visible = chartGroupingData.size > 1
        buttonsStatisticsDetailGrouping.adapter.replace(chartGroupingData)

        val chartLengthData = viewData.chartLengthViewData
        buttonsStatisticsDetailLength.visible = chartLengthData.isNotEmpty()
        buttonsStatisticsDetailLength.adapter.replace(chartLengthData)

        val rangeAveragesData = viewData.rangeAverages
        cardStatisticsDetailRangeAverage.itemsDescription = viewData.rangeAveragesTitle
        cardStatisticsDetailRangeAverage.visible = rangeAveragesData.isNotEmpty()
        cardStatisticsDetailRangeAverage.items = rangeAveragesData
    }

    private fun setEmptyRangeAveragesData(
        viewData: List<StatisticsDetailCardViewData>,
    ) = with(binding) {
        cardStatisticsDetailRangeAverage.itemsDescription = " "
        cardStatisticsDetailRangeAverage.visible = true
        cardStatisticsDetailRangeAverage.items = viewData
    }

    private fun updateSplitChartGroupingData(
        viewData: List<ViewHolderType>,
    ) = with(binding.buttonsStatisticsDetailSplitGrouping) {
        visible = viewData.isNotEmpty()
        adapter.replace(viewData)
    }

    private fun updateSplitChartViewData(
        viewData: StatisticsDetailChartViewData,
    ) {
        binding.tvStatisticsDetailSplitHint.visible = viewData.visible
        binding.chartStatisticsDetailSplit.setViewData(viewData)
    }

    private fun updateComparisonSplitChartViewData(
        viewData: StatisticsDetailChartViewData,
    ) {
        binding.chartStatisticsDetailComparisonSplit.setViewData(viewData)
    }

    private fun updateDurationSplitChartViewData(
        viewData: StatisticsDetailChartViewData,
    ) {
        binding.tvStatisticsDetailDurationSplitHint.visible = viewData.visible
        binding.chartStatisticsDetailDurationSplit.setViewData(viewData)
    }

    private fun updateComparisonDurationSplitChartViewData(
        viewData: StatisticsDetailChartViewData,
    ) {
        binding.chartStatisticsDetailDurationSplitCompare.setViewData(viewData)
    }

    private fun updateNextActivitiesViewData(
        viewData: List<ViewHolderType>,
    ) {
        binding.rvStatisticsDetailNextActivities.isVisible = viewData.isNotEmpty()
        nextActivitiesAdapter.replace(viewData)
    }

    private fun updateRangeItems(viewData: RangesViewData) = with(binding) {
        spinnerStatisticsDetail.setData(viewData.items, viewData.selectedPosition)
    }

    private fun updateRangeButtonsVisibility(isVisible: Boolean) = with(binding) {
        btnStatisticsDetailPrevious.visible = isVisible
        btnStatisticsDetailNext.visible = isVisible
    }

    private fun BarChartView.setViewData(
        viewData: StatisticsDetailChartViewData,
    ) {
        visible = viewData.visible
        setBars(viewData.data)
        setLegendTextSuffix(viewData.legendSuffix)
        shouldAddLegendToSelectedBar(viewData.addLegendToSelectedBar)
        shouldDrawHorizontalLegends(viewData.shouldDrawHorizontalLegends)
        setGoalValue(viewData.goalValue)
    }

    companion object {
        private const val ARGS_PARAMS = "args_params"

        fun createBundle(data: StatisticsDetailParams): Bundle = Bundle().apply {
            putParcelable(ARGS_PARAMS, data)
        }
    }
}
