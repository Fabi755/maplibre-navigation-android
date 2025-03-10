package org.maplibre.navigation.android.navigation.ui.v5.utils.time

import org.maplibre.navigation.core.navigation.MapLibreNavigationOptions
import java.util.Calendar
import java.util.Locale


open class NoneSpecifiedTimeFormat(
    private val isDeviceTwentyFourHourFormat: Boolean
) : TimeFormatResolver {

    override fun nextChain(chain: TimeFormatResolver?) {}

    override fun obtainTimeFormatted(
        type: MapLibreNavigationOptions.TimeFormat,
        time: Calendar
    ): String {
        return if (isDeviceTwentyFourHourFormat) {
            String.format(
                Locale.getDefault(),
                TwentyFourHoursTimeFormat.TWENTY_FOUR_HOURS_FORMAT,
                time,
                time
            )
        } else {
            String.format(
                Locale.getDefault(),
                TwelveHoursTimeFormat.TWELVE_HOURS_FORMAT,
                time,
                time,
                time
            )
        }
    }
}
