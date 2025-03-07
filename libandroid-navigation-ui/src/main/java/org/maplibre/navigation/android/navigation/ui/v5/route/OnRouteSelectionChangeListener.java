package org.maplibre.navigation.android.navigation.ui.v5.route;

import org.maplibre.navigation.core.models.DirectionsRoute;

/**
 * Listener for determining which current route the user has selected as their primary route for
 * navigation.
 *
 * @since 0.8.0
 */
public interface OnRouteSelectionChangeListener {

  /**
   * Callback when the user selects a different route.
   *
   * @param directionsRoute the route which the user has currently selected
   * @since 0.8.0
   */
  void onNewPrimaryRouteSelected(DirectionsRoute directionsRoute);
}
