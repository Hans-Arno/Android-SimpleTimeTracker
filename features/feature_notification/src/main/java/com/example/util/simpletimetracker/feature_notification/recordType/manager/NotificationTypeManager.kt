package com.example.util.simpletimetracker.feature_notification.recordType.manager

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.SystemClock
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.util.simpletimetracker.core.utils.PendingIntents
import com.example.util.simpletimetracker.feature_notification.R
import com.example.util.simpletimetracker.feature_notification.recevier.NotificationReceiver
import com.example.util.simpletimetracker.feature_notification.recordType.customView.NotificationIconView
import com.example.util.simpletimetracker.feature_views.extension.getBitmapFromView
import com.example.util.simpletimetracker.feature_views.extension.measureExactly
import com.example.util.simpletimetracker.feature_views.viewData.RecordTypeIcon
import com.example.util.simpletimetracker.navigation.Router
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationTypeManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val router: Router
) {

    private val notificationManager: NotificationManagerCompat =
        NotificationManagerCompat.from(context)
    private val iconView =
        NotificationIconView(ContextThemeWrapper(context, R.style.AppTheme))
    private val iconSize by lazy {
        context.resources.getDimensionPixelSize(R.dimen.notification_icon_size)
    }

    fun show(params: NotificationTypeParams) {
        val notification: Notification = buildNotification(params)
        createAndroidNotificationChannel()
        notificationManager.notify(params.id.toInt(), notification)
    }

    fun hide(id: Int) {
        notificationManager.cancel(id)
    }

    private fun buildNotification(params: NotificationTypeParams): Notification {
        val startIntent = router.getMainStartIntent().apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val contentIntent = PendingIntent.getActivity(
            context,
            0,
            startIntent,
            PendingIntents.getFlags(),
        )
        val stopIntent = getPendingSelfIntent(
            context = context,
            action = ACTION_NOTIFICATION_STOP,
            recordTypeId = params.id
        )

        val builder = NotificationCompat.Builder(context, NOTIFICATIONS_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(contentIntent)
            .setOngoing(true)
            .setAutoCancel(false)
            .setCustomContentView(prepareView(params, isBig = false))
            .setCustomBigContentView(prepareView(params, isBig = true))
            .addAction(0, params.stopButton, stopIntent)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setPriority(NotificationCompat.PRIORITY_LOW) // no sound
        return builder.build().apply {
            flags = flags or Notification.FLAG_NO_CLEAR
        }
    }

    private fun createAndroidNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATIONS_CHANNEL_ID,
                NOTIFICATIONS_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW // no sound
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun prepareView(
        params: NotificationTypeParams,
        isBig: Boolean,
    ): RemoteViews {
        return RemoteViews(context.packageName, R.layout.notification_record_layout).apply {
            val typesControlsVisibility = if (isBig) View.VISIBLE else View.GONE
            setViewVisibility(R.id.containerNotificationTypes, typesControlsVisibility)
            setViewVisibility(R.id.tvNotificationControlsHint, typesControlsVisibility)

            val tagsControlsVisibility = if (params.tags.isNotEmpty()) View.VISIBLE else View.GONE
            setViewVisibility(R.id.containerNotificationTags, tagsControlsVisibility)

            setTextViewText(R.id.tvNotificationText, params.text)
            setTextViewText(R.id.tvNotificationTimeStarted, params.timeStarted)
            setTextViewText(R.id.tvNotificationGoalTime, params.goalTime)
            setImageViewBitmap(R.id.ivNotificationIcon, getIconBitmap(params.icon, params.color))
            val base = SystemClock.elapsedRealtime() - (System.currentTimeMillis() - params.startedTimeStamp)
            setChronometer(R.id.timerNotification, base, null, true)

            if (isBig) addTypeControls(params)
            if (isBig && params.tags.isNotEmpty()) addTagControls(params)
        }
    }

    private fun RemoteViews.addTypeControls(
        params: NotificationTypeParams,
    ) {
        // Prev button
        getTypeControlView(
            icon = params.controlIconPrev,
            color = params.controlIconColor,
            intent = getPendingSelfIntent(
                context = context,
                action = ACTION_NOTIFICATION_TYPES_PREV,
                recordTypeId = params.id,
                recordTypesShift = (params.typesShift - TYPES_LIST_SIZE)
                    .coerceAtLeast(0),
            )
        ).let {
            addView(R.id.containerNotificationTypes, it)
        }

        // Types buttons
        val currentTypes = params.types.drop(params.typesShift).take(TYPES_LIST_SIZE)
        currentTypes.forEach {
            getTypeControlView(
                icon = it.icon,
                color = it.color,
                intent = getPendingSelfIntent(
                    context = context,
                    action = if (it.id == params.id) {
                        ACTION_NOTIFICATION_STOP
                    } else {
                        ACTION_NOTIFICATION_TYPE_CLICK
                    },
                    requestCode = it.id.toInt(),
                    recordTypeId = params.id,
                    recordTypesShift = params.typesShift,
                    selectedTypeId = it.id,
                )
            ).let {
                addView(R.id.containerNotificationTypes, it)
            }
        }

        // Populate container with empty items to preserve prev next controls position
        repeat(TYPES_LIST_SIZE - currentTypes.size) {
            getTypeControlView(
                icon = null,
                color = null,
                intent = null
            ).let {
                addView(R.id.containerNotificationTypes, it)
            }
        }

        // Next button
        getTypeControlView(
            icon = params.controlIconNext,
            color = params.controlIconColor,
            intent = getPendingSelfIntent(
                context = context,
                action = ACTION_NOTIFICATION_TYPES_NEXT,
                recordTypeId = params.id,
                recordTypesShift = (params.typesShift + TYPES_LIST_SIZE)
                    .takeUnless { it >= params.types.size }
                    ?: params.typesShift
            )
        ).let {
            addView(R.id.containerNotificationTypes, it)
        }
    }

    private fun RemoteViews.addTagControls(
        params: NotificationTypeParams,
    ) {
        // TODO move prev next buttons directly to layout
        // Prev button
        getTypeControlView(
            icon = params.controlIconPrev,
            color = params.controlIconColor,
            intent = getPendingSelfIntent(
                context = context,
                action = ACTION_NOTIFICATION_TAGS_PREV,
                recordTypeId = params.id,
                selectedTypeId = params.selectedTypeId,
                recordTypesShift = params.typesShift,
                recordTagsShift = (params.tagsShift - TAGS_LIST_SIZE)
                    .coerceAtLeast(0),
            )
        ).let {
            addView(R.id.containerNotificationTags, it)
        }

        // Types buttons
        val currentTags = params.tags.drop(params.tagsShift).take(TAGS_LIST_SIZE)
        currentTags.forEach {
            getTagControlView(
                text = it.text,
                color = it.color,
                intent = getPendingSelfIntent(
                    context = context,
                    action = ACTION_NOTIFICATION_TAG_CLICK,
                    requestCode = it.id.toInt(),
                    selectedTypeId = params.selectedTypeId,
                    recordTypeId = params.id,
                    recordTagId = it.id,
                    recordTypesShift = params.typesShift,
                )
            ).let {
                addView(R.id.containerNotificationTags, it)
            }
        }

        // Populate container with empty items to preserve prev next controls position
        repeat(TAGS_LIST_SIZE - currentTags.size) {
            getTagControlView(
                text = "",
                color = null,
                intent = null
            ).let {
                addView(R.id.containerNotificationTags, it)
            }
        }

        // Next button
        getTypeControlView(
            icon = params.controlIconNext,
            color = params.controlIconColor,
            intent = getPendingSelfIntent(
                context = context,
                action = ACTION_NOTIFICATION_TAGS_NEXT,
                recordTypeId = params.id,
                selectedTypeId = params.selectedTypeId,
                recordTypesShift = params.typesShift,
                recordTagsShift = (params.tagsShift + TAGS_LIST_SIZE)
                    .takeUnless { it >= params.tags.size }
                    ?: params.tagsShift
            )
        ).let {
            addView(R.id.containerNotificationTags, it)
        }
    }

    private fun getTypeControlView(
        icon: RecordTypeIcon?,
        color: Int?,
        intent: PendingIntent?,
    ): RemoteViews {
        return RemoteViews(context.packageName, R.layout.notification_type_layout)
            .apply {
                if (icon != null && color != null) {
                    setViewVisibility(R.id.containerNotificationType, View.VISIBLE)
                    setImageViewBitmap(R.id.ivNotificationType, getIconBitmap(icon, color))
                } else {
                    setViewVisibility(R.id.containerNotificationType, View.INVISIBLE)
                }
                if (intent != null) {
                    setOnClickPendingIntent(R.id.btnNotificationType, intent)
                }
            }
    }

    private fun getTagControlView(
        text: String,
        color: Int?,
        intent: PendingIntent?,
    ): RemoteViews {
        return RemoteViews(context.packageName, R.layout.notification_tag_layout)
            .apply {
                setTextViewText(R.id.tvNotificationTag, text)
                if (color != null) {
                    setViewVisibility(R.id.containerNotificationTag, View.VISIBLE)
                    setInt(R.id.ivNotificationTag, "setColorFilter", color)
                } else {
                    setViewVisibility(R.id.containerNotificationTag, View.INVISIBLE)
                }
                if (intent != null) {
                    setOnClickPendingIntent(R.id.btnNotificationTag, intent)
                }
            }
    }

    private fun getPendingSelfIntent(
        context: Context,
        action: String,
        recordTypeId: Long,
        requestCode: Int? = null,
        selectedTypeId: Long? = null,
        recordTagId: Long? = null,
        recordTypesShift: Int? = null,
        recordTagsShift: Int? = null,
    ): PendingIntent {
        val intent = Intent(context, NotificationReceiver::class.java)
        intent.action = action
        intent.putExtra(ARGS_TYPE_ID, recordTypeId)
        selectedTypeId?.let { intent.putExtra(ARGS_SELECTED_TYPE_ID, it) }
        recordTagId?.let { intent.putExtra(ARGS_TAG_ID, it) }
        recordTypesShift?.let { intent.putExtra(ARGS_TYPES_SHIFT, it) }
        recordTagsShift?.let { intent.putExtra(ARGS_TAGS_SHIFT, it) }
        return PendingIntent.getBroadcast(
            context,
            requestCode ?: recordTypeId.toInt(),
            intent,
            PendingIntents.getFlags()
        )
    }

    private fun getIconBitmap(
        icon: RecordTypeIcon,
        color: Int
    ): Bitmap {
        return iconView.apply {
            itemIcon = icon
            itemColor = color
            measureExactly(iconSize)
        }.getBitmapFromView()
    }

    companion object {
        const val ACTION_NOTIFICATION_STOP =
            "com.example.util.simpletimetracker.feature_notification.recordType.onStop"
        const val ACTION_NOTIFICATION_TYPE_CLICK =
            "com.example.util.simpletimetracker.feature_notification.recordType.onTypeClick"
        const val ACTION_NOTIFICATION_TAG_CLICK =
            "com.example.util.simpletimetracker.feature_notification.recordType.onTagClick"

        const val ACTION_NOTIFICATION_TYPES_PREV =
            "com.example.util.simpletimetracker.feature_notification.recordType.onTypesPrevClick"
        const val ACTION_NOTIFICATION_TYPES_NEXT =
            "com.example.util.simpletimetracker.feature_notification.recordType.onTypesNextClick"
        const val ACTION_NOTIFICATION_TAGS_PREV =
            "com.example.util.simpletimetracker.feature_notification.recordType.onTagsPrevClick"
        const val ACTION_NOTIFICATION_TAGS_NEXT =
            "com.example.util.simpletimetracker.feature_notification.recordType.onTagsNextClick"

        const val ARGS_TYPE_ID = "typeId"
        const val ARGS_SELECTED_TYPE_ID = "selectedTypeId"
        const val ARGS_TAG_ID = "tagId"
        const val ARGS_TYPES_SHIFT = "typesShift"
        const val ARGS_TAGS_SHIFT = "tagsShift"

        private const val NOTIFICATIONS_CHANNEL_ID = "NOTIFICATIONS"
        private const val NOTIFICATIONS_CHANNEL_NAME = "Notifications"

        private const val TYPES_LIST_SIZE = 7
        private const val TAGS_LIST_SIZE = 4
    }
}