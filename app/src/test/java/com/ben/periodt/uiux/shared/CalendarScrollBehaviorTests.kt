package com.ben.periodt.uiux.shared

import com.ben.periodt.ui.calendar.nextCalendarCollapsedState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CalendarScrollBehaviorTests {
    private val threshold = 16f

    @Test
    fun ignoresSmallScrollNoiseInsteadOfCollapsingCalendar() {
        assertNull(nextCalendarCollapsedState(accumulatedDeltaY = -5f, isCollapsed = false, historyAtTop = true, thresholdPx = threshold))
        assertNull(nextCalendarCollapsedState(accumulatedDeltaY = 5f, isCollapsed = true, historyAtTop = true, thresholdPx = threshold))
    }

    @Test
    fun collapsesExpandedCalendarAfterAnIntentionalUpwardSwipe() {
        assertEquals(true, nextCalendarCollapsedState(accumulatedDeltaY = -24f, isCollapsed = false, historyAtTop = true, thresholdPx = threshold))
    }

    @Test
    fun expandsCollapsedCalendarOnDownwardSwipeOnlyWhenHistoryIsAtTop() {
        assertEquals(false, nextCalendarCollapsedState(accumulatedDeltaY = 24f, isCollapsed = true, historyAtTop = true, thresholdPx = threshold))
        assertNull(nextCalendarCollapsedState(accumulatedDeltaY = 24f, isCollapsed = true, historyAtTop = false, thresholdPx = threshold))
    }

    @Test
    fun doesNotRequestTheCurrentCalendarStateAgain() {
        assertNull(nextCalendarCollapsedState(accumulatedDeltaY = -24f, isCollapsed = true, historyAtTop = true, thresholdPx = threshold))
        assertNull(nextCalendarCollapsedState(accumulatedDeltaY = 24f, isCollapsed = false, historyAtTop = true, thresholdPx = threshold))
    }
}
