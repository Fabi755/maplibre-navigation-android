package org.maplibre.navigation.core.utils

import org.maplibre.geojson.model.Point
import org.maplibre.geojson.utils.PolylineUtils
import org.maplibre.navigation.core.BaseTest
import org.maplibre.navigation.core.models.LegStep
import org.maplibre.navigation.core.models.StepManeuver
import kotlin.test.Test
import kotlin.test.assertEquals

class MeasurementUtilsTest : BaseTest() {

    @Test
    fun userTrueDistanceFromStep_returnsZeroWhenCurrentStepAndPointEqualSame() {
        val futurePoint = Point(-95.367697, 29.758938)

        val geometryPoints: MutableList<Point> = ArrayList()
        geometryPoints.add(futurePoint)
        val step = getLegStep(Point(0.0, 0.0), geometryPoints)

        val distance = MeasurementUtils.userTrueDistanceFromStep(futurePoint, step)
        assertEquals(0.0, distance, DELTA)
    }

    @Test
    fun userTrueDistanceFromStep_onlyOnePointInLineStringStillMeasuresDistanceCorrectly() {
        val futurePoint = Point(-95.3676974, 29.7589382)

        val geometryPoints: MutableList<Point> = ArrayList()
        geometryPoints.add(Point(-95.8427, 29.7757))
        val step = getLegStep(Point(0.0, 0.0), geometryPoints)

        val distance = MeasurementUtils.userTrueDistanceFromStep(futurePoint, step)
        assertEquals(45900.73617999494, distance, DELTA)
    }

    @Test
    fun userTrueDistanceFromStep_onePointStepGeometryWithDifferentRawPoint() {
        val futurePoint = Point(-95.3676974, 29.7589382)

        val geometryPoints: MutableList<Point> = ArrayList()
        geometryPoints.add(Point(-95.8427, 29.7757))
        geometryPoints.add(futurePoint)
        val step = getLegStep(Point(0.0, 0.0), geometryPoints)

        val distance = MeasurementUtils.userTrueDistanceFromStep(futurePoint, step)
        assertEquals(0.04457271773629306, distance, DELTA)
    }

    private fun getLegStep(location: Point, geometryPoints: List<Point>): LegStep {
        return LegStep(
            geometry = PolylineUtils.encode(geometryPoints, Constants.PRECISION_6),
            mode = "driving",
            distance = 0.0,
            duration = 0.0,
            maneuver = StepManeuver(
                location = location,
                bearingBefore = 0.0,
                bearingAfter = 0.0,
                instruction = null,
                type = null,
                modifier = null,
                exit = null
            ),
            weight = 0.0,
            durationTypical = null,
            speedLimitUnit = null,
            speedLimitSign = null,
            name = null,
            ref = null,
            destinations = null,
            pronunciation = null,
            rotaryName = null,
            rotaryPronunciation = null,
            voiceInstructions = null,
            bannerInstructions = null,
            drivingSide = null,
            intersections = null,
            exits = null,
        )
    }
}