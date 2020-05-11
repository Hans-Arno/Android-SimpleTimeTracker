package com.example.util.simpletimetracker.navigation

import android.app.Activity
import androidx.navigation.NavController
import com.example.util.simpletimetracker.R
import androidx.navigation.findNavController
import com.example.util.simpletimetracker.feature_change_record_type.view.ChangeRecordTypeFragment
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RouterImpl @Inject constructor(): Router() {

    private var navController: NavController? = null

    override fun navigate(screen: Screen, data: Any?) {
        when (screen) {
            Screen.CHANGE_RECORD_TYPE ->
                navController?.navigate(
                    R.id.changeRecordTypeFragment,
                    ChangeRecordTypeFragment.createBundle(data)
                )
        }
    }

    override fun back() {
        navController?.popBackStack()
    }

    override fun bind(activity: Activity) {
        navController = activity.findNavController(R.id.container)
    }
}