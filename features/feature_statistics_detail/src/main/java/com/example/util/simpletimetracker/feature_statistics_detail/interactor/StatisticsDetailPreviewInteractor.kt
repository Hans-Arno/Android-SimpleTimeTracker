package com.example.util.simpletimetracker.feature_statistics_detail.interactor

import com.example.util.simpletimetracker.core.interactor.RecordFilterInteractor
import com.example.util.simpletimetracker.domain.extension.getCategoryItems
import com.example.util.simpletimetracker.domain.extension.getSelectedTags
import com.example.util.simpletimetracker.domain.extension.getTypeIds
import com.example.util.simpletimetracker.domain.extension.hasActivityFilter
import com.example.util.simpletimetracker.domain.extension.hasCategoryFilter
import com.example.util.simpletimetracker.domain.extension.hasMultitaskFilter
import com.example.util.simpletimetracker.domain.extension.hasSelectedTagsFilter
import com.example.util.simpletimetracker.domain.extension.hasUntrackedFilter
import com.example.util.simpletimetracker.domain.interactor.CategoryInteractor
import com.example.util.simpletimetracker.domain.interactor.PrefsInteractor
import com.example.util.simpletimetracker.domain.interactor.RecordTagInteractor
import com.example.util.simpletimetracker.domain.interactor.RecordTypeInteractor
import com.example.util.simpletimetracker.domain.model.Category
import com.example.util.simpletimetracker.domain.model.RecordTag
import com.example.util.simpletimetracker.domain.model.RecordType
import com.example.util.simpletimetracker.domain.model.RecordsFilter
import com.example.util.simpletimetracker.feature_base_adapter.ViewHolderType
import com.example.util.simpletimetracker.feature_statistics_detail.mapper.StatisticsDetailViewDataMapper
import com.example.util.simpletimetracker.feature_statistics_detail.viewData.StatisticsDetailPreviewCompareViewData
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StatisticsDetailPreviewInteractor @Inject constructor(
    private val prefsInteractor: PrefsInteractor,
    private val recordTypeInteractor: RecordTypeInteractor,
    private val categoryInteractor: CategoryInteractor,
    private val recordTagInteractor: RecordTagInteractor,
    private val recordFilterInteractor: RecordFilterInteractor,
    private val statisticsDetailViewDataMapper: StatisticsDetailViewDataMapper,
) {

    suspend fun getPreviewData(
        filterParams: List<RecordsFilter>,
        isForComparison: Boolean,
    ): List<ViewHolderType> = withContext(Dispatchers.Default) {
        val isDarkTheme = prefsInteractor.getDarkMode()

        suspend fun mapActivities(
            selectedIds: List<Long>,
        ): List<ViewHolderType> {
            return recordTypeInteractor.getAll()
                .filter { it.id in selectedIds }
                .mapIndexed { index, type ->
                    statisticsDetailViewDataMapper.mapToPreview(
                        recordType = type,
                        isDarkTheme = isDarkTheme,
                        isFirst = index == 0,
                        isForComparison = isForComparison,
                    )
                }
        }

        val viewData = when {
            filterParams.hasUntrackedFilter() -> {
                statisticsDetailViewDataMapper.mapUntrackedPreview(
                    isDarkTheme = isDarkTheme,
                    isForComparison = isForComparison,
                ).let(::listOf)
            }
            filterParams.hasMultitaskFilter() -> {
                statisticsDetailViewDataMapper.mapMultitaskPreview(
                    isDarkTheme = isDarkTheme,
                    isForComparison = isForComparison,
                ).let(::listOf)
            }
            filterParams.hasActivityFilter() -> {
                val selectedIds = filterParams.getTypeIds()
                mapActivities(selectedIds)
            }
            filterParams.hasCategoryFilter() -> {
                val selectedCategories = filterParams.getCategoryItems()
                val categories = categoryInteractor.getAll().associateBy(Category::id)
                selectedCategories.mapNotNull {
                    when (it) {
                        is RecordsFilter.CategoryItem.Categorized -> {
                            val category = categories[it.categoryId] ?: return@mapNotNull null
                            statisticsDetailViewDataMapper.mapToCategorizedPreview(
                                category = category,
                                isDarkTheme = isDarkTheme,
                                isForComparison = isForComparison,
                            )
                        }
                        is RecordsFilter.CategoryItem.Uncategorized -> {
                            statisticsDetailViewDataMapper.mapToUncategorizedPreview(
                                isDarkTheme = isDarkTheme,
                                isForComparison = isForComparison,
                            )
                        }
                    }
                }
            }
            filterParams.hasSelectedTagsFilter() -> {
                val selectedTags = filterParams.getSelectedTags()
                val types = recordTypeInteractor.getAll().associateBy(RecordType::id)
                val tags = recordTagInteractor.getAll().associateBy(RecordTag::id)
                selectedTags.mapNotNull {
                    when (it) {
                        is RecordsFilter.TagItem.Tagged -> {
                            val tag = tags[it.tagId] ?: return@mapNotNull null
                            statisticsDetailViewDataMapper.mapToTaggedPreview(
                                tag = tag,
                                type = types[tag.typeId],
                                isDarkTheme = isDarkTheme,
                                isForComparison = isForComparison,
                            )
                        }
                        is RecordsFilter.TagItem.Untagged -> {
                            statisticsDetailViewDataMapper.mapToUntaggedPreview(
                                isDarkTheme = isDarkTheme,
                                isForComparison = isForComparison,
                            )
                        }
                    }
                }
            }
            else -> {
                val records = recordFilterInteractor.getByFilter(filterParams)
                val selectedIds = records.map { it.typeIds }.flatten().distinct()
                mapActivities(selectedIds)
            }
        }

        return@withContext if (isForComparison) {
            buildComparisonViewData(viewData)
        } else {
            buildFilterViewData(viewData, isDarkTheme)
        }
    }

    private fun buildFilterViewData(
        viewData: List<ViewHolderType>,
        isDarkTheme: Boolean,
    ): List<ViewHolderType> {
        return when {
            viewData.isEmpty() -> {
                statisticsDetailViewDataMapper
                    .mapToPreviewEmpty(isDarkTheme)
                    .let(::listOf)
            }
            else -> {
                viewData
            }
        }
    }

    private fun buildComparisonViewData(
        viewData: List<ViewHolderType>,
    ): List<ViewHolderType> {
        return if (viewData.isEmpty()) {
            viewData
        } else {
            StatisticsDetailPreviewCompareViewData.let(::listOf) + viewData
        }
    }
}