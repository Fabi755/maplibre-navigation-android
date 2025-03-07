package org.maplibre.navigation.android.navigation.ui.v5;

import org.maplibre.navigation.core.models.DirectionsRoute;
import org.maplibre.geojson.Point;

public interface ViewRouteListener {

  void onRouteUpdate(DirectionsRoute directionsRoute);

  void onRouteRequestError(String errorMessage);

  void onDestinationSet(Point destination);
}
