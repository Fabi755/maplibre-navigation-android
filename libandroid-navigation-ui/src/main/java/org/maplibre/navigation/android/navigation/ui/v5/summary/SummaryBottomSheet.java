package org.maplibre.navigation.android.navigation.ui.v5.summary;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.OnLifecycleEvent;
import android.content.Context;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import org.maplibre.navigation.android.navigation.ui.v5.NavigationViewModel;
import org.maplibre.navigation.android.navigation.ui.v5.R;
import org.maplibre.navigation.android.navigation.ui.v5.ThemeSwitcher;
import org.maplibre.navigation.core.models.UnitType;
import org.maplibre.navigation.core.navigation.MapLibreNavigationOptions;
import org.maplibre.navigation.core.navigation.NavigationConstants;
import org.maplibre.navigation.core.routeprogress.ProgressChangeListener;
import org.maplibre.navigation.core.routeprogress.RouteProgress;
import org.maplibre.navigation.android.navigation.ui.v5.utils.DistanceFormatter;
import org.maplibre.navigation.android.navigation.ui.v5.utils.LocaleUtils;

import java.text.DecimalFormat;

/**
 * A view with {@link BottomSheetBehavior}
 * that displays route summary information during navigation.
 * <p>
 * Can be expanded / collapsed to show / hide the list of
 * directions.
 *
 * @since 0.6.0
 */
public class SummaryBottomSheet extends FrameLayout implements LifecycleObserver {

  private static final String EMPTY_STRING = "";
  private TextView distanceRemainingText;
  private TextView timeRemainingText;
  private TextView arrivalTimeText;
  private ProgressBar rerouteProgressBar;
  private boolean isRerouting;
  private MapLibreNavigationOptions.TimeFormat timeFormatType;
  private DistanceFormatter distanceFormatter;
  private NavigationViewModel navigationViewModel;
  private LifecycleOwner lifecycleOwner;

  public SummaryBottomSheet(Context context) {
    this(context, null);
  }

  public SummaryBottomSheet(Context context, AttributeSet attrs) {
    this(context, attrs, -1);
  }

  public SummaryBottomSheet(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initialize();
  }

  /**
   * After the layout inflates, binds all necessary views,
   * create a {@link RecyclerView} for the list of directions,
   * and a new {@link DecimalFormat} for formatting distance remaining.
   */
  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();
    bind();
  }

  /**
   * Subscribes to a {@link NavigationViewModel} for
   * updates from {@link androidx.lifecycle.LiveData}.
   * <p>
   * Updates all views with fresh data / shows &amp; hides re-route state.
   *
   * @param navigationViewModel to which this View is subscribing
   * @since 0.6.2
   */
  public void subscribe(LifecycleOwner owner, NavigationViewModel navigationViewModel) {
    lifecycleOwner = owner;
    lifecycleOwner.getLifecycle().addObserver(this);
    this.navigationViewModel = navigationViewModel;

    navigationViewModel.summaryModel.observe(lifecycleOwner, new Observer<SummaryModel>() {
      @Override
      public void onChanged(@Nullable SummaryModel summaryModel) {
        if (summaryModel != null && !isRerouting) {
          arrivalTimeText.setText(summaryModel.getArrivalTime());
          timeRemainingText.setText(summaryModel.getTimeRemaining());
          distanceRemainingText.setText(summaryModel.getDistanceRemaining());
        }
      }
    });
    navigationViewModel.isOffRoute.observe(lifecycleOwner, new Observer<Boolean>() {
      @Override
      public void onChanged(@Nullable Boolean isOffRoute) {
        if (isOffRoute != null) {
          isRerouting = isOffRoute;
          if (isRerouting) {
            showRerouteState();
          } else {
            hideRerouteState();
          }
        }
      }
    });
  }

  /**
   * Unsubscribes {@link NavigationViewModel} {@link androidx.lifecycle.LiveData} objects
   * previously added in {@link SummaryBottomSheet#subscribe(LifecycleOwner, NavigationViewModel)}
   * by removing the observers of the {@link LifecycleOwner} when parent view is destroyed
   */
  @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
  public void unsubscribe() {
    if (navigationViewModel != null) {
      navigationViewModel.summaryModel.removeObservers(lifecycleOwner);
      navigationViewModel.isOffRoute.removeObservers(lifecycleOwner);
    }
  }

  /**
   * Called in {@link ProgressChangeListener}, creates a new model and then
   * uses it to update the views.
   *
   * @param routeProgress used to provide navigation / routeProgress data
   * @since 0.6.2
   */
  @SuppressWarnings("UnusedDeclaration")
  public void update(RouteProgress routeProgress) {
    if (routeProgress != null && !isRerouting) {
      SummaryModel model = new SummaryModel(getContext(), distanceFormatter, routeProgress, timeFormatType);
      arrivalTimeText.setText(model.getArrivalTime());
      timeRemainingText.setText(model.getTimeRemaining());
      distanceRemainingText.setText(model.getDistanceRemaining());
    }
  }

  /**
   * Shows the reroute progress bar and clears current text views.
   * Also sets boolean to rerouting state so views do not
   * continue to update while we are fetching a new route.
   *
   * @since 0.6.0
   */
  public void showRerouteState() {
    rerouteProgressBar.setVisibility(VISIBLE);
    clearViews();
  }

  /**
   * Hides the reroute progress bar and sets
   * rerouting state to false to text will begin updating again.
   *
   * @since 0.6.0
   */
  public void hideRerouteState() {
    rerouteProgressBar.setVisibility(INVISIBLE);
  }

  /**
   * Sets the time format type to use
   *
   * @param type to use
   */
  public void setTimeFormat(MapLibreNavigationOptions.TimeFormat type) {
    this.timeFormatType = type;
  }

  /**
   * Sets the distance formatter
   *
   * @param distanceFormatter to set
   */
  public void setDistanceFormatter(DistanceFormatter distanceFormatter) {
    if (distanceFormatter != null && !distanceFormatter.equals(this.distanceFormatter)) {
      this.distanceFormatter = distanceFormatter;
    }
  }

  /**
   * Inflates this layout needed for this view and initializes the locale as the device locale.
   */
  private void initialize() {
    initializeDistanceFormatter();
    inflate(getContext(), R.layout.summary_bottomsheet_layout, this);
  }

  private void initializeDistanceFormatter() {
    LocaleUtils localeUtils = new LocaleUtils();
    String language = localeUtils.inferDeviceLanguage(getContext());
    UnitType unitType = localeUtils.getUnitTypeForDeviceLocale(getContext());
    distanceFormatter = new DistanceFormatter(getContext(), language, unitType, MapLibreNavigationOptions.RoundingIncrement.ROUNDING_INCREMENT_FIFTY);
  }

  /**
   * Finds and binds all necessary views
   */
  private void bind() {
    distanceRemainingText = findViewById(R.id.distanceRemainingText);
    timeRemainingText = findViewById(R.id.timeRemainingText);
    arrivalTimeText = findViewById(R.id.arrivalTimeText);
    rerouteProgressBar = findViewById(R.id.rerouteProgressBar);
    updateRouteOverviewImage();
  }

  private void updateRouteOverviewImage() {
    ImageButton routeOverviewBtn = findViewById(R.id.routeOverviewBtn);
    routeOverviewBtn.setImageDrawable(ThemeSwitcher.retrieveThemeOverviewDrawable(getContext()));
  }

  /**
   * Clears all {@link View}s.
   */
  private void clearViews() {
    arrivalTimeText.setText(EMPTY_STRING);
    timeRemainingText.setText(EMPTY_STRING);
    distanceRemainingText.setText(EMPTY_STRING);
  }
}
