package org.maplibre.navigation.android.navigation.v5.routeprogress

import android.location.Location

fun interface ProgressChangeListener {
    fun onProgressChange(location: Location, routeProgress: RouteProgress)
}
