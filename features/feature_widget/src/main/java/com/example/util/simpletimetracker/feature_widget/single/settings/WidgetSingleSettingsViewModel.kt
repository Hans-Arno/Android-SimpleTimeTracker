package com.example.util.simpletimetracker.feature_widget.single.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.util.simpletimetracker.feature_base_adapter.ViewHolderType
import com.example.util.simpletimetracker.domain.interactor.WidgetInteractor
import com.example.util.simpletimetracker.core.mapper.RecordTypeViewDataMapper
import com.example.util.simpletimetracker.feature_base_adapter.recordType.RecordTypeViewData
import com.example.util.simpletimetracker.domain.interactor.PrefsInteractor
import com.example.util.simpletimetracker.domain.interactor.RecordTypeInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WidgetSingleSettingsViewModel @Inject constructor(
    private val recordTypeInteractor: RecordTypeInteractor,
    private val prefsInteractor: PrefsInteractor,
    private val widgetInteractor: WidgetInteractor,
    private val recordTypeViewDataMapper: RecordTypeViewDataMapper
) : ViewModel() {

    lateinit var extra: WidgetSingleSettingsExtra

    val recordTypes: LiveData<List<ViewHolderType>> by lazy {
        MutableLiveData<List<ViewHolderType>>().let { initial ->
            viewModelScope.launch { initial.value = loadRecordTypesViewData() }
            initial
        }
    }
    val handled: LiveData<Int> = MutableLiveData()

    fun onRecordTypeClick(item: RecordTypeViewData) {
        viewModelScope.launch {
            prefsInteractor.setWidget(extra.widgetId, item.id)
            widgetInteractor.updateWidget(extra.widgetId)
            (handled as MutableLiveData).value = extra.widgetId
        }
    }

    private suspend fun loadRecordTypesViewData(): List<ViewHolderType> {
        val isDarkTheme = prefsInteractor.getDarkMode()

        return recordTypeInteractor.getAll()
            .filter { !it.hidden }
            .map { recordTypeViewDataMapper.map(it, isDarkTheme) }
            .takeUnless { it.isEmpty() }
            ?: recordTypeViewDataMapper.mapToEmpty()
    }
}