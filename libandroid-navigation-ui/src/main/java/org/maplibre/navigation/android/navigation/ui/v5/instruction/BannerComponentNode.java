package org.maplibre.navigation.android.navigation.ui.v5.instruction;

import org.maplibre.navigation.core.models.BannerComponents;

/**
 * Class used to construct a list of BannerComponents to be populated into a TextView
 */
class BannerComponentNode {
  BannerComponents bannerComponents;
  int startIndex;

  BannerComponentNode(BannerComponents bannerComponents, int startIndex) {
    this.bannerComponents = bannerComponents;
    this.startIndex = startIndex;
  }

  @Override
  public String toString() {
    return bannerComponents.getText();
  }

  void setStartIndex(int startIndex) {
    this.startIndex = startIndex;
  }
}
