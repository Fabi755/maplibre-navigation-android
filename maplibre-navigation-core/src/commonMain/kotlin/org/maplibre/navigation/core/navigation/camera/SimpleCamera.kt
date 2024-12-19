package org.maplibre.navigation.core.navigation.camera

import org.maplibre.navigation.geo.LineString
import org.maplibre.navigation.geo.Point
import org.maplibre.navigation.core.models.DirectionsRoute
import org.maplibre.navigation.core.utils.Constants
import org.maplibre.navigation.geo.turf.TurfMeasurement
import org.maplibre.navigation.core.navigation.MapLibreNavigation

/**
 * The default camera used by [MapLibreNavigation].
 *
 * @since 0.10.0
 */
open class SimpleCamera : Camera {

    private var routeCoordinates: List<Point> = ArrayList()
    private var initialBearing = 0.0
    private var initialRoute: DirectionsRoute? = null

    override fun bearing(routeInformation: RouteInformation): Double {
        return routeInformation.route?.let { route ->
            setupLineStringAndBearing(route)
            initialBearing
        }
            ?: routeInformation.location?.bearing?.toDouble()
            ?: 0.0
    }

    override fun target(routeInformation: RouteInformation): Point? {
        return routeInformation.route?.let { route ->
            setupLineStringAndBearing(route)
            val firstPoint = routeCoordinates.first()
            Point(
                longitude = firstPoint.longitude,
                latitude = firstPoint.latitude
            )
        } ?: routeInformation.location?.let { location ->
            Point(
                longitude = location.longitude,
                latitude = location.latitude
            )
        }
    }

    override fun tilt(routeInformation: RouteInformation): Double {
        return DEFAULT_TILT.toDouble()
    }

    override fun zoom(routeInformation: RouteInformation): Double {
        return DEFAULT_ZOOM
    }

    override fun overview(routeInformation: RouteInformation): List<Point> {
        if (routeCoordinates.isEmpty()) {
            buildRouteCoordinatesFromRouteData(routeInformation)
        }

        return routeCoordinates
    }

    private fun buildRouteCoordinatesFromRouteData(routeInformation: RouteInformation) {
        routeInformation.route?.let { route ->
            setupLineStringAndBearing(route)
        } ?: routeInformation.routeProgress?.let { routeProgress ->
            setupLineStringAndBearing(routeProgress.directionsRoute)
        }
    }

    private fun setupLineStringAndBearing(route: DirectionsRoute) {
        if (initialRoute != null && route == initialRoute) {
            return // no need to recalculate these values
        }

        initialRoute = route
        routeCoordinates = generateRouteCoordinates(route)
        initialBearing = TurfMeasurement.bearing(
            Point(
                longitude = routeCoordinates.first().longitude,
                latitude = routeCoordinates.first().latitude
            ),
            Point(
                longitude = routeCoordinates[1].longitude,
                latitude = routeCoordinates[1].latitude
            )
        )
    }

    private fun generateRouteCoordinates(route: DirectionsRoute?): List<Point> {
        return route?.let { rte ->
            val lineString = LineString.fromPolyline(rte.geometry, Constants.PRECISION_6)
            lineString.points
        } ?: emptyList()
    }

    protected companion object {
        const val DEFAULT_TILT: Int = 50
        const val DEFAULT_ZOOM: Double = 15.0
    }
}