package com.umc.itday.core.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.os.SystemClock
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume

class FusedLocationRepository(
    private val context: Context,
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

    override suspend fun getAddress(
        latitude: Double,
        longitude: Double,
    ): String? =
        withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.KOREA)
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                addresses?.firstOrNull()?.let { address ->
                    // 상세 주소에서 필요한 부분만 추출 (예: 구 + 동)
                    val locality = address.locality ?: "" // 시
                    val subLocality = address.subLocality ?: "" // 구
                    val thoroughfare = address.thoroughfare ?: "" // 동
                    
                    if (subLocality.isNotEmpty() && thoroughfare.isNotEmpty()) {
                        "$subLocality $thoroughfare"
                    } else if (thoroughfare.isNotEmpty()) {
                        thoroughfare
                    } else {
                        address.getAddressLine(0)?.replace("대한민국 ", "") ?: "알 수 없는 위치"
                    }
                }
            } catch (e: Exception) {
                null
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
