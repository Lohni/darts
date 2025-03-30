package com.lohni.darts.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.lohni.darts.R
import com.lohni.darts.common.FormatUtil
import com.lohni.darts.room.entities.GameMode
import com.lohni.darts.room.entities.Player
import com.lohni.darts.room.enums.GameModeType
import com.lohni.darts.ui.composable.BasicLineChart
import com.lohni.darts.ui.composable.CustomDropdown
import com.lohni.darts.ui.composable.InfoDialog
import com.lohni.darts.ui.composable.OutlinedDropdown
import com.lohni.darts.viewmodel.StatisticsViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalUnit
import java.util.Locale
import kotlin.math.max

@Composable
fun StatisticsScreen(navController: NavController) {
    val statisticsViewModel: StatisticsViewModel = viewModel(factory = StatisticsViewModel.Factory)
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            val numSegments = 3
            var selectedSegment by remember { mutableIntStateOf(0) }
            val selectedGameMode by remember { statisticsViewModel.gameMode }

            Row {
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.weight(1.0F),
                ) {
                    SegmentedButton(
                        selected = selectedSegment == 0,
                        shape = SegmentedButtonDefaults.itemShape(0, numSegments),
                        onClick = { selectedSegment = 0 }
                    ) {
                        Text(stringResource(R.string.overall))
                    }
                    SegmentedButton(
                        selected = selectedSegment == 1,
                        shape = SegmentedButtonDefaults.itemShape(1, numSegments),
                        onClick = { selectedSegment = 1 }
                    ) {
                        Text(stringResource(R.string.competitive))
                    }
                    SegmentedButton(
                        selected = selectedSegment == 2,
                        shape = SegmentedButtonDefaults.itemShape(2, numSegments),
                        onClick = { selectedSegment = 2 }
                    ) {
                        Text(stringResource(R.string.visual))
                    }
                }
            }

            Row(
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp, start = 8.dp, end = 8.dp)
            ) {
                val players = remember { statisticsViewModel.availablePlayers }
                val selectedPlayer by remember { statisticsViewModel.player }
                OutlinedDropdown(
                    modifier = Modifier.weight(0.4f),
                    initialItem = selectedPlayer,
                    itemText = Player::pName,
                    inputLabel = stringResource(R.string.player),
                    selectOptions = players
                ) {
                    statisticsViewModel.changePlayer(it)
                }

                Spacer(Modifier.weight(0.05f))

                val gameModes = remember { statisticsViewModel.availableGameModes }
                OutlinedDropdown(
                    modifier = Modifier.weight(0.5f),
                    initialItem = selectedGameMode,
                    itemText = GameMode::gmName,
                    inputLabel = stringResource(R.string.game_mode),
                    selectOptions = gameModes
                ) {
                    statisticsViewModel.changeGameMode(it)
                }
            }

            Row {
                if (selectedSegment == 0) {
                    OverallStatisticsScreen(statisticsViewModel)
                } else if (selectedSegment == 1) {
                    CompetitiveStatisticsScreen(statisticsViewModel)
                } else if (selectedSegment == 2) {
                    VisualStatisticsScreen(statisticsViewModel)
                }
            }
        }
    }
}

@Composable
fun OverallStatisticsScreen(statisticsViewModel: StatisticsViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp)
    ) {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            val gameMode by remember { statisticsViewModel.gameMode }

            val format = FormatUtil.createDecimalFormat("0.00")
            val darts by remember { statisticsViewModel.darts }
            val dartsPerLeg by remember { statisticsViewModel.dartsPerLeg }
            val average by remember { statisticsViewModel.average }
            val triples by remember { statisticsViewModel.triples }
            val doubles by remember { statisticsViewModel.doubles }
            val misses by remember { statisticsViewModel.misses }
            val eightyPlus by remember { statisticsViewModel.eightyPlus }
            val hundredPlus by remember { statisticsViewModel.hundredPlus }
            val hundredFourtyPlus by remember { statisticsViewModel.hundredFourtyPlus }
            val hundredEightyPlus by remember { statisticsViewModel.hundredEightyPlus }
            val highestFinish by remember { statisticsViewModel.highestFinish }
            val scoringDarts by remember { statisticsViewModel.scoringDarts }
            val finishDarts by remember { statisticsViewModel.finishDarts }
            val scoringAverage by remember { statisticsViewModel.scoringAverage }
            val finishAverage by remember { statisticsViewModel.finishAverage }

            StatisticsEntry(stringResource(R.string.darts_total), "", darts.toString())
            StatisticsEntry(stringResource(R.string.average), "", format.format(average))
            StatisticsEntry(stringResource(R.string.darts_per_leg), "", format.format(dartsPerLeg))
            StatisticsEntry(stringResource(R.string.triple_count), "", triples.toString())
            StatisticsEntry(stringResource(R.string.double_count), "", doubles.toString())
            StatisticsEntry(stringResource(R.string.miss_bust), "", misses.toString())
            StatisticsEntry(
                stringResource(R.string.highest_finish),
                "",
                format.format(highestFinish)
            )
            HorizontalDivider(modifier = Modifier.padding(top = 8.dp, bottom = 4.dp))
            StatisticsEntry(stringResource(R.string.eightyplus), "", eightyPlus.toString())
            StatisticsEntry(stringResource(R.string.hundredplus), "", hundredPlus.toString())
            StatisticsEntry(
                stringResource(R.string.hundred_fourty_plus),
                "",
                hundredFourtyPlus.toString()
            )
            StatisticsEntry(
                stringResource(R.string.hundred_eighty),
                "",
                hundredEightyPlus.toString()
            )

            if (gameMode.gmType == GameModeType.CLASSIC) {
                HorizontalDivider(modifier = Modifier.padding(top = 8.dp, bottom = 4.dp))
                StatisticsEntry(
                    stringResource(R.string.darts_to_score),
                    stringResource(R.string.darts_to_score_info),
                    scoringDarts.toString()
                )
                StatisticsEntry(
                    stringResource(R.string.scoring_average),
                    stringResource(R.string.scoring_average_info),
                    format.format(scoringAverage)
                )
                StatisticsEntry(
                    stringResource(R.string.darts_to_finish),
                    stringResource(R.string.darts_to_finish_info),
                    finishDarts.toString()
                )
                StatisticsEntry(
                    stringResource(R.string.finish_average),
                    stringResource(R.string.finish_average_info),
                    format.format(finishAverage)
                )
            }
        }
    }
}

@Composable
fun StatisticsEntry(label: String, info: String, vararg value: String) {
    StatisticsEntry(label, info, value.asList())
}

@Composable
fun StatisticsEntry(label: String, info: String, value: List<String>) {
    Row(
        modifier = Modifier.padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(label, modifier = Modifier.weight(0.4f), fontSize = 17.sp)

        if (info.isNotEmpty()) {
            var showDialog by remember { mutableStateOf(false) }
            Icon(
                modifier = Modifier
                    .padding(4.dp)
                    .size(20.dp)
                    .clickable {
                        showDialog = true
                    },
                imageVector = Icons.Rounded.Info,
                contentDescription = null
            )
            InfoDialog(
                showDialog,
                label,
                stringResource(R.string.close),
                onDissmiss = { showDialog = false }
            ) {
                Text(info, fontSize = 16.sp)
            }
        }

        val weight = 0.6f / value.size
        for (v in value) {
            Text(v, modifier = Modifier.weight(weight), textAlign = TextAlign.End, fontSize = 17.sp)
        }
    }
}

@Composable
fun CompetitiveStatisticsScreen(statisticsViewModel: StatisticsViewModel) {
    Column {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp)
        ) {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .padding(top = 4.dp, end = 16.dp, bottom = 16.dp, start = 16.dp)
                    .verticalScroll(scrollState)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val availablePlayers = remember { statisticsViewModel.availablePlayers }
                    val comparePlayer by remember { statisticsViewModel.playerToCompare }
                    CustomDropdown(
                        modifier = Modifier,
                        initialItem = comparePlayer,
                        itemText = Player::pName,
                        selectOptions = availablePlayers
                    ) {
                        statisticsViewModel.changeComparingPlayer(it)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }

                val totalStats = remember { statisticsViewModel.competitiveStats }
                val average = remember { statisticsViewModel.comparisonAverage }
                val format = FormatUtil.createDecimalFormat("0.00")
                StatisticsEntry("", "", totalStats.map { it.player?.pName ?: "" })
                Text(stringResource(R.string.total_played_won), fontSize = 14.sp)
                StatisticsEntry(
                    stringResource(R.string.games),
                    "",
                    totalStats.map { "${it.gamesTotal}/${it.gamesWon}" })
                StatisticsEntry(
                    stringResource(R.string.sets),
                    "",
                    totalStats.map { "${it.setsTotal}/${it.setsWon}" })
                StatisticsEntry(
                    stringResource(R.string.legs),
                    "",
                    totalStats.map { "${it.legsTotal}/${it.legsWon}" })
                StatisticsEntry(
                    stringResource(R.string.average),
                    "",
                    average.map { format.format(it) })

                HorizontalDivider(modifier = Modifier.padding(top = 8.dp, bottom = 8.dp))

                val stats = remember { statisticsViewModel.directComparisonStats }
                Text(stringResource(R.string.faceoff_played_won), fontSize = 14.sp)
                StatisticsEntry(
                    stringResource(R.string.games),
                    "",
                    stats.map { "${it.gamesTotal}/${it.gamesWon}" })
                StatisticsEntry(
                    stringResource(R.string.sets),
                    "",
                    stats.map { "${it.setsTotal}/${it.setsWon}" })
                StatisticsEntry(
                    stringResource(R.string.legs),
                    "",
                    stats.map { "${it.legsTotal}/${it.legsWon}" })
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VisualStatisticsScreen(statisticsViewModel: StatisticsViewModel) {
    Column {
        var selectedTimeFrame by remember { mutableStateOf(TimeFrameOption.MONTH) }
        var firstPlayedDate by remember { statisticsViewModel.firstPlayedGame }

        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TimeFrameOption.entries.forEach { timeFrame ->
                FilterChip(
                    selected = selectedTimeFrame == timeFrame,
                    onClick = {
                        selectedTimeFrame = timeFrame
                    },
                    colors = FilterChipDefaults.elevatedFilterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    label = { Text(timeFrame.label) },
                    modifier = Modifier.padding(2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier.verticalScroll(scrollState)
        ) {
            val earliestDate =
                if (selectedTimeFrame == TimeFrameOption.ALL) firstPlayedDate else LocalDate.now()
            val dates: List<LocalDate> =
                getDatesSince(selectedTimeFrame.temporalUnit, LocalDate.now(), earliestDate)
            val xLabels = getXAxisLabels(dates, selectedTimeFrame)

            val lineChartData =
                if (statisticsViewModel.gameMode.value.gmType == GameModeType.STEP) {
                    LineChartData(
                        runningLineChartData = createLineChartData(
                            dates,
                            true,
                            Pair(
                                stringResource(R.string.running_total),
                                statisticsViewModel.dailyGroupedRunningAvg
                            )
                        ),
                        dailyLineChartData = createLineChartData(
                            dates,
                            false,
                            Pair(
                                stringResource(R.string.running_total),
                                statisticsViewModel.dailyGroupedAvg
                            )
                        ),
                        xAxisLabel = xLabels
                    )
                } else {
                    LineChartData(
                        runningLineChartData = createLineChartData(
                            dates,
                            true,
                            Pair(
                                stringResource(R.string.running_score),
                                statisticsViewModel.dailyGroupedRunningScoringAvg
                            ),
                            Pair(
                                stringResource(R.string.running_finish),
                                statisticsViewModel.dailyGroupedRunningFinishAvg
                            ),
                            Pair(
                                stringResource(R.string.running_total),
                                statisticsViewModel.dailyGroupedRunningAvg
                            )
                        ),
                        dailyLineChartData = createLineChartData(
                            dates,
                            false,
                            Pair(
                                stringResource(R.string.running_score),
                                statisticsViewModel.dailyGroupedScoringAvg
                            ),
                            Pair(
                                stringResource(R.string.running_finish),
                                statisticsViewModel.dailyGroupedFinishAvg
                            ),
                            Pair(
                                stringResource(R.string.running_total),
                                statisticsViewModel.dailyGroupedAvg
                            )
                        ),
                        xAxisLabel = xLabels
                    )
                }
            LineGraphs(lineChartData)
        }
    }
}

@Composable
fun LineGraphs(chartData: LineChartData) {
    BasicLineChart(
        stringResource(R.string.running_average),
        data = chartData.runningLineChartData,
        xAxisLabel = chartData.xAxisLabel
    )

    BasicLineChart(
        stringResource(R.string.daily_average),
        data = chartData.dailyLineChartData,
        xAxisLabel = chartData.xAxisLabel
    )
}

fun getDatesSince(
    temporalUnit: TemporalUnit,
    baseDate: LocalDate = LocalDate.now(),
    earliestDate: LocalDate = LocalDate.now(),
): List<LocalDate> {
    val startDate = if (temporalUnit == ChronoUnit.FOREVER) earliestDate
    else baseDate.minus(1L, temporalUnit)

    val dates = mutableListOf<LocalDate>()
    var currentDate = startDate

    while (!currentDate.isAfter(baseDate)) {
        dates.add(currentDate)
        currentDate = currentDate.plusDays(1)
    }

    return dates
}

private fun createLineChartData(
    dates: List<LocalDate>,
    substituteMissingDate: Boolean,
    vararg dataSeries: Pair<String, Map<LocalDate, Float>>,
): Map<String, List<Float>> {
    if (dates.isEmpty()) return emptyMap()

    val result = mutableMapOf<String, List<Float>>()

    dataSeries.forEach { (key, data) ->
        val lastValue =
            if (substituteMissingDate) data.keys.filter { it.isBefore(dates.first()) }
                .maxOrNull()
                ?.let { data[it] } ?: 0f
            else 0f

        val runningSeries = mutableListOf<Float>()
        var currentValue = lastValue

        dates.forEach { date ->
            currentValue =
                data.getOrDefault(date, if (substituteMissingDate) currentValue else 0f)
            runningSeries.add(currentValue)
        }
        result[key] = runningSeries.toList()
    }
    return result.toMap()
}

fun getXAxisLabels(
    dates: List<LocalDate>,
    timeFrameOption: TimeFrameOption
): List<String> {
    val numLabels = timeFrameOption.xLabelCount
    if (dates.isEmpty() || numLabels <= 0) {
        return emptyList()
    }

    val format =
        when (timeFrameOption) {
            TimeFrameOption.WEEK -> DateTimeFormatter.ofPattern("dd")
            TimeFrameOption.MONTH -> DateTimeFormatter.ofPattern("MMM dd")
                .withLocale(Locale.ENGLISH)

            TimeFrameOption.ALL -> DateTimeFormatter.ofPattern("MMM dd").withLocale(Locale.ENGLISH)
            TimeFrameOption.YEAR -> DateTimeFormatter.ofPattern("MM")
        }

    if (numLabels <= 2) {
        return listOf(dates[0].format(format), dates[dates.size - 1].format(format))
    }

    val interval = max(1, dates.size / (numLabels - 1))
    val labels = mutableListOf<String>()

    for (i in 0 until numLabels - 1) {
        val index = i * interval
        if (index < dates.size) {
            labels.add(dates[index].format(format))
        }
    }

    if (labels.size < numLabels) {
        labels.add(dates.last().format(format))
    }

    return labels
}

enum class TimeFrameOption(
    val id: Int,
    val label: String,
    val temporalUnit: TemporalUnit,
    val xLabelCount: Int
) {
    WEEK(0, "Week", ChronoUnit.WEEKS, 8),
    MONTH(1, "Month", ChronoUnit.MONTHS, 5),
    YEAR(2, "Year", ChronoUnit.YEARS, 13),
    ALL(3, "All", ChronoUnit.FOREVER, 5)
}

data class LineChartData(
    val runningLineChartData: Map<String, List<Float>>,
    val dailyLineChartData: Map<String, List<Float>>,
    val xAxisLabel: List<String>
)
