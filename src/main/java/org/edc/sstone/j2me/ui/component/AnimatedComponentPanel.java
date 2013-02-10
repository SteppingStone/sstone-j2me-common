/*
 * Copyright (c) 2012 EDC
 * 
 * This file is part of Stepping Stone.
 * 
 * Stepping Stone is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Stepping Stone is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Stepping Stone.  If not, see <http://www.gnu.org/licenses/gpl.txt>.
 */
package org.edc.sstone.j2me.ui.component;

import org.edc.sstone.Constants;
import org.edc.sstone.j2me.core.Registry;
import org.edc.sstone.j2me.device.BacklightControl;
import org.edc.sstone.j2me.ui.menu.MenuButton;
import org.edc.sstone.j2me.ui.scroll.ScrollDirection;
import org.edc.sstone.j2me.ui.scroll.VerticalScreenPosition;
import org.edc.sstone.j2me.ui.style.Style;
import org.edc.sstone.log.Log;
import org.edc.sstone.util.TimerQueue;
import org.edc.sstone.util.TimerTask;

/**
 * @author Greg Orlowski
 */
public class AnimatedComponentPanel extends ComponentContentPanel {

    protected int selectedComponent = 0;
    TimerQueue executionQueue;
    boolean shouldWait = false;
    boolean animationRunning = false;
    boolean continueAnimation = true;
    float animationSpeedMultiplier = 1.0f;

    public AnimatedComponentPanel() {
        super();
        Object animationSpeedMultObj = Registry.getManager().getUserPreference(Constants.ANIMATION_SPEED_RECORD_ID);
        if (animationSpeedMultObj != null) {
            // We store ANIMATION_SPEED_RECORD_ID as an integer value of multiplier*10. So, e.g.,
            // a value of 5 would mean that the animation delay should be multiplied by 0.5f (make
            // it twice as fast)
            float v = (float) ((Integer) animationSpeedMultObj).intValue();
            animationSpeedMultiplier = v / 10.0f;
        }
    }

    public void prepareLayout(int viewportWidth, int viewportHeight) {
        super.prepareLayout(viewportWidth, viewportHeight);

        /*
         * We need to be sure that the layout is prepared BEFORE we start animation because we rely
         * on the scroll state of the layout to determine whether we need to add
         */
        if (!animationRunning) {
            animate();
        }
    }

    protected void cancelAnimation() {
        continueAnimation = false;
        executionQueue.cleanup();
    }

    public void restartAnimation() {
        enableReplay(false);
        executionQueue.cleanup();
        scrollManager.resetToTop();
        animationRunning = false;
        getScreen().repaint(); // once animationRunning is false, repainting will restart animation
    }

    public void animate() {
        animationRunning = true;
        executionQueue = new TimerQueue();

        boolean firstTimerTask = true;
        final int componentCount = getComponents().size();

        for (int i = 0; i < componentCount; i++) {
            final Component c = getComponent(i);

            if (c instanceof AnimatedComponent) {
                // If the component is not shown, advance past the PREVIOUS component
                // before running this component's animations. This is necessary
                // for consecutive off-screen components
                if (c.getScreenPosition() == VerticalScreenPosition.NOT_SHOWN) {
                    advancePastComponent(c, i - 1, firstTimerTask);
                    firstTimerTask = false;
                }
                runComponentAnimations((AnimatedComponent) c, firstTimerTask);
                firstTimerTask = false;
            }
        }
        // if the last component on the screen is not animated, we still want to scroll to
        // it so it is visible (no need to set firstTimerTask=true b/c there can be none
        // after this anyway)
        final Component c = getComponent(componentCount - 1);
        if (c.getScreenPosition() == VerticalScreenPosition.NOT_SHOWN) {
            advancePastComponent(c, componentCount - 1, firstTimerTask);
        }

        addReplayTask();
    }

    private void addReplayTask() {
        executionQueue.scheduleOnce(new TimerTask() {
            public void run() {
                enableReplay(true);
                getScreen().repaint();
            }
        }, 0l);
    }

    protected void enableReplay(boolean enable) {
        MenuButton centerButton = getScreen().getMenuButton(MenuButton.CENTER);
        if (centerButton != null) {
            centerButton.setEnabled(enable);
        }
    }

    protected void runComponentAnimations(final AnimatedComponent c, boolean isFirstAnimatedComponent) {
        final Style componentStyle = c.getStyle();
        executionQueue.schedule(createComponentAnimationTask(c),
                (long) (componentStyle.getAnimationStartDelay() * animationSpeedMultiplier),
                (long) (componentStyle.getAnimationPeriod() * animationSpeedMultiplier));
    }

    /*
     * NOTE: do NOT animate in showNotify. Do it in prepareLayout (see comment there)
     */
    // public void showNotify() {
    // super.showNotify();
    // animate();
    // }

    public void hideNotify() {
        super.hideNotify();
        cancelAnimation();
    }

    protected TimerTask createComponentAnimationTask(final AnimatedComponent c) {
        final TimerTask task = new TimerTask() {
            public void run() {
                if (continueAnimation && c.hasMoreFrames()) {
                    c.advanceFrame(scrollManager);
                    backlightKeepAlive();
                } else {
                    c.reset();
                    cancel();
                }
            }
        };
        return task;
    }

    protected static void backlightKeepAlive() {
        BacklightControl blc = Registry.getManager().getBacklightControl();
        if (blc != null) {
            blc.stayLit();
        }
    }

    protected void advancePastComponent(final Component c, final int componentIdx, boolean forFirstAnimatedComponent) {
        final Style componentStyle = c.getStyle();
        Log.debug("Calling advancePastComponent: " + c.getClass().getName() + " -- " + componentIdx);
        executionQueue.schedule(new TimerTask() {
            public void run() {
                if (continueAnimation && scrollManager.getLastVisibleComponentIdx() <= componentIdx
                        && scrollManager.canScroll(ScrollDirection.DOWN)) {
                    scrollManager.scroll(ScrollDirection.DOWN);
                    backlightKeepAlive();
                    getScreen().repaint();
                } else {
                    cancel();
                }
            }
        }, (long) (componentStyle.getAnimationStartDelay() * animationSpeedMultiplier),
                (long) (componentStyle.getAnimationPeriod() * animationSpeedMultiplier));
    }
}
