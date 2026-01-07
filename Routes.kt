package com.techmaina.visionintel.ui.navigation

import android.net.Uri

object Routes {
    const val APP_START = "app_start"
    const val SPLASH_1 = "splash_1"
    const val SPLASH_2 = "splash_2"
    const val SPLASH_3 = "splash_3"
    const val PERMISSIONS = "permissions"
    const val STORAGE_PERMISSION = "storage_permission"
    const val LOGIN = "login"
    const val GOOGLE_AUTHENTICATING = "google_authenticating"
    const val GOOGLE_CONNECTED = "google_connected"
    const val REGISTER = "register"
    const val FORGOT_PASSWORD = "forgot_password"
    const val RESET_PASSWORD = "reset_password"
    const val NETWORK_STATUS = "network_status"
    const val NETWORK_STATUS_SETUP = "network_status_setup"
    const val UPLOAD_VIDEO = "upload_video"
    const val LIVE_CAMERA_FEED = "live_camera_feed"
    const val SELECT_VIDEO_SOURCE = "select_video_source"
    const val DRONE_VIDEO_UPLOAD = "drone_video_upload"
    const val PROCESSING_DRONE_VIDEO = "processing_drone_video?uri={uri}"
    const val PROCESSING_VIDEO_ANALYSIS = "processing_video?source={source}&uri={uri}"
    const val ANALYSIS_PLACEHOLDER = "analysis_placeholder?source={source}&uri={uri}"
    const val ALERTS_LIST = "alerts"
    const val ALERTS = "alerts?origin={origin}"
    const val ANALYSIS_RESULT = "analysis_result?origin={origin}"
    const val DAILY_SUMMARY = "daily_summary"
    const val THREAT_DETECTED = "threat_detected"
    const val THREAT_DETECTED_WITH_IDS = "threat_detected?alertId={alertId}&reportId={reportId}"
    const val ALERT_DETAIL = "alert_detail"
    const val ALERT_DETAIL_WITH_IDS = "alert_detail?alertId={alertId}&reportId={reportId}"
    const val DETAILED_REPORT = "detailed_report"
    const val DETAILED_REPORT_WITH_ID = "detailed_report?reportId={reportId}"
    const val EXPORT_REPORT = "export_report"
    const val EXPORT_REPORT_WITH_ID = "export_report?reportId={reportId}"
    const val EXPORT_REPORT_WITH_JOB_ID = "export_report?jobId={jobId}"
    const val REPORTS_DASHBOARD = "reports_dashboard"
    const val REPORTS_HISTORY = "reports_history"
    const val HISTORY_DASHBOARD = "history_dashboard"
    const val HISTORY_DETAIL = "history_detail/{historyId}"
    const val SNAPSHOT_GALLERY = "snapshot_gallery"
    const val SEARCH_FILTER = "search_filter"
    const val PROFILE_DASHBOARD = "profile_dashboard"
    const val ACKNOWLEDGE_ALERT = "acknowledge_alert"
    const val ACKNOWLEDGE_ALERT_WITH_ID = "acknowledge_alert?alertId={alertId}"
    const val SETTINGS = "settings"
    const val EDIT_PROFILE = "edit_profile"
    const val CHANGE_PASSWORD = "change_password"
    const val NOTIFICATION_SETTINGS = "notification_settings"
    const val THEME_SETTINGS = "theme_settings"
    const val LANGUAGE_SETTINGS = "language_settings"
    const val AI_MODEL_INFO = "ai_model_info"
    const val ABOUT_VISION_INTEL = "about_vision_intel"
    const val HELP_SUPPORT = "help_support"
    const val NETWORK_SETTINGS = "network_settings"
    const val HOME_DASHBOARD = "home"
    const val HOME = HOME_DASHBOARD
    const val ML_SMOKE_TEST = "ml_smoke_test"
    const val JOB_REPORT = "job_report?jobId={jobId}&origin={origin}"
    const val DASHBOARD = "dashboard"
    const val COMING_SOON = "coming_soon/{title}"

    fun comingSoon(title: String): String {
        return "coming_soon/${Uri.encode(title)}"
    }

    fun analysisPlaceholder(
        source: String,
        uri: String? = null
    ): String {
        val encodedSource = Uri.encode(source)
        val encodedUri = Uri.encode(uri ?: "")
        return "analysis_placeholder?source=$encodedSource&uri=$encodedUri"
    }

    fun alerts(originRoute: String? = null): String {
        return if (originRoute.isNullOrBlank()) {
            ALERTS_LIST
        } else {
            "$ALERTS_LIST?origin=${Uri.encode(originRoute)}"
        }
    }

    fun analysisResult(originRoute: String? = null): String {
        return if (originRoute.isNullOrBlank()) {
            "analysis_result"
        } else {
            "analysis_result?origin=${Uri.encode(originRoute)}"
        }
    }

    fun jobReport(jobId: String, originRoute: String? = null): String {
        val jid = Uri.encode(jobId)
        val origin = Uri.encode(originRoute ?: "")
        return "job_report?jobId=$jid&origin=$origin"
    }

    fun historyDetail(historyId: String): String {
        return "history_detail/${Uri.encode(historyId)}"
    }

    fun processingDroneVideo(uri: String): String {
        return "processing_drone_video?uri=${Uri.encode(uri)}"
    }

    fun processingVideo(source: String, uri: String): String {
        return "processing_video?source=${Uri.encode(source)}&uri=${Uri.encode(uri)}"
    }

    fun detailedReport(reportId: String?): String {
        val safe = Uri.encode(reportId ?: "")
        return "detailed_report?reportId=$safe"
    }

    fun exportReport(reportId: String?): String {
        val safe = Uri.encode(reportId ?: "")
        return "export_report?reportId=$safe"
    }

    fun exportReportByJob(jobId: String): String {
        return "export_report?jobId=${Uri.encode(jobId)}"
    }

    fun threatDetected(alertId: String?, reportId: String?): String {
        val a = Uri.encode(alertId ?: "")
        val r = Uri.encode(reportId ?: "")
        return "threat_detected?alertId=$a&reportId=$r"
    }

    fun alertDetail(alertId: String?, reportId: String?): String {
        val a = Uri.encode(alertId ?: "")
        val r = Uri.encode(reportId ?: "")
        return "alert_detail?alertId=$a&reportId=$r"
    }

    fun acknowledgeAlert(alertId: String?): String {
        val a = Uri.encode(alertId ?: "")
        return "acknowledge_alert?alertId=$a"
    }
}
