package com.example.util.simpletimetracker.feature_dialogs.cardSize.view

import com.example.util.simpletimetracker.feature_dialogs.databinding.CardSizeDialogFragmentBinding as Binding
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.util.simpletimetracker.core.base.BaseBottomSheetFragment
import com.example.util.simpletimetracker.core.extension.setSkipCollapsed
import com.example.util.simpletimetracker.feature_base_adapter.BaseRecyclerAdapter
import com.example.util.simpletimetracker.feature_base_adapter.empty.createEmptyAdapterDelegate
import com.example.util.simpletimetracker.feature_base_adapter.loader.createLoaderAdapterDelegate
import com.example.util.simpletimetracker.feature_base_adapter.recordType.createRecordTypeAdapterDelegate
import com.example.util.simpletimetracker.feature_dialogs.cardSize.viewData.CardSizeDefaultButtonViewData
import com.example.util.simpletimetracker.feature_dialogs.cardSize.viewModel.CardSizeViewModel
import com.example.util.simpletimetracker.feature_views.extension.setOnClick
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CardSizeDialogFragment : BaseBottomSheetFragment<Binding>() {

    override val inflater: (LayoutInflater, ViewGroup?, Boolean) -> Binding =
        Binding::inflate

    private val viewModel: CardSizeViewModel by viewModels()

    private val recordTypesAdapter: BaseRecyclerAdapter by lazy {
        BaseRecyclerAdapter(
            createEmptyAdapterDelegate(),
            createRecordTypeAdapterDelegate(),
            createLoaderAdapterDelegate(),
        )
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        viewModel.onDismiss()
    }

    override fun initDialog() {
        setSkipCollapsed()
    }

    override fun initUi() {
        binding.rvCardSizeContainer.apply {
            layoutManager = FlexboxLayoutManager(requireContext()).apply {
                flexDirection = FlexDirection.ROW
                justifyContent = JustifyContent.CENTER
                flexWrap = FlexWrap.WRAP
            }
            adapter = recordTypesAdapter
        }
    }

    override fun initUx(): Unit = with(binding) {
        buttonsCardSize.listener = viewModel::onButtonClick
        btnCardSizeDefault.setOnClick(viewModel::onDefaultButtonClick)
    }

    override fun initViewModel(): Unit = with(viewModel) {
        recordTypes.observe(recordTypesAdapter::replace)
        buttons.observe(binding.buttonsCardSize.adapter::replace)
        defaultButton.observe(::updateDefaultButton)
    }

    private fun updateDefaultButton(viewData: CardSizeDefaultButtonViewData) {
        binding.btnCardSizeDefault.backgroundTintList = ColorStateList.valueOf(viewData.color)
    }
}