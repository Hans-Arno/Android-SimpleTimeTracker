package com.example.util.simpletimetracker.feature_complex_rules.interactor

import com.example.util.simpletimetracker.domain.interactor.ComplexRuleInteractor
import com.example.util.simpletimetracker.domain.interactor.PrefsInteractor
import com.example.util.simpletimetracker.domain.interactor.RecordTagInteractor
import com.example.util.simpletimetracker.domain.interactor.RecordTypeInteractor
import com.example.util.simpletimetracker.domain.model.ComplexRule
import com.example.util.simpletimetracker.feature_base_adapter.ViewHolderType
import com.example.util.simpletimetracker.feature_base_adapter.hint.HintViewData
import com.example.util.simpletimetracker.feature_complex_rules.mapper.ComplexRulesViewDataMapper
import javax.inject.Inject

class ComplexRulesViewDataInteractor @Inject constructor(
    private val prefsInteractor: PrefsInteractor,
    private val recordTagInteractor: RecordTagInteractor,
    private val recordTypeInteractor: RecordTypeInteractor,
    private val complexRuleInteractor: ComplexRuleInteractor,
    private val complexRulesViewDataMapper: ComplexRulesViewDataMapper,
) {

    suspend fun getViewData(): List<ViewHolderType> {
        val isDarkTheme = prefsInteractor.getDarkMode()
        val rules = complexRuleInteractor.getAll()
        val result: MutableList<ViewHolderType> = mutableListOf()
        val typesMap = recordTypeInteractor.getAll().associateBy { it.id }
        val tagsMap = recordTagInteractor.getAll().associateBy { it.id }

        val sortByActionsList = listOf(
            ComplexRule.Action.AllowMultitasking,
            ComplexRule.Action.DisallowMultitasking,
            ComplexRule.Action.AssignTag,
        )
        // TODO RULES add negate of rule (ex. Set tag on starting Read while Not Commute).

        val ruleGroups = rules
            .groupBy { it.action }
        val shouldAddHints = ruleGroups.size > 1

        result += complexRulesViewDataMapper.mapAddItem(isDarkTheme)

        sortByActionsList.forEach {
            val group = ruleGroups[it] ?: return@forEach
            if (shouldAddHints) {
                val anyAction = group.firstOrNull()?.action
                if (anyAction != null) {
                    val hint = complexRulesViewDataMapper.mapBaseTitle(anyAction)
                    result += HintViewData(hint)
                }
            }
            group.forEach { rule ->
                result += complexRulesViewDataMapper.mapRule(
                    rule = rule,
                    isDarkTheme = isDarkTheme,
                    typesMap = typesMap,
                    tagsMap = tagsMap,
                )
            }
        }

        return result
    }
}