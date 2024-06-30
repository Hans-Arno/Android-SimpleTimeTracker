package com.example.util.simpletimetracker.domain.interactor

import com.example.util.simpletimetracker.domain.model.RunningRecord
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RemoveRunningRecordMediator @Inject constructor(
    private val recordInteractor: RecordInteractor,
    private val runningRecordInteractor: RunningRecordInteractor,
    private val notificationTypeInteractor: NotificationTypeInteractor,
    private val notificationInactivityInteractor: NotificationInactivityInteractor,
    private val notificationActivityInteractor: NotificationActivityInteractor,
    private val notificationGoalTimeInteractor: NotificationGoalTimeInteractor,
    private val widgetInteractor: WidgetInteractor,
    private val wearInteractor: WearInteractor,
    private val prefsInteractor: PrefsInteractor,
    private val activityStartedStoppedBroadcastInteractor: ActivityStartedStoppedBroadcastInteractor,
    private val pomodoroStopInteractor: PomodoroStopInteractor,
) {

    suspend fun removeWithRecordAdd(
        runningRecord: RunningRecord,
        updateWidgets: Boolean = true,
    ) {
        val durationToIgnore = prefsInteractor.getIgnoreShortRecordsDuration()
        val duration = TimeUnit.MILLISECONDS
            .toSeconds(System.currentTimeMillis() - runningRecord.timeStarted)

        if (duration > durationToIgnore || durationToIgnore == 0L) {
            // No need to update widgets and notification because it will be done in running record remove.
            recordInteractor.addFromRunning(runningRecord)
        }
        activityStartedStoppedBroadcastInteractor.onActivityStopped(
            typeId = runningRecord.id,
            tagIds = runningRecord.tagIds,
            comment = runningRecord.comment,
        )
        remove(runningRecord.id, updateWidgets)
        pomodoroStopInteractor.checkAndStop(runningRecord.id)
    }

    suspend fun remove(
        typeId: Long,
        updateWidgets: Boolean,
    ) {
        runningRecordInteractor.remove(typeId)
        notificationTypeInteractor.checkAndHide(typeId)
        notificationInactivityInteractor.checkAndSchedule()
        // Cancel if no activity tracked.
        val runningRecordIds = runningRecordInteractor.getAll().map { it.id }
        if (runningRecordIds.isEmpty()) notificationActivityInteractor.cancel()
        notificationGoalTimeInteractor.checkAndReschedule(runningRecordIds + typeId)
        if (updateWidgets) {
            widgetInteractor.updateWidgets()
            wearInteractor.update()
        }
    }
}