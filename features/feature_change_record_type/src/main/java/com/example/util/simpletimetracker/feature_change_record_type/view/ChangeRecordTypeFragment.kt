package com.example.util.simpletimetracker.feature_change_record_type.view

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.util.simpletimetracker.core.base.BaseFragment
import com.example.util.simpletimetracker.core.dialog.ColorSelectionDialogListener
import com.example.util.simpletimetracker.core.dialog.DurationDialogListener
import com.example.util.simpletimetracker.core.dialog.EmojiSelectionDialogListener
import com.example.util.simpletimetracker.core.extension.hideKeyboard
import com.example.util.simpletimetracker.core.extension.observeOnce
import com.example.util.simpletimetracker.core.extension.setSharedTransitions
import com.example.util.simpletimetracker.core.extension.showKeyboard
import com.example.util.simpletimetracker.core.extension.toViewData
import com.example.util.simpletimetracker.core.repo.DeviceRepo
import com.example.util.simpletimetracker.core.utils.fragmentArgumentDelegate
import com.example.util.simpletimetracker.core.view.buttonsRowView.ButtonsRowViewData
import com.example.util.simpletimetracker.domain.model.IconEmojiType
import com.example.util.simpletimetracker.domain.model.IconImageState
import com.example.util.simpletimetracker.domain.model.IconType
import com.example.util.simpletimetracker.feature_base_adapter.BaseRecyclerAdapter
import com.example.util.simpletimetracker.feature_base_adapter.category.createCategoryAdapterDelegate
import com.example.util.simpletimetracker.feature_base_adapter.category.createCategoryAddAdapterDelegate
import com.example.util.simpletimetracker.feature_base_adapter.color.createColorAdapterDelegate
import com.example.util.simpletimetracker.feature_base_adapter.color.createColorPaletteAdapterDelegate
import com.example.util.simpletimetracker.feature_base_adapter.dayOfWeek.createDayOfWeekAdapterDelegate
import com.example.util.simpletimetracker.feature_base_adapter.divider.createDividerAdapterDelegate
import com.example.util.simpletimetracker.feature_base_adapter.emoji.createEmojiAdapterDelegate
import com.example.util.simpletimetracker.feature_base_adapter.empty.createEmptyAdapterDelegate
import com.example.util.simpletimetracker.feature_base_adapter.hint.createHintAdapterDelegate
import com.example.util.simpletimetracker.feature_base_adapter.hintBig.createHintBigAdapterDelegate
import com.example.util.simpletimetracker.feature_base_adapter.info.createInfoAdapterDelegate
import com.example.util.simpletimetracker.feature_base_adapter.loader.LoaderViewData
import com.example.util.simpletimetracker.feature_base_adapter.loader.createLoaderAdapterDelegate
import com.example.util.simpletimetracker.feature_base_adapter.recordType.RecordTypeViewData
import com.example.util.simpletimetracker.feature_change_record_type.R
import com.example.util.simpletimetracker.feature_change_record_type.adapter.createChangeRecordTypeIconAdapterDelegate
import com.example.util.simpletimetracker.feature_change_record_type.adapter.createChangeRecordTypeIconCategoryAdapterDelegate
import com.example.util.simpletimetracker.feature_change_record_type.adapter.createChangeRecordTypeIconCategoryInfoAdapterDelegate
import com.example.util.simpletimetracker.feature_change_record_type.goals.GoalsViewDelegate
import com.example.util.simpletimetracker.feature_change_record_type.viewData.ChangeRecordTypeChooserState
import com.example.util.simpletimetracker.feature_change_record_type.viewData.ChangeRecordTypeChooserState.State.Category
import com.example.util.simpletimetracker.feature_change_record_type.viewData.ChangeRecordTypeChooserState.State.Closed
import com.example.util.simpletimetracker.feature_change_record_type.viewData.ChangeRecordTypeChooserState.State.Color
import com.example.util.simpletimetracker.feature_change_record_type.viewData.ChangeRecordTypeChooserState.State.GoalTime
import com.example.util.simpletimetracker.feature_change_record_type.viewData.ChangeRecordTypeChooserState.State.Icon
import com.example.util.simpletimetracker.feature_change_record_type.viewData.ChangeRecordTypeGoalsViewData
import com.example.util.simpletimetracker.feature_change_record_type.viewData.ChangeRecordTypeIconCategoryInfoViewData
import com.example.util.simpletimetracker.feature_change_record_type.viewData.ChangeRecordTypeIconSelectorStateViewData
import com.example.util.simpletimetracker.feature_change_record_type.viewData.ChangeRecordTypeIconStateViewData
import com.example.util.simpletimetracker.feature_change_record_type.viewData.ChangeRecordTypeIconSwitchViewData
import com.example.util.simpletimetracker.feature_change_record_type.viewData.ChangeRecordTypeScrollViewData
import com.example.util.simpletimetracker.feature_change_record_type.viewModel.ChangeRecordTypeViewModel
import com.example.util.simpletimetracker.feature_views.extension.addOnScrollListenerAdapter
import com.example.util.simpletimetracker.feature_views.extension.dpToPx
import com.example.util.simpletimetracker.feature_views.extension.pxToDp
import com.example.util.simpletimetracker.feature_views.extension.setOnClick
import com.example.util.simpletimetracker.feature_views.extension.setSpanSizeLookup
import com.example.util.simpletimetracker.feature_views.extension.visible
import com.example.util.simpletimetracker.feature_views.viewData.RecordTypeIcon
import com.example.util.simpletimetracker.navigation.params.screen.ChangeRecordTypeParams
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.appbar.AppBarLayout
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.max
import com.example.util.simpletimetracker.feature_change_record_type.databinding.ChangeRecordTypeFragmentBinding as Binding

@AndroidEntryPoint
class ChangeRecordTypeFragment :
    BaseFragment<Binding>(),
    DurationDialogListener,
    EmojiSelectionDialogListener,
    ColorSelectionDialogListener {

    override val inflater: (LayoutInflater, ViewGroup?, Boolean) -> Binding =
        Binding::inflate

    @Inject
    lateinit var deviceRepo: DeviceRepo

    private val viewModel: ChangeRecordTypeViewModel by viewModels()

    private val colorsAdapter: BaseRecyclerAdapter by lazy {
        BaseRecyclerAdapter(
            createColorAdapterDelegate(viewModel::onColorClick),
            createColorPaletteAdapterDelegate(viewModel::onColorPaletteClick),
        )
    }
    private val iconsAdapter: BaseRecyclerAdapter by lazy {
        BaseRecyclerAdapter(
            createLoaderAdapterDelegate(),
            createChangeRecordTypeIconAdapterDelegate(viewModel::onIconClick),
            createEmojiAdapterDelegate(viewModel::onEmojiClick),
            createChangeRecordTypeIconCategoryInfoAdapterDelegate(),
        )
    }
    private val iconCategoriesAdapter: BaseRecyclerAdapter by lazy {
        BaseRecyclerAdapter(
            createChangeRecordTypeIconCategoryAdapterDelegate {
                viewModel.onIconCategoryClick(it)
                binding.rvChangeRecordTypeIcon.stopScroll()
            },
        )
    }
    private val categoriesAdapter: BaseRecyclerAdapter by lazy {
        BaseRecyclerAdapter(
            createCategoryAdapterDelegate(
                onClick = viewModel::onCategoryClick,
                onLongClickWithTransition = viewModel::onCategoryLongClick,
            ),
            createCategoryAddAdapterDelegate { throttle(viewModel::onAddCategoryClick).invoke() },
            createDividerAdapterDelegate(),
            createInfoAdapterDelegate(),
            createHintAdapterDelegate(),
            createHintBigAdapterDelegate(),
            createEmptyAdapterDelegate(),
        )
    }
    private val dailyGoalDayOfWeekAdapter: BaseRecyclerAdapter by lazy {
        BaseRecyclerAdapter(
            createDayOfWeekAdapterDelegate(viewModel::onDayOfWeekClick),
        )
    }
    private var iconsLayoutManager: GridLayoutManager? = null
    private val params: ChangeRecordTypeParams by fragmentArgumentDelegate(
        key = ARGS_PARAMS,
        default = ChangeRecordTypeParams.New(ChangeRecordTypeParams.SizePreview()),
    )

    override fun initUi(): Unit = with(binding) {
        postponeEnterTransition()

        setPreview()

        setSharedTransitions(
            additionalCondition = { params !is ChangeRecordTypeParams.New },
            transitionName = (params as? ChangeRecordTypeParams.Change)?.transitionName.orEmpty(),
            sharedView = previewChangeRecordType,
        )

        rvChangeRecordTypeColor.apply {
            layoutManager = FlexboxLayoutManager(requireContext()).apply {
                flexDirection = FlexDirection.ROW
                justifyContent = JustifyContent.CENTER
                flexWrap = FlexWrap.WRAP
            }
            adapter = colorsAdapter
        }

        rvChangeRecordTypeIcon.apply {
            layoutManager = GridLayoutManager(requireContext(), getIconsColumnCount())
                .also { iconsLayoutManager = it }
            adapter = iconsAdapter
            itemAnimator = null
            setIconsSpanSize()
        }

        rvChangeRecordTypeIconCategory.apply {
            layoutManager = GridLayoutManager(requireContext(), IconEmojiType.values().size)
            adapter = iconCategoriesAdapter
            itemAnimator = null
        }

        rvChangeRecordTypeCategories.apply {
            layoutManager = FlexboxLayoutManager(requireContext()).apply {
                flexDirection = FlexDirection.ROW
                justifyContent = JustifyContent.CENTER
                flexWrap = FlexWrap.WRAP
            }
            adapter = categoriesAdapter
        }

        GoalsViewDelegate.initGoalUi(
            layout = binding.layoutChangeRecordTypeGoals,
            dayOfWeekAdapter = dailyGoalDayOfWeekAdapter,
        )

        setOnPreDrawListener {
            startPostponedEnterTransition()
        }
    }

    override fun initUx() = with(binding) {
        etChangeRecordTypeName.doAfterTextChanged { viewModel.onNameChange(it.toString()) }
        fieldChangeRecordTypeColor.setOnClick(viewModel::onColorChooserClick)
        fieldChangeRecordTypeIcon.setOnClick(viewModel::onIconChooserClick)
        fieldChangeRecordTypeCategory.setOnClick(viewModel::onCategoryChooserClick)
        fieldChangeRecordTypeGoalTime.setOnClick(viewModel::onGoalTimeChooserClick)
        btnChangeRecordTypeSave.setOnClick(viewModel::onSaveClick)
        btnChangeRecordTypeDelete.setOnClick(viewModel::onDeleteClick)
        btnChangeRecordTypeIconSwitch.listener = {
            updateIconContainerScroll(it)
            viewModel.onIconTypeClick(it)
        }
        etChangeRecordTypeIconSearch.doAfterTextChanged { viewModel.onIconImageSearch(it.toString()) }
        btnChangeRecordTypeIconSearch.setOnClick(viewModel::onIconImageSearchClicked)
        GoalsViewDelegate.initGoalUx(
            viewModel = viewModel,
            layout = layoutChangeRecordTypeGoals,
        )
        rvChangeRecordTypeIcon.addOnScrollListenerAdapter(
            onScrolled = { _, _, _ ->
                iconsLayoutManager?.let {
                    viewModel.onIconsScrolled(
                        firstVisiblePosition = it.findFirstCompletelyVisibleItemPosition(),
                        lastVisiblePosition = it.findLastCompletelyVisibleItemPosition(),
                    )
                }
            },
        )
    }

    override fun initViewModel(): Unit = with(binding) {
        with(viewModel) {
            extra = params
            deleteIconVisibility.observeOnce(viewLifecycleOwner, btnChangeRecordTypeDelete::isVisible::set)
            saveButtonEnabled.observe(btnChangeRecordTypeSave::setEnabled)
            deleteButtonEnabled.observe(btnChangeRecordTypeDelete::setEnabled)
            recordType.observeOnce(viewLifecycleOwner, ::updateUi)
            recordType.observe(::updatePreview)
            colors.observe(colorsAdapter::replace)
            icons.observe(::updateIconsState)
            iconCategories.observe(iconCategoriesAdapter::replaceAsNew)
            iconsTypeViewData.observe(btnChangeRecordTypeIconSwitch.adapter::replace)
            iconSelectorViewData.observe(::updateIconSelectorViewData)
            categories.observe(categoriesAdapter::replace)
            goalsViewData.observe(::updateGoalsState)
            notificationsHintVisible.observe(
                layoutChangeRecordTypeGoals.containerChangeRecordTypeGoalNotificationsHint::visible::set,
            )
            chooserState.observe(::updateChooserState)
            keyboardVisibility.observe { visible ->
                if (visible) showKeyboard(etChangeRecordTypeName) else hideKeyboard()
            }
            iconsScrollPosition.observe {
                if (it is ChangeRecordTypeScrollViewData.ScrollTo) {
                    iconsLayoutManager?.scrollToPositionWithOffset(it.position, 0)
                    onScrolled()
                }
            }
            expandIconTypeSwitch.observe { appBarChangeRecordTypeIcon.setExpanded(true) }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onVisible()
        GoalsViewDelegate.onResume(binding.layoutChangeRecordTypeGoals)
    }

    override fun onDurationSet(duration: Long, tag: String?) {
        viewModel.onDurationSet(
            tag = tag,
            duration = duration,
            anchor = binding.btnChangeRecordTypeSave,
        )
    }

    override fun onDisable(tag: String?) {
        viewModel.onDurationDisabled(tag)
    }

    override fun onEmojiSelected(emojiText: String) {
        viewModel.onEmojiSelected(emojiText)
    }

    override fun onColorSelected(colorInt: Int) {
        viewModel.onCustomColorSelected(colorInt)
    }

    private fun updateUi(item: RecordTypeViewData) = with(binding) {
        etChangeRecordTypeName.setText(item.name)
        etChangeRecordTypeName.setSelection(item.name.length)
        (item.iconId as? RecordTypeIcon.Text)?.text?.let {
            etChangeRecordTypeIconText.setText(it)
        }
        // Set listener only after text is set to avoid trigger on screen return.
        etChangeRecordTypeIconText.doAfterTextChanged { viewModel.onIconTextChange(it.toString()) }
    }

    private fun updatePreview(item: RecordTypeViewData) {
        with(binding.previewChangeRecordType) {
            itemName = item.name
            itemIcon = item.iconId
            itemColor = item.color
        }
    }

    private fun setPreview() {
        val maxWidth = resources.displayMetrics.widthPixels.pxToDp() - DELETE_BUTTON_SIZE

        with(binding.previewChangeRecordType) {
            itemIsRow = params.sizePreview.asRow
            layoutParams = layoutParams.also { layoutParams ->
                params.sizePreview.width?.coerceAtMost(maxWidth)?.dpToPx()?.let { layoutParams.width = it }
                params.sizePreview.height?.dpToPx()?.let { layoutParams.height = it }
            }

            (params as? ChangeRecordTypeParams.Change)?.preview?.let {
                itemName = it.name
                itemIcon = it.iconId.toViewData()
                itemColor = it.color
            }
        }
    }

    private fun updateChooserState(state: ChangeRecordTypeChooserState) = with(binding) {
        GoalsViewDelegate.updateChooser<Color>(
            state = state,
            chooserData = rvChangeRecordTypeColor,
            chooserView = fieldChangeRecordTypeColor,
            chooserArrow = arrowChangeRecordTypeColor,
        )
        GoalsViewDelegate.updateChooser<Icon>(
            state = state,
            chooserData = containerChangeRecordTypeIcon,
            chooserView = fieldChangeRecordTypeIcon,
            chooserArrow = arrowChangeRecordTypeIcon,
        )
        GoalsViewDelegate.updateChooser<Category>(
            state = state,
            chooserData = rvChangeRecordTypeCategories,
            chooserView = fieldChangeRecordTypeCategory,
            chooserArrow = arrowChangeRecordTypeCategory,
        )
        GoalsViewDelegate.updateChooser<GoalTime>(
            state = state,
            chooserData = containerChangeRecordTypeGoalTime,
            chooserView = fieldChangeRecordTypeGoalTime,
            chooserArrow = arrowChangeRecordTypeGoalTime,
        )

        val isClosed = state.current is Closed
        inputChangeRecordTypeName.isVisible = isClosed

        // Chooser fields
        fieldChangeRecordTypeColor.isVisible = isClosed || state.current is Color
        fieldChangeRecordTypeIcon.isVisible = isClosed || state.current is Icon
        fieldChangeRecordTypeCategory.isVisible = isClosed || state.current is Category
        fieldChangeRecordTypeGoalTime.isVisible = isClosed || state.current is GoalTime
    }

    private fun updateGoalsState(state: ChangeRecordTypeGoalsViewData) = with(binding) {
        GoalsViewDelegate.updateGoalsState(
            state = state,
            layout = layoutChangeRecordTypeGoals,
        )
    }

    private fun updateIconsState(state: ChangeRecordTypeIconStateViewData) = with(binding) {
        when (state) {
            is ChangeRecordTypeIconStateViewData.Icons -> {
                rvChangeRecordTypeIcon.isVisible = true
                inputChangeRecordTypeIconText.isVisible = false
                iconsAdapter.replaceAsNew(state.items)
            }
            is ChangeRecordTypeIconStateViewData.Text -> {
                rvChangeRecordTypeIcon.isVisible = false
                inputChangeRecordTypeIconText.isVisible = true
            }
        }
    }

    private fun getIconsColumnCount(): Int {
        val screenWidth = deviceRepo.getScreenWidthInDp().dpToPx()
        val recyclerWidth = screenWidth -
            2 * resources.getDimensionPixelOffset(R.dimen.color_icon_recycler_margin)
        val elementWidth = resources.getDimensionPixelOffset(R.dimen.color_icon_item_width) +
            2 * resources.getDimensionPixelOffset(R.dimen.color_icon_item_margin)
        val columnCount = max(recyclerWidth / elementWidth, 1)

        val rowWidth = elementWidth * columnCount
        val recyclerPadding = (recyclerWidth - rowWidth) / 2
        binding.rvChangeRecordTypeIcon
            .updatePadding(left = recyclerPadding, right = recyclerPadding)

        return columnCount
    }

    private fun setIconsSpanSize() {
        iconsLayoutManager?.setSpanSizeLookup { position ->
            when (iconsAdapter.getItemByPosition(position)) {
                is ChangeRecordTypeIconCategoryInfoViewData,
                is LoaderViewData,
                -> iconsLayoutManager?.spanCount ?: 1
                else -> 1
            }
        }
    }

    private fun updateIconContainerScroll(item: ButtonsRowViewData) = with(binding) {
        if (item !is ChangeRecordTypeIconSwitchViewData) return@with

        val scrollFlags = if (item.iconType == IconType.TEXT) 0
        else AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL

        (btnChangeRecordTypeIconSwitch.layoutParams as? AppBarLayout.LayoutParams)
            ?.scrollFlags = scrollFlags
    }

    private fun updateIconSelectorViewData(
        data: ChangeRecordTypeIconSelectorStateViewData,
    ) = with(binding) {
        if (data is ChangeRecordTypeIconSelectorStateViewData.Available) {
            btnChangeRecordTypeIconSearch.isVisible = data.searchButtonIsVisible
            ivChangeRecordTypeIconSearch.backgroundTintList = ColorStateList.valueOf(data.searchButtonColor)
            rvChangeRecordTypeIconCategory.isVisible = data.state is IconImageState.Chooser
            inputChangeRecordTypeIconSearch.isVisible = data.state is IconImageState.Search
        } else {
            btnChangeRecordTypeIconSearch.isVisible = false
            rvChangeRecordTypeIconCategory.isVisible = false
            inputChangeRecordTypeIconSearch.isVisible = false
        }
    }

    companion object {
        private const val ARGS_PARAMS = "args_params"
        private const val DELETE_BUTTON_SIZE = 72 // TODO get from dimens or viewModel

        fun createBundle(data: ChangeRecordTypeParams): Bundle = Bundle().apply {
            putParcelable(ARGS_PARAMS, data)
        }
    }
}