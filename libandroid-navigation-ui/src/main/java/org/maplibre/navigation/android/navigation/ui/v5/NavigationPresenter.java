package org.maplibre.navigation.android.navigation.ui.v5;

import org.maplibre.navigation.core.location.Location;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.maplibre.navigation.core.models.DirectionsRoute;
import org.maplibre.geojson.Point;

class NavigationPresenter {

  private NavigationContract.View view;
  private boolean resumeState;

  NavigationPresenter(NavigationContract.View view) {
    this.view = view;
  }

  void updateResumeState(boolean resumeState) {
    this.resumeState = resumeState;
  }

  void onRecenterClick() {
    view.setSummaryBehaviorHideable(false);
    view.setSummaryBehaviorState(BottomSheetBehavior.STATE_EXPANDED);
    view.updateWayNameVisibility(true);
    view.resetCameraPosition();
    view.hideRecenterBtn();
  }

  void onCameraTrackingDismissed() {
    if (!view.isSummaryBottomSheetHidden()) {
      view.setSummaryBehaviorHideable(true);
      view.setSummaryBehaviorState(BottomSheetBehavior.STATE_HIDDEN);
      view.updateWayNameVisibility(false);
    }
  }

  void onSummaryBottomSheetHidden() {
    if (view.isSummaryBottomSheetHidden()) {
      view.showRecenterBtn();
    }
  }

  void onRouteUpdate(DirectionsRoute directionsRoute) {
    view.drawRoute(directionsRoute);
    if (resumeState && view.isRecenterButtonVisible()) {
      view.updateCameraRouteOverview();
    } else {
      view.startCamera(directionsRoute);
    }
  }

  void onDestinationUpdate(Point point) {
    view.addMarker(point);
  }

  void onNavigationLocationUpdate(Location location) {
    if (resumeState && !view.isRecenterButtonVisible()) {
      view.resumeCamera(location);
      resumeState = false;
    }
    view.updateNavigationMap(location);
  }

  void onWayNameChanged(@NonNull String wayName) {
    if (TextUtils.isEmpty(wayName) || view.isSummaryBottomSheetHidden()) {
      view.updateWayNameVisibility(false);
      return;
    }
    view.updateWayNameView(wayName);
    view.updateWayNameVisibility(true);
  }

  void onNavigationStopped() {
    view.updateWayNameVisibility(false);
  }

  void onRouteOverviewClick() {
    view.updateWayNameVisibility(false);
    view.updateCameraRouteOverview();
    view.showRecenterBtn();
  }
}