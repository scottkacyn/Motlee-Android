/*******************************************************************************
 * Copyright 2012 Steven Rudenko
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package shared.ui.actionscontentview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Scroller;

public class ActionsContentView extends ViewGroup {
  private static final String TAG = ActionsContentView.class.getSimpleName();
  private static final boolean DEBUG = false;

  /**
   * Spacing will be calculated as offset from right bound of view.
   */
  public static final int SPACING_RIGHT_OFFSET = 0;
  /**
   * Spacing will be calculated as right bound of actions bar container.
   */
  public static final int SPACING_ACTIONS_WIDTH = 1;

  private final ContentScrollController mContentScrollController;
  private final GestureDetector mGestureDetector;

  private int mSpacingType = SPACING_RIGHT_OFFSET;
  /**
   * Value of spacing to use.
   */
  private int mSpacing;

  /**
   * Value of actions container spacing to use.
   */
  private int mActionsSpacing;

  /**
   * Value of shadow width.
   */
  private int mShadowWidth = 0;

  /**
   * Indicates whether swiping is enabled or not.
   */
  private boolean isSwipingEnabled = true;

  /**
   * Indicates how long flinging will take time in milliseconds.
   */
  private int mFlingDuration = 250;

  private final FrameLayout viewActionsContainer;
  private final LinearLayout viewContentContainer;

  private final Rect mContentHitRect = new Rect();

  public ActionsContentView(Context context) {
    this(context, null);
  }

  public ActionsContentView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public ActionsContentView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);

    setClipChildren(false);
    setClipToPadding(false);

    // reading attributes
    final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ActionsContentView);
    mSpacingType = a.getInteger(R.styleable.ActionsContentView_spacing_type, SPACING_RIGHT_OFFSET);
    final int spacingDefault = context.getResources().getDimensionPixelSize(R.dimen.default_actionscontentview_spacing);
    mSpacing = a.getDimensionPixelSize(R.styleable.ActionsContentView_spacing, spacingDefault);
    final int actionsSpacingDefault = context.getResources().getDimensionPixelSize(R.dimen.default_actionscontentview_actions_spacing);
    mActionsSpacing = a.getDimensionPixelSize(R.styleable.ActionsContentView_actions_spacing, actionsSpacingDefault);

    final int actionsLayout = a.getResourceId(R.styleable.ActionsContentView_actions_layout, 0);
    final int contentLayout = a.getResourceId(R.styleable.ActionsContentView_content_layout, 0);

    mShadowWidth = a.getDimensionPixelSize(R.styleable.ActionsContentView_shadow_width, 0);
    final int shadowDrawableRes = a.getResourceId(R.styleable.ActionsContentView_shadow_drawable, 0);

    a.recycle();

    if (DEBUG) {
      Log.d(TAG, "spacing type: " + mSpacingType);
      Log.d(TAG, "spacing value: " + mSpacing);
      Log.d(TAG, "actions spacing value: " + mActionsSpacing);
      Log.d(TAG, "actions layout id: " + actionsLayout);
      Log.d(TAG, "content layout id: " + contentLayout);
      Log.d(TAG, "shadow drawable: " + shadowDrawableRes);
      Log.d(TAG, "shadow width: " + mShadowWidth);
    }

    mContentScrollController = new ContentScrollController(new Scroller(context));
    mGestureDetector = new GestureDetector(context, mContentScrollController);
    mGestureDetector.setIsLongpressEnabled(false);

    final LayoutInflater inflater = LayoutInflater.from(context);
    viewActionsContainer = new FrameLayout(context);
    if (actionsLayout != 0)
      inflater.inflate(actionsLayout, viewActionsContainer, true);

    addView(viewActionsContainer, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

    viewContentContainer = new LinearLayout(context) {
      @Override
      public boolean onTouchEvent(MotionEvent event) {
        // prevent ray cast of touch events to actions container
        getHitRect(mContentHitRect);
        mContentHitRect.offset(-viewContentContainer.getScrollX(), viewContentContainer.getScrollY());
        if (mContentHitRect.contains((int)event.getX(), (int)event.getY())) {
          return true;
        }

        return super.onTouchEvent(event);
      }
    };

    // we need to be sure we have horizontal layout to add shadow to left border
    viewContentContainer.setOrientation(LinearLayout.HORIZONTAL);

    if (mShadowWidth > 0 && shadowDrawableRes != 0) {
      final ImageView shadow = new ImageView(context);
      shadow.setBackgroundResource(shadowDrawableRes);
      final LinearLayout.LayoutParams shadowParams = new LinearLayout.LayoutParams(mShadowWidth, LinearLayout.LayoutParams.MATCH_PARENT);
      shadow.setLayoutParams(shadowParams);
      viewContentContainer.addView(shadow);
    }

    if (contentLayout != 0)
      inflater.inflate(contentLayout, viewContentContainer, true);

    addView(viewContentContainer, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
  }

  public ViewGroup getActionsContainer() {
    return viewActionsContainer;
  }

  public ViewGroup getContentContainer() {
    return viewContentContainer;
  }

  public boolean isActionsShown() {
    return !mContentScrollController.isContentShown();
  }

  public void showActions() {
    mContentScrollController.hideContent(mFlingDuration);
  }

  public boolean isContentShown() {
    return mContentScrollController.isContentShown();
  }

  public void showContent() {
    mContentScrollController.showContent(mFlingDuration);
  }

  public void toggleActions() {
    if (isActionsShown())
      showContent();
    else
      showActions();
  }

  public void setFlingDuration(int duration) {
    mFlingDuration = duration;
  }

  public int getFlingDuration() {
    return mFlingDuration;
  }

  public boolean isSwipingEnabled() {
    return isSwipingEnabled;
  }

  public void setSwipingEnabled(boolean enabled) {
    isSwipingEnabled = enabled;
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    // return true always as far we should handle touch event for swiping
    if (isSwipingEnabled)
      return true;

    return super.onTouchEvent(event);
  }

  @Override
  public boolean dispatchTouchEvent(MotionEvent ev) {
    if (!isSwipingEnabled)
      return super.dispatchTouchEvent(ev);

    final int action = ev.getAction();
    // if current touch event should be handled
    if (mContentScrollController.isHandled() && action == MotionEvent.ACTION_UP) {
      mContentScrollController.onUp(ev);
      return false;
    }

    if (mGestureDetector.onTouchEvent(ev) || mContentScrollController.isHandled()) {
      clearPressedState(this);
      return false;
    }

    return super.dispatchTouchEvent(ev);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    final int width = MeasureSpec.getSize(widthMeasureSpec);
    final int height = MeasureSpec.getSize(heightMeasureSpec);

    if (DEBUG)
      Log.d(TAG, "width: " + width + " height: " + height);

    final int childrenCount = getChildCount();
    for (int i=0; i<childrenCount; ++i) {
      final View v = getChildAt(i);
      if (v == viewActionsContainer) {
        // setting size of actions according to spacing parameters
        if (mSpacingType == SPACING_ACTIONS_WIDTH)
          viewActionsContainer.measure(MeasureSpec.makeMeasureSpec(mSpacing, MeasureSpec.EXACTLY), heightMeasureSpec);
        else // all other situations are handled as SPACING_RIGHT_OFFSET
          viewActionsContainer.measure(MeasureSpec.makeMeasureSpec(width - mSpacing, MeasureSpec.EXACTLY), heightMeasureSpec);
      } else if (v == viewContentContainer) {
        final int contentWidth = MeasureSpec.getSize(widthMeasureSpec) - mActionsSpacing + mShadowWidth;
        v.measure(MeasureSpec.makeMeasureSpec(contentWidth, MeasureSpec.EXACTLY), heightMeasureSpec);
      } else {
        v.measure(widthMeasureSpec, heightMeasureSpec);
      }
    }

    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    if (DEBUG) {
      final Rect layout = new Rect(l, t, r, b);
      Log.d(TAG, "layout: " + layout.toShortString());
    }

    // putting every child view to top-left corner
    final int childrenCount = getChildCount();
    for (int i=0; i<childrenCount; ++i) {
      final View v = getChildAt(i);
      if (v == viewContentContainer)
        v.layout(l + mActionsSpacing - mShadowWidth, t, l + mActionsSpacing + v.getMeasuredWidth(), t + v.getMeasuredHeight());
      else
        v.layout(l, t, l + v.getMeasuredWidth(), t + v.getMeasuredHeight());
    }
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);

    // set correct position of content view after view size was changed
    if (w != oldw || h != oldh) {
      mContentScrollController.init();
    }
  }

  /**
   * Clears pressed state for all views hierarchy starting from parent view.
   * @param parent - parent view
   * @return true is press state was cleared
   */
  private static boolean clearPressedState(ViewGroup parent) {
    if (parent.isPressed()) {
      parent.setPressed(false);
      return true;
    }

    final int count = parent.getChildCount();
    for (int i=0; i<count; ++i) {
      final View v = parent.getChildAt(i);
      if (v.isPressed()) {
        v.setPressed(false);
        return true;
      }

      if (!(v instanceof ViewGroup))
        continue;

      final ViewGroup vg = (ViewGroup) v;
      if (clearPressedState(vg))
        return true;
    }
    return false;
  }

  /**
   * Used to handle scrolling events and scroll content container
   * on top of actions one.
   * @author steven
   *
   */
  private class ContentScrollController implements GestureDetector.OnGestureListener, Runnable {
    /**
     * Used to auto-scroll to closest bound on touch up event.
     */
    private final Scroller mScroller;

    // using Boolean object to initialize while first scroll event
    private Boolean mHandleEvent = null;

    private int mLastFlingX = 0;

    /**
     * Indicates whether we need initialize position of view after measuring is finished.
     */
    private boolean isContentShown = true;

    public ContentScrollController(Scroller scroller) {
      mScroller = scroller;
    }

    /**
     * Initializes visibility of content after views measuring is finished.
     */
    public void init() {
      if (DEBUG)
        Log.d(TAG, "Scroller: init");

      if (isContentShown)
        showContent(0);
      else
        hideContent(0);
    }

    /**
     * Returns handling lock value. It indicates whether all events
     * should be marked as handled.
     * @return
     */
    public boolean isHandled() {
      return mHandleEvent != null && mHandleEvent;
    }

    @Override
    public boolean onDown(MotionEvent e) {
      mHandleEvent = null;
      reset();
      return false;
    }

    public boolean onUp(MotionEvent e) {
      if (!isHandled())
        return false;

      mHandleEvent = null;
      completeScrolling();
      return true;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
      return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
      // No-op
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
        float distanceY) {

      // if there is first scroll event after touch down
      if (mHandleEvent == null) {
        if (Math.abs(distanceX) < Math.abs(distanceY)) {
          // if first event is more scroll by Y axis than X one
          // ignore all events until event up
          mHandleEvent = Boolean.FALSE;
          return mHandleEvent;
        } else {
          // handle all events of scrolling by X axis
          mHandleEvent = Boolean.TRUE;
          scrollBy((int) distanceX);
        }
      } else if (mHandleEvent) {
        // it is not first event we should handle as scrolling by X axis
        scrollBy((int) distanceX);
      }

      return mHandleEvent;
    }

    @Override
    public void onLongPress(MotionEvent e) {
      // No-op
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
        float velocityY) {
      // does not work because onDown() method returns false always
      return false;
    }

    /**
     * Scrolling content view according by given value.
     * @param dx
     */
    private void scrollBy(int dx) {
      final int x = viewContentContainer.getScrollX();

      if (DEBUG)
        Log.d(TAG, "scroll from " + x + " by " + dx);

      final int scrollBy;
      if (dx < 0) { // scrolling right
        final int rightBound = getRightBound();
        if (x + dx < -rightBound)
          scrollBy = -rightBound - x;
        else
          scrollBy = dx;
      } else { // scrolling left
        // don't scroll if we are at left bound
        if (x == 0)
          return;

        if (x + dx > 0)
          scrollBy = -x;
        else
          scrollBy = dx;
      }

      viewContentContainer.scrollBy(scrollBy, 0);
    }

    public boolean isContentShown() {
      final int x;
      if (!mScroller.isFinished())
        x = mScroller.getFinalX();
      else
        x = viewContentContainer.getScrollX();

      return x == 0;
    }

    public void hideContent(int duration) {
      if (DEBUG)
        Log.d(TAG, "Scroller: hide content by " + duration + "ms");

      isContentShown = false;
      if (viewContentContainer.getMeasuredWidth() == 0 || viewContentContainer.getMeasuredHeight() == 0) {
        return;
      }

      final int startX = viewContentContainer.getScrollX();
      final int dx = getRightBound() + startX;
      fling(startX, dx, duration);
    }

    public void showContent(int duration) {
      if (DEBUG)
        Log.d(TAG, "Scroller: show content by " + duration + "ms");

      isContentShown = true;
      if (viewContentContainer.getMeasuredWidth() == 0 || viewContentContainer.getMeasuredHeight() == 0) {
        return;
      }

      final int startX = viewContentContainer.getScrollX();
      final int dx = startX;
      fling(startX, dx, duration);
    }

    /**
     * Starts auto-scrolling to bound which is closer to current position.
     */
    private void completeScrolling() {
      final int startX = viewContentContainer.getScrollX();

      final int rightBound = getRightBound();
      final int middle = -rightBound / 2;
      if (startX > middle) {
        showContent(mFlingDuration);
      } else {
        hideContent(mFlingDuration);
      }
    }

    private void fling(int startX, int dx, int duration) {
      reset();

      if (dx == 0)
        return;

      if (duration <= 0) {
        viewContentContainer.scrollBy(-dx, 0);
        return;
      }

      mScroller.startScroll(startX, 0, dx, 0, duration);

      if (DEBUG)
        Log.d(TAG, "starting fling at " + startX + " for " + dx + " by " + duration);

      mLastFlingX = startX;
      viewContentContainer.post(this);
    }

    /**
     * Processes auto-scrolling to bound which is closer to current position.
     */
    @Override
    public void run() {
      if (mScroller.isFinished()) {
        if (DEBUG)
          Log.d(TAG, "scroller is finished, done with fling");
        return;
      }

      final boolean more = mScroller.computeScrollOffset();
      final int x = mScroller.getCurrX();
      final int diff = mLastFlingX - x;
      if (diff != 0) {
        viewContentContainer.scrollBy(diff, 0);
        mLastFlingX = x;
      }

      if (more) {
        viewContentContainer.post(this);
      }
    }

    /**
     * Resets scroller controller. Stops flinging on current position.
     */
    public void reset() {
      if (DEBUG)
        Log.d(TAG, "Scroller: reset");

      if (!mScroller.isFinished()) {
        mScroller.forceFinished(true);
      }
    }

    /**
     * Returns right bound (limit) for scroller.
     * @return right bound (limit) for scroller.
     */
    private int getRightBound() {
      if (mSpacingType == SPACING_ACTIONS_WIDTH) {
        return mSpacing - mActionsSpacing;
      } else { // all other situations are handled as SPACING_RIGHT_OFFSET
        return getWidth() - mSpacing - mActionsSpacing;
      }
    }
  };

  public static class SavedState extends BaseSavedState {
    /**
     * Indicates whether content was shown while saving state.
     */
    private boolean isContentShown;

    public SavedState(Parcelable superState) {
      super(superState);
    }

    public void writeToParcel(Parcel out, int flags) {
      super.writeToParcel(out, flags);
      out.writeBooleanArray(new boolean[]{isContentShown});
    }

    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
      public SavedState[] newArray(int size) {
        return new SavedState[size];
      }

      @Override
      public SavedState createFromParcel(Parcel source) {
        return new SavedState(source);
      }
    };

    SavedState(Parcel in) {
      super(in);

      boolean[] showing = new boolean[1];
      in.readBooleanArray(showing);
      isContentShown = showing[0];
    }
  }


  public Parcelable onSaveInstanceState() {
    final Parcelable superState = super.onSaveInstanceState();
    final SavedState ss = new SavedState(superState);
    ss.isContentShown = isContentShown();
    return ss;
  }


  public void onRestoreInstanceState(Parcelable state) {
    if (!(state instanceof SavedState)) {
      super.onRestoreInstanceState(state);
      return;
    }

    final SavedState ss = (SavedState)state;
    super.onRestoreInstanceState(ss.getSuperState());

    if (ss.isContentShown) {
      showContent();
    } else {
      showActions();
    }
  }


}
