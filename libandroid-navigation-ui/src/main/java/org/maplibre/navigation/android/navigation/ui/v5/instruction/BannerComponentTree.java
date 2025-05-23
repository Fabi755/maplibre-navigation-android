package org.maplibre.navigation.android.navigation.ui.v5.instruction;

import androidx.annotation.NonNull;
import android.widget.TextView;

import org.maplibre.navigation.core.models.BannerComponents;
import org.maplibre.navigation.core.models.BannerText;

import java.util.ArrayList;
import java.util.List;

class BannerComponentTree {
  private final NodeCreator[] nodeCreators;
  private final List<BannerComponentNode> bannerComponentNodes;

  /**
   * Creates a master coordinator to make sure the coordinators passed in are used appropriately
   *
   * @param nodeCreators coordinators in the order that they should process banner components
   */
  BannerComponentTree(@NonNull BannerText bannerText, NodeCreator... nodeCreators) {
    this.nodeCreators = nodeCreators;
    bannerComponentNodes = parseBannerComponents(bannerText);
  }

  /**
   * Parses the banner components and processes them using the nodeCreators in the order they
   * were originally passed
   *
   * @param bannerText to parse
   * @return the list of nodes representing the bannerComponents
   */
  private List<BannerComponentNode> parseBannerComponents(BannerText bannerText) {
    int length = 0;
    List<BannerComponentNode> bannerComponentNodes = new ArrayList<>();

    for (BannerComponents components : bannerText.getComponents()) {
      BannerComponentNode node = null;
      for (NodeCreator nodeCreator : nodeCreators) {
        if (nodeCreator.isNodeType(components)) {
          String modifier = null;
          if (bannerText.getModifier() != null) {
            modifier = bannerText.getModifier().getText();
          }

          node = nodeCreator.setupNode(components, bannerComponentNodes.size(), length, modifier);
          break;
        }
      }

      if (node != null) {
        bannerComponentNodes.add(node);
        length += components.getText().length();
      }
    }

    return bannerComponentNodes;
  }

  /**
   * Loads the instruction into the given text view. If things have to be done in a particular order,
   * the coordinator methods preProcess and postProcess can be used. PreProcess should be used to
   * load text into the textView (so there should only be one coordinator calling this method), and
   * postProcess should be used to make changes to that text, i.e., to load images into the textView.
   *
   * @param textView in which to load text and images
   */
  void loadInstruction(TextView textView) {
    for (NodeCreator nodeCreator : nodeCreators) {
      nodeCreator.preProcess(textView, bannerComponentNodes);
    }

    for (NodeCreator nodeCreator : nodeCreators) {
      nodeCreator.postProcess(textView, bannerComponentNodes);
    }
  }
}
