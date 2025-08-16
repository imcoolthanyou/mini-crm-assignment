package gautam.projects.crm_assignment.core.sync

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import android.util.Log

object SyncStatusNotifier {
    // syncInProgress MUST only be true during real sync, and reset to false regardless of errors.
    private val _syncInProgress = MutableStateFlow(false)
    val syncInProgress = _syncInProgress.asStateFlow()

    // Track last error message
    private val _syncError = MutableStateFlow<String?>(null)
    val syncError = _syncError.asStateFlow()

    fun notifySyncStarted() {
        Log.d("SyncStatusNotifier", "notifySyncStarted: spinner ON")
        _syncInProgress.value = true
        _syncError.value = null // Clear error when starting
    }

    fun notifySyncFinished() {
        Log.d("SyncStatusNotifier", "notifySyncFinished: spinner OFF")
        _syncInProgress.value = false
    }

    fun notifySyncError(error: String) {
        Log.d("SyncStatusNotifier", "notifySyncError: $error, spinner OFF")
        _syncError.value = error
        _syncInProgress.value = false // Stop spinner even on error
    }

    fun clearSyncError() {
        Log.d("SyncStatusNotifier", "clearSyncError: clearing error message")
        _syncError.value = null
    }
}