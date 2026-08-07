package com.example.itday.core.location

import android.annotation.SuppressLint
import android.content.Context
import android.os.SystemClock
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class FusedLocationRepository(
    context: Context,
) : LocationRepository {
    private val locationClient =
        LocationServices.getFusedLocationProviderClient(context.applicationContext)
    private val requestMutex = Mutex()
    private var cachedLocation: CachedLocation? = null

    override suspend fun getCurrentLocation(forceRefresh: Boolean): LocationCoordinate? =
        requestMutex.withLock {
            cachedLocation
                ?.takeUnless { forceRefresh || it.isExpired() }
                ?.coordinate
                ?: requestLocation()?.also { coordinate ->
                    cachedLocation = CachedLocation(coordinate, SystemClock.elapsedRealtime())
                }
        }

    @SuppressLint("MissingPermission")
    private suspend fun requestLocation(): LocationCoordinate? =
        suspendCancellableCoroutine { continuation ->
            try {
                locationClient
                    .getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
                    .addOnSuccessListener { location ->
                        if (continuation.isActive) {
                            continuation.resume(
                                location?.let { LocationCoordinate(it.latitude, it.longitude) },
                            )
                        }
                    }.addOnFailureListener {
                        if (continuation.isActive) continuation.resume(null)
                    }.addOnCanceledListener {
                        continuation.cancel()
                    }
            } catch (_: SecurityException) {
                continuation.resume(null)
            }
        }

    private data class CachedLocation(
        val coordinate: LocationCoordinate,
        val measuredAtMillis: Long,
    ) {
        fun isExpired(): Boolean =
            SystemClock.elapsedRealtime() - measuredAtMillis >= CACHE_VALIDITY_MILLIS
    }

    private companion object {
        const val CACHE_VALIDITY_MILLIS = 2 * 60 * 1_000L
    }
}
