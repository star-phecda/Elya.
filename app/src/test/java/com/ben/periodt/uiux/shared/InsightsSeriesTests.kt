package com.ben.periodt.uiux.shared

import com.ben.periodt.ui.overview.buildInsightsSeries
import com.ben.periodt.viewmodel.PeriodViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class InsightsSeriesTests {
    private fun cycle(
        id: Int = 1,
        start: LocalDate = LocalDate.of(2026, 1, 1),
        end: LocalDate? = null,
        bleeding: String = "Medium",
        bloodColor: String = "Red",
        pain: Int = 2
    ) = PeriodViewModel.Cycle(
        id = id,
        startDate = start,
        endDate = end,
        bleeding = bleeding,
        bloodColor = bloodColor,
        painLevel = pain
    )

    @Test
    fun emptyHistoryProducesEmptyChartSeries() {
        val series = buildInsightsSeries(emptyList(), emptyMap())

        assertTrue(series.bleeding.points.isEmpty())
        assertTrue(series.bleeding.dates.isEmpty())
        assertTrue(series.pain.points.isEmpty())
        assertTrue(series.pain.dates.isEmpty())
        assertTrue(series.bloodColorDistribution.isEmpty())
    }

    @Test
    fun dailyOverrideIsUsedForBleedingPainAndBloodColor() {
        val start = LocalDate.of(2026, 1, 1)
        val cycle = cycle(start = start, bleeding = "Light", bloodColor = "Bright Red", pain = 1)
        val log = PeriodViewModel.DailyLog(
            id = 99,
            cycleId = cycle.id,
            date = start,
            bleeding = "Heavy",
            bloodColor = "Dark Red",
            painLevel = 8
        )

        val series = buildInsightsSeries(listOf(cycle), mapOf("${cycle.id}|$start" to log))

        assertEquals(3f, series.bleeding.points.first().second, 0.001f)
        assertEquals(8f, series.pain.points.first().second, 0.001f)
        assertEquals("Jan 1", series.bleeding.dates.first())
        assertEquals(0.2f, series.bloodColorDistribution.first { it.first == "dark red" }.second, 0.001f)
        assertEquals(0.8f, series.bloodColorDistribution.first { it.first == "bright red" }.second, 0.001f)
    }

    @Test
    fun chartsUseChronologicalCycleOrderAndSameDayCount() {
        val firstStart = LocalDate.of(2026, 1, 1)
        val laterStart = LocalDate.of(2026, 2, 1)
        val laterCycle = cycle(id = 2, start = laterStart, end = laterStart.plusDays(1), bleeding = "Heavy", pain = 7)
        val earlierCycle = cycle(id = 1, start = firstStart, end = firstStart.plusDays(2), bleeding = "Spotting", pain = 0)

        val series = buildInsightsSeries(listOf(laterCycle, earlierCycle), emptyMap())

        assertEquals(5, series.bleeding.points.size)
        assertEquals(5, series.pain.points.size)
        assertEquals(0.5f, series.bleeding.points.first().second, 0.001f)
        assertEquals(7f, series.pain.points.last().second, 0.001f)
        assertEquals("Jan 1", series.bleeding.dates.first())
        assertEquals("Feb 2", series.pain.dates.last())
    }
}
