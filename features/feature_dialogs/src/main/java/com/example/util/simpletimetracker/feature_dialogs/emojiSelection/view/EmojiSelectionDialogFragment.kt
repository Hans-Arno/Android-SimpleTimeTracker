package com.example.util.simpletimetracker.feature_dialogs.emojiSelection.view

import com.example.util.simpletimetracker.feature_dialogs.databinding.EmojiSelectionDialogFragmentBinding as Binding
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.example.util.simpletimetracker.core.base.BaseBottomSheetFragment
import com.example.util.simpletimetracker.core.dialog.EmojiSelectionDialogListener
import com.example.util.simpletimetracker.core.extension.getAllFragments
import com.example.util.simpletimetracker.core.extension.setSkipCollapsed
import com.example.util.simpletimetracker.core.utils.fragmentArgumentDelegate
import com.example.util.simpletimetracker.feature_base_adapter.BaseRecyclerAdapter
import com.example.util.simpletimetracker.feature_base_adapter.emoji.createEmojiAdapterDelegate
import com.example.util.simpletimetracker.feature_base_adapter.loader.createLoaderAdapterDelegate
import com.example.util.simpletimetracker.feature_dialogs.emojiSelection.viewModel.EmojiSelectionViewModel
import com.example.util.simpletimetracker.navigation.params.screen.EmojiSelectionDialogParams
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EmojiSelectionDialogFragment : BaseBottomSheetFragment<Binding>() {

    override val inflater: (LayoutInflater, ViewGroup?, Boolean) -> Binding =
        Binding::inflate

    private val viewModel: EmojiSelectionViewModel by viewModels()

    private val adapter: BaseRecyclerAdapter by lazy {
        BaseRecyclerAdapter(
            createLoaderAdapterDelegate(),
            createEmojiAdapterDelegate(viewModel::onEmojiClick),
        )
    }

    private val params: EmojiSelectionDialogParams by fragmentArgumentDelegate(
        key = ARGS_PARAMS, default = EmojiSelectionDialogParams(),
    )
    private var emojiSelectionDialogListener: EmojiSelectionDialogListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        when (context) {
            is EmojiSelectionDialogListener -> {
                emojiSelectionDialogListener = context
                return
            }
            is AppCompatActivity -> {
                context.getAllFragments()
                    .firstOrNull { it is EmojiSelectionDialogListener && it.isResumed }
                    ?.let { emojiSelectionDialogListener = it as? EmojiSelectionDialogListener }
            }
        }
    }

    override fun initDialog() {
        setSkipCollapsed()
    }

    override fun initUi() {
        binding.rvEmojiSelectionContainer.apply {
            layoutManager = FlexboxLayoutManager(requireContext()).apply {
                flexDirection = FlexDirection.ROW
                justifyContent = JustifyContent.CENTER
                flexWrap = FlexWrap.WRAP
            }
            adapter = this@EmojiSelectionDialogFragment.adapter
        }
    }

    override fun initViewModel(): Unit = with(viewModel) {
        extra = params
        icons.observe(adapter::replace)
        iconSelected.observe(::onEmojiSelected)
    }

    private fun onEmojiSelected(emojiText: String) {
        emojiSelectionDialogListener?.onEmojiSelected(emojiText)
        dismiss()
    }

    companion object {
        private const val ARGS_PARAMS = "args_params"

        fun createBundle(data: EmojiSelectionDialogParams): Bundle = Bundle().apply {
            putParcelable(ARGS_PARAMS, data)
        }
    }
}