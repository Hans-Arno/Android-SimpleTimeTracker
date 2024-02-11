package com.example.util.simpletimetracker.feature_dialogs.chartFilter.view

import com.example.util.simpletimetracker.feature_dialogs.databinding.ChartFilterDialogFragmentBinding as Binding
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.example.util.simpletimetracker.core.base.BaseBottomSheetFragment
import com.example.util.simpletimetracker.core.dialog.ChartFilterDialogListener
import com.example.util.simpletimetracker.core.extension.blockContentScroll
import com.example.util.simpletimetracker.core.extension.getAllFragments
import com.example.util.simpletimetracker.core.extension.setSkipCollapsed
import com.example.util.simpletimetracker.feature_base_adapter.BaseRecyclerAdapter
import com.example.util.simpletimetracker.feature_base_adapter.category.createCategoryAdapterDelegate
import com.example.util.simpletimetracker.feature_base_adapter.empty.createEmptyAdapterDelegate
import com.example.util.simpletimetracker.feature_base_adapter.loader.createLoaderAdapterDelegate
import com.example.util.simpletimetracker.feature_base_adapter.recordType.createRecordTypeAdapterDelegate
import com.example.util.simpletimetracker.feature_dialogs.chartFilter.viewModel.ChartFilterViewModel
import com.example.util.simpletimetracker.feature_views.extension.setOnClick
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChartFilterDialogFragment : BaseBottomSheetFragment<Binding>() {

    override val inflater: (LayoutInflater, ViewGroup?, Boolean) -> Binding =
        Binding::inflate

    private val viewModel: ChartFilterViewModel by viewModels()

    private val recordTypesAdapter: BaseRecyclerAdapter by lazy {
        BaseRecyclerAdapter(
            createRecordTypeAdapterDelegate(viewModel::onRecordTypeClick),
            createCategoryAdapterDelegate(viewModel::onCategoryClick),
            createLoaderAdapterDelegate(),
            createEmptyAdapterDelegate(),
        )
    }

    private var chartFilterDialogListener: ChartFilterDialogListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        when (context) {
            is ChartFilterDialogListener -> {
                chartFilterDialogListener = context
                return
            }
            is AppCompatActivity -> {
                context.getAllFragments()
                    .firstOrNull { it is ChartFilterDialogListener && it.isResumed }
                    ?.let { chartFilterDialogListener = it as? ChartFilterDialogListener }
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        chartFilterDialogListener?.onChartFilterDialogDismissed()
    }

    override fun initDialog() {
        setSkipCollapsed()
        blockContentScroll(binding.rvChartFilterContainer)
    }

    override fun initUi() {
        binding.rvChartFilterContainer.apply {
            layoutManager = FlexboxLayoutManager(requireContext()).apply {
                flexDirection = FlexDirection.ROW
                justifyContent = JustifyContent.CENTER
                flexWrap = FlexWrap.WRAP
            }
            adapter = recordTypesAdapter
        }
    }

    override fun initUx(): Unit = with(binding) {
        buttonsChartFilterType.listener = viewModel::onFilterTypeClick
        btnChartFilterShowAll.setOnClick(viewModel::onShowAllClick)
        btnChartFilterHideAll.setOnClick(viewModel::onHideAllClick)
    }

    override fun initViewModel(): Unit = with(viewModel) {
        filterTypeViewData.observe(binding.buttonsChartFilterType.adapter::replace)
        types.observe(recordTypesAdapter::replace)
    }
}