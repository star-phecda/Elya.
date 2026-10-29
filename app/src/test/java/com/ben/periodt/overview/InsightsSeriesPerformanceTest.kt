package com.ben.periodt.ui.overview

import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class InsightsSeriesPerformanceTest {
    @Test
    fun downsamplesLongChartSeriesToBoundedPointCountAndPreservesEndpoints() {
        val points = (0 until 1_000).map { it.toFloat() to (it % 4).toFloat() }
        val dates = (0 until 1_000).map { "Day $it" }

        val result = downsampleInsightsSeries(InsightsChartSeries(points, dates))

        assertEquals(120, result.points.size)
        assertEquals(120, result.dates.size)
        assertEquals(points.first().first, result.points.first().first, 0.001f)
        assertEquals(points.last().first, result.points.last().first, 0.001f)
        assertEquals(dates.first(), result.dates.first())
        assertEquals(dates.last(), result.dates.last())
    }

    @Test
    fun keepsShortChartSeriesUnchanged() {
        val series = InsightsChartSeries(
            points = listOf(0f to 1f, 1f to 2f, 2f to 3f),
            dates = listOf("Oct 1", "Oct 2", "Oct 3")
        )

        val result = downsampleInsightsSeries(series)

        assertSame(series, result)
    }
}
