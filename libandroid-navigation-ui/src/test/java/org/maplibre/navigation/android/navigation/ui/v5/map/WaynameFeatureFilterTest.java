package org.maplibre.navigation.android.navigation.ui.v5.map;

import static com.google.common.base.Charsets.UTF_8;
import static org.mockito.Mockito.mock;

import androidx.annotation.NonNull;

import org.maplibre.geojson.Feature;
import org.maplibre.geojson.LineString;
import org.maplibre.geojson.Point;

import org.junit.Test;
import org.maplibre.navigation.core.location.Location;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class WaynameFeatureFilterTest {

  @Test
  public void findPointFromCurrentPoint() {
    Feature featureOne = Feature.fromJson(loadJsonFixture("feature_one.json"));
    Point currentPoint = Point.fromLngLat(1.234, 4.567);
    WaynameFeatureFilter waynameFeatureFilter = buildFilter();

    Point featureAheadOfUser = waynameFeatureFilter.findPointFromCurrentPoint(currentPoint, (LineString) featureOne.geometry());
  }

  private List<Feature> buildQueriedFeatures() {
    List<Feature> queriedFeatures = new ArrayList<>();
    Feature featureOne = Feature.fromJson(loadJsonFixture("feature_one.json"));
    Feature featureTwo = Feature.fromJson(loadJsonFixture("feature_two.json"));
    queriedFeatures.add(featureOne);
    queriedFeatures.add(featureTwo);
    return queriedFeatures;
  }

  private String loadJsonFixture(String filename) {
    ClassLoader classLoader = getClass().getClassLoader();
    InputStream inputStream = classLoader.getResourceAsStream(filename);
    Scanner scanner = new Scanner(inputStream, UTF_8.name()).useDelimiter("\\A");
    return scanner.hasNext() ? scanner.next() : "";
  }

  @NonNull
  private WaynameFeatureFilter buildFilter() {
    return new WaynameFeatureFilter(Collections.emptyList(), mock(Location.class), Collections.emptyList());
  }
}