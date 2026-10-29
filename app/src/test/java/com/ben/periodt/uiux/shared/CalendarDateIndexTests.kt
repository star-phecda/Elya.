package com.ben.periodt.uiux.shared

import com.ben.periodt.ui.shared.CycleRegularity
import com.ben.periodt.ui.shared.Prediction
import com.ben.periodt.ui.calendar.buildCalendarDayIndex
import com.ben.periodt.viewmodel.PeriodViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth

class CalendarDateIndexTests {
    private val today = LocalDate.of(2026, 10, 29)

    private fun cycle(start: LocalDate, end: LocalDate?) = PeriodViewModel.Cycle(
        id = 7,
        startDate = start,
        endDate = end,
        bleeding = "Medium",
        bloodColor = "Red",
        painLevel = 2
    )

    private fun pack(start: LocalDate, count: Int, end: LocalDate? = null) = PeriodViewModel.PillPack(
        id = 3,
        startDate = start,
        pillCount = count,
        endDate = end
    )

    private fun prediction(
        periodStart: LocalDate = today.plusDays(20),
        periodLength: Int = 5,
        fertileWindow: ClosedRange<LocalDate> = today.minusDays(2)..today.plusDays(2)
    ) = Prediction(
        minPeriodStart = periodStart.minusDays(2),
        maxPeriodStart = periodStart.plusDays(2),
        mostLikelyPeriodStart = periodStart,
        periodLength = periodLength,
        ovulationDay = periodStart.minusDays(14),
        ovulationConfidence = 0.8f,
        fertileWindow = fertileWindow,
        cycleLength = 28,
        cycleRegularity = CycleRegularity.REGULAR
    )

    @Test
    fun loggedCycleTakesPrecedenceOverOverlappingPillPack() {
        val activeCycle = cycle(today.minusDays(2), today.plusDays(2))
        val activePack = pack(today.minusDays(5), count = 21)

        val index = buildCalendarDayIndex(
            today = today,
            cycles = listOf(activeCycle),
            prediction = prediction(),
            transitioning = false,
            onPill = true,
            packs = listOf(activePack)
        )

        assertEquals(1, index.phases[today])
        assertEquals(activeCycle, index.cycles[today])
    }

    @Test
    fun pillPackTakesPrecedenceOverPredictedPeriodAndFertileWindow() {
        val index = buildCalendarDayIndex(
            today = today,
            cycles = emptyList(),
            prediction = prediction(periodStart = today),
            transitioning = false,
            onPill = false,
            packs = listOf(pack(today.minusDays(2), count = 21))
        )

        assertEquals(5, index.phases[today])
        assertEquals(true, index.packs.containsKey(today))
    }

    @Test
    fun predictedPeriodUsesAnExclusiveEndBoundary() {
        val start = today.minusDays(2)
        val index = buildCalendarDayIndex(
            today = today,
            cycles = emptyList(),
            prediction = prediction(
                periodStart = start,
                periodLength = 3,
                fertileWindow = LocalDate.of(2020, 1, 1)..LocalDate.of(2020, 1, 7)
            ),
            transitioning = false,
            onPill = false,
            packs = emptyList()
        )

        assertEquals(2, index.phases[today])
        assertEquals(0, index.phases[start.plusDays(3)])
    }

    @Test
    fun fertileWindowIsOnlyShownWhenPredictionIsActive() {
        val activePrediction = buildCalendarDayIndex(
            today = today,
            cycles = emptyList(),
            prediction = prediction(),
            transitioning = false,
            onPill = false,
            packs = emptyList()
        )
        val pausedPrediction = buildCalendarDayIndex(
            today = today,
            cycles = emptyList(),
            prediction = prediction(),
            transitioning = true,
            onPill = false,
            packs = emptyList()
        )

        assertEquals(3, activePrediction.phases[today])
        assertEquals(0, pausedPrediction.phases[today])
    }

    @Test
    fun indexIncludesFullCalendarGridSpilloverAtBothEdges() {
        val index = buildCalendarDayIndex(
            today = today,
            cycles = emptyList(),
            prediction = null,
            transitioning = false,
            onPill = false,
            packs = emptyList()
        )
        val currentMonth = YearMonth.from(today)
        val firstSupportedMonthDay = currentMonth.minusMonths(12).atDay(1)
        val lastSupportedMonthDay = currentMonth.plusMonths(12).atEndOfMonth()

        assertTrue(index.phases.containsKey(firstSupportedMonthDay.minusDays(8)))
        assertTrue(index.phases.containsKey(lastSupportedMonthDay.plusDays(8)))
        assertFalse(index.phases.containsKey(firstSupportedMonthDay.minusDays(9)))
        assertFalse(index.phases.containsKey(lastSupportedMonthDay.plusDays(9)))
    }

    @Test
    fun indexProvidesAdjacentDayStatesForContinuousRangeHighlighting() {
        val loggedCycle = cycle(today.minusDays(1), today.plusDays(1))
        val index = buildCalendarDayIndex(
            today = today,
            cycles = listOf(loggedCycle),
            prediction = null,
            transitioning = false,
            onPill = false,
            packs = emptyList()
        )

        assertEquals(1, index.phases[today.minusDays(1)])
        assertEquals(1, index.phases[today])
        assertEquals(1, index.phases[today.plusDays(1)])
        assertEquals(0, index.phases[today.plusDays(2)])
    }
}
