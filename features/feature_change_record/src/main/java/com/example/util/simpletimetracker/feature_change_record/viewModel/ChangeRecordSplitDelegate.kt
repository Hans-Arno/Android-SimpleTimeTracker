package com.example.util.simpletimetracker.feature_change_record.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.util.simpletimetracker.core.extension.set
import com.example.util.simpletimetracker.domain.interactor.AddRecordMediator
import com.example.util.simpletimetracker.domain.model.Record
import com.example.util.simpletimetracker.feature_base_adapter.ViewHolderType
import com.example.util.simpletimetracker.feature_change_record.interactor.ChangeRecordViewDataInteractor
import com.example.util.simpletimetracker.feature_change_record.mapper.ChangeRecordViewDataMapper
import com.example.util.simpletimetracker.feature_change_record.viewData.ChangeRecordPreview
import javax.inject.Inject

interface ChangeRecordSplitDelegate {
    val timeSplitText: LiveData<String>
    val splitPreview: LiveData<ChangeRecordPreview>
    val timeSplitAdjustmentItems: LiveData<List<ViewHolderType>>
}

class ChangeRecordSplitDelegateImpl @Inject constructor(
    private val changeRecordViewDataInteractor: ChangeRecordViewDataInteractor,
    private val addRecordMediator: AddRecordMediator,
    private val changeRecordViewDataMapper: ChangeRecordViewDataMapper,
) : ChangeRecordSplitDelegate {

    override val timeSplitAdjustmentItems: LiveData<List<ViewHolderType>> by lazy {
        MutableLiveData(loadTimeSplitAdjustmentItems())
    }
    override val timeSplitText: LiveData<String> = MutableLiveData()
    override val splitPreview: LiveData<ChangeRecordPreview> =
        MutableLiveData(ChangeRecordPreview.NotAvailable)

    suspend fun onSplitClickDelegate(
        newTypeId: Long,
        newTimeStarted: Long,
        newTimeSplit: Long,
        newComment: String,
        newCategoryIds: List<Long>,
        onSplitComplete: () -> Unit,
    ) {
        Record(
            id = 0L, // Zero id creates new record
            typeId = newTypeId,
            timeStarted = newTimeStarted,
            timeEnded = newTimeSplit,
            comment = newComment,
            tagIds = newCategoryIds,
        ).let {
            addRecordMediator.add(it)
        }
        onSplitComplete()
    }

    suspend fun updateTimeSplitValue(
        newTimeSplit: Long,
    ) {
        val data = loadTimeSplitValue(newTimeSplit)
        timeSplitText.set(data)
    }

    suspend fun updateSplitPreviewViewData(
        newTypeId: Long,
        newTimeStarted: Long,
        newTimeSplit: Long,
        newTimeEnded: Long,
        showTimeEnded: Boolean,
    ) {
        val data = loadSplitPreviewViewData(
            newTypeId = newTypeId,
            newTimeStarted = newTimeStarted,
            newTimeSplit = newTimeSplit,
            newTimeEnded = newTimeEnded,
            showTimeEnded = showTimeEnded,
        )
        splitPreview.set(data)
    }

    private fun loadTimeSplitAdjustmentItems(): List<ViewHolderType> {
        return changeRecordViewDataInteractor.getTimeAdjustmentItems()
    }

    private suspend fun loadTimeSplitValue(
        newTimeSplit: Long,
    ): String {
        return changeRecordViewDataInteractor.mapTime(newTimeSplit)
    }

    private suspend fun loadSplitPreviewViewData(
        newTypeId: Long,
        newTimeStarted: Long,
        newTimeSplit: Long,
        newTimeEnded: Long,
        showTimeEnded: Boolean,
    ): ChangeRecordPreview {
        val firstRecord = Record(
            typeId = newTypeId,
            timeStarted = newTimeStarted,
            timeEnded = newTimeSplit,
            comment = "",
        ).let {
            changeRecordViewDataInteractor.getPreviewViewData(it)
        }
        val secondRecord = Record(
            typeId = newTypeId,
            timeStarted = newTimeSplit,
            timeEnded = newTimeEnded,
            comment = "",
        ).let {
            changeRecordViewDataInteractor.getPreviewViewData(it)
        }

        return ChangeRecordPreview.Available(
            id = 0,
            before = changeRecordViewDataMapper.mapSimple(
                preview = firstRecord,
                showTimeEnded = true,
                timeStartedChanged = false,
                timeEndedChanged = true,
            ),
            after = changeRecordViewDataMapper.mapSimple(
                preview = secondRecord,
                showTimeEnded = showTimeEnded,
                timeStartedChanged = true,
                timeEndedChanged = false,
            ),
        )
    }
}