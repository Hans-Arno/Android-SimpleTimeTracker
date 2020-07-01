package com.example.util.simpletimetracker

import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.util.simpletimetracker.core.utils.TestUtils
import com.example.util.simpletimetracker.di.AppModule
import com.example.util.simpletimetracker.di.DaggerTestAppComponent
import com.example.util.simpletimetracker.ui.MainActivity
import com.example.util.simpletimetracker.utils.NavUtils
import com.example.util.simpletimetracker.utils.checkViewDoesNotExist
import com.example.util.simpletimetracker.utils.checkViewIsDisplayed
import com.example.util.simpletimetracker.utils.clickOnView
import com.example.util.simpletimetracker.utils.clickOnViewWithId
import com.example.util.simpletimetracker.utils.clickOnViewWithText
import com.example.util.simpletimetracker.utils.typeTextIntoView
import org.hamcrest.CoreMatchers.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
class MainScreenEmptyTest {

    @Inject
    lateinit var testUtils: TestUtils

    @Rule
    @JvmField
    val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setUp() {
        val app = ApplicationProvider.getApplicationContext() as TimeTrackerApp
        DaggerTestAppComponent.builder()
            .appModule(AppModule(app))
            .build()
            .inject(this)

        testUtils.clearDatabase()
    }

    @Test
    fun test() {
        val name = "Test"

        // Empty main
        checkViewDoesNotExist(withText(R.string.running_records_empty))

        // Empty records
        NavUtils.openRecordsScreen()
        checkViewIsDisplayed(allOf(withText(R.string.records_empty), isCompletelyDisplayed()))
        clickOnViewWithId(R.id.btnRecordsContainerPrevious)
        checkViewIsDisplayed(allOf(withText(R.string.records_empty), isCompletelyDisplayed()))
        clickOnViewWithId(R.id.btnRecordsContainerNext)
        clickOnViewWithId(R.id.btnRecordsContainerNext)
        checkViewIsDisplayed(allOf(withText(R.string.records_empty), isCompletelyDisplayed()))

        // Empty statistics
        NavUtils.openStatisticsScreen()
        checkViewIsDisplayed(
            allOf(
                withId(R.id.layoutStatisticsItem),
                hasDescendant(withText(R.string.untracked_time_name)),
                hasDescendant(ViewMatchers.withSubstring("100%")),
                isCompletelyDisplayed()
            )
        )
        clickOnViewWithId(R.id.btnStatisticsContainerNext)
        checkViewIsDisplayed(allOf(withText(R.string.statistics_empty), isCompletelyDisplayed()))

        // Week range
        clickOnViewWithId(R.id.btnStatisticsContainerToday)
        clickOnViewWithText(R.string.title_this_week)
        checkViewIsDisplayed(
            allOf(
                withId(R.id.layoutStatisticsItem),
                hasDescendant(withText(R.string.untracked_time_name)),
                hasDescendant(ViewMatchers.withSubstring("100%")),
                isCompletelyDisplayed()
            )
        )
        clickOnViewWithId(R.id.btnStatisticsContainerNext)
        checkViewIsDisplayed(allOf(withText(R.string.statistics_empty), isCompletelyDisplayed()))

        // Month range
        clickOnViewWithId(R.id.btnStatisticsContainerToday)
        clickOnViewWithText(R.string.title_this_month)
        checkViewIsDisplayed(
            allOf(
                withId(R.id.layoutStatisticsItem),
                hasDescendant(withText(R.string.untracked_time_name)),
                hasDescendant(ViewMatchers.withSubstring("100%")),
                isCompletelyDisplayed()
            )
        )
        clickOnViewWithId(R.id.btnStatisticsContainerNext)
        checkViewIsDisplayed(allOf(withText(R.string.statistics_empty), isCompletelyDisplayed()))

        // Overall range
        clickOnViewWithId(R.id.btnStatisticsContainerToday)
        clickOnViewWithText(R.string.title_overall)
        checkViewIsDisplayed(allOf(withText(R.string.statistics_empty), isCompletelyDisplayed()))

        // Back to day range
        clickOnViewWithId(R.id.btnStatisticsContainerToday)
        clickOnViewWithText(R.string.title_today)
        checkViewIsDisplayed(
            allOf(
                withId(R.id.layoutStatisticsItem),
                hasDescendant(withText(R.string.untracked_time_name)),
                hasDescendant(ViewMatchers.withSubstring("100%")),
                isCompletelyDisplayed()
            )
        )
        clickOnViewWithId(R.id.btnStatisticsContainerNext)
        checkViewIsDisplayed(allOf(withText(R.string.statistics_empty), isCompletelyDisplayed()))

        // Add activity
        NavUtils.openRunningRecordsScreen()
        clickOnView(withText(R.string.running_records_add_type))
        typeTextIntoView(R.id.etChangeRecordTypeName, name)
        pressBack()
        clickOnView(withText(R.string.change_record_type_save))
        checkViewIsDisplayed(withText(R.string.running_records_empty))

        // Start timer
        clickOnViewWithText(name)
        checkViewDoesNotExist(withText(R.string.running_records_empty))
        clickOnView(allOf(isDescendantOfA(withId(R.id.layoutRunningRecordItem)), withText(name)))
        checkViewIsDisplayed(withText(R.string.running_records_empty))
    }
}