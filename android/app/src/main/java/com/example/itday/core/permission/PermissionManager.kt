package com.example.itday.core.permission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

interface PermissionManager {
    fun isGranted(permission: AppPermission): Boolean

    fun permissionsFor(permission: AppPermission): List<String>
}

class AndroidPermissionManager(
    context: Context,
) : PermissionManager {
    private val appContext = context.applicationContext

    override fun isGranted(permission: AppPermission): Boolean {
        val permissions = permissionsFor(permission)
        if (permissions.isEmpty()) return true

        return when (permission) {
            AppPermission.Location -> permissions.any(::isGranted)
            AppPermission.Notification -> permissions.all(::isGranted)
        }
    }

    override fun permissionsFor(permission: AppPermission): List<String> =
        when (permission) {
            AppPermission.Location ->
                listOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                )
            AppPermission.Notification ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    listOf(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    emptyList()
                }
        }

    private fun isGranted(permission: String): Boolean =
        ContextCompat.checkSelfPermission(appContext, permission) == PackageManager.PERMISSION_GRANTED
}
