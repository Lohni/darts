package com.lohni.darts

import com.lohni.darts.ui.screens.TimeFrameOption
import com.lohni.darts.ui.screens.getDatesSince
import com.lohni.darts.ui.screens.getXAxisLabels
import org.junit.Assert
import org.junit.Test
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class StatisticChartTest {

    @Test
    fun `getXAxisLabels with empty dates`() {
        Assert.assertEquals(emptyList<String>(), getXAxisLabels(emptyList(), TimeFrameOption.WEEK))
    }

    @Test
    fun testXLabesForWeek() {
        val dates = getDatesSince(ChronoUnit.WEEKS, LocalDate.of(2025, 2, 28))
        Assert.assertEquals(
            listOf("21", "22", "23", "24", "25", "26", "27", "28"),
            getXAxisLabels(dates, TimeFrameOption.WEEK)
        )
    }

    @Test
    fun testXLabesForMonth() {
        val dates = getDatesSince(ChronoUnit.MONTHS, LocalDate.of(2025, 2, 28))
        Assert.assertEquals(
            listOf("Jan 28", "Feb 05", "Feb 13", "Feb 21", "Feb 28"),
            getXAxisLabels(dates, TimeFrameOption.MONTH)
        )
    }

    @Test
    fun testXLabesForYear() {
        val dates = getDatesSince(ChronoUnit.YEARS, LocalDate.of(2025, 2, 28))
        Assert.assertEquals(
            listOf("02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "01", "02"),
            getXAxisLabels(dates, TimeFrameOption.YEAR)
        )
    }

    @Test
    fun testXLabesForAll() {
        val dates = getDatesSince(ChronoUnit.FOREVER, LocalDate.of(2025, 3, 28), LocalDate.of(2025, 1, 1))
        Assert.assertEquals(
            listOf("Jan 01", "Jan 22", "Feb 12", "Mar 05", "Mar 28"),
            getXAxisLabels(dates, TimeFrameOption.ALL)
        )
    }
}