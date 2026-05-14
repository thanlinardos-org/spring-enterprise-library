package com.thanlinardos.spring_enterprise_library.model;

import com.thanlinardos.spring_enterprise_library.annotations.CoreTest;
import com.thanlinardos.spring_enterprise_library.time.model.TimeInterval;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@CoreTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TimeIntervalTest {

    private static final TimeInterval YEAR_2000 = TimeInterval.forIsoDatesMilli("2000-01-01", "2000-12-31");
    private static final TimeInterval INTERVAL_200001 = TimeInterval.forIsoDatesMilli("2000-01-01", "2000-01-31");
    private static final TimeInterval OPEN_END = TimeInterval.forIsoDateMilliToNull("2000-05-01");
    private static final TimeInterval JAN_MAY_2000 = TimeInterval.forIsoDatesMilli("2000-01-01", "2000-05-31");
    private static final TimeInterval JUN_JUL_2000 = TimeInterval.forIsoDatesMilli("2000-06-01", "2000-07-31");
    private static final TimeInterval FEB_OCT_2000 = TimeInterval.forIsoDatesMilli("2000-02-01", "2000-10-31");
    private static final TimeInterval APR_2024 = TimeInterval.forIsoDatesMilli("2024-03-01", "2024-03-31");
    private static final TimeInterval JUN_2024 = TimeInterval.forIsoDatesMilli("2024-06-01", "2024-06-30");
    private static final TimeInterval NOV_2024 = TimeInterval.forIsoDatesMilli("2024-11-01", "2024-11-30");
    private static final TimeInterval JAN_NOV_2024 = TimeInterval.forIsoDatesMilli("2024-01-01", "2024-11-30");
    private static final TimeInterval JAN_2024 = TimeInterval.forIsoDatesMilli("2024-01-01", "2024-01-31");
    private static final TimeInterval APR_MAY_2024 = TimeInterval.forIsoDatesMilli("2024-04-01", "2024-05-31");
    private static final TimeInterval JUL_OCT_2024 = TimeInterval.forIsoDatesMilli("2024-07-01", "2024-10-31");
    private static final TimeInterval FEB_2024 = TimeInterval.forIsoDatesMilli("2024-02-01", "2024-02-29");
    private static final TimeInterval NOV_2000 = TimeInterval.forIsoDatesMilli("2000-11-01", "2000-11-30");
    private static final TimeInterval JAN_NOV_2000 = TimeInterval.forIsoDatesMilli("2000-01-01", "2000-11-30");
    private static final TimeInterval JAN_OCT_2000 = TimeInterval.forIsoDatesMilli("2000-01-01", "2000-10-31");
    private static final TimeInterval YEAR_2001 = TimeInterval.forIsoDatesMilli("2001-01-01", "2001-12-31");
    private static final TimeInterval YEARS_2000_2001 = TimeInterval.forIsoDatesMilli("2000-01-01", "2001-12-31");
    private static final TimeInterval YEAR_3333 = TimeInterval.forIsoDatesMilli("3333-01-01", "3333-12-31");
    private static final TimeInterval MAY_2000 = TimeInterval.forIsoDatesMilli("2000-05-01", "2000-05-31");
    private static final TimeInterval APR_MAY_2000 = TimeInterval.forIsoDatesMilli("2000-04-01", "2000-05-31");
    private static final TimeInterval JUN_2000_TO_EOW = TimeInterval.forIsoDateToNull("2000-06-01");

    public static Stream<Arguments> splitFactory() {
        return Stream.of(
                Arguments.of("Empty list", Collections.emptyList(), Collections.emptyList()),
                Arguments.of("One interval",
                        List.of(YEAR_2000),
                        List.of(YEAR_2000)),
                Arguments.of("Interval 1 partially overlaps interval 2",
                        List.of(JAN_MAY_2000, TimeInterval.forIsoDates("2000-03-31", "2000-07-31")),
                        List.of(TimeInterval.forIsoDates("2000-01-01", "2000-03-30"),
                                TimeInterval.forIsoDates("2000-03-31", "2000-05-31"),
                                JUN_JUL_2000)),
                Arguments.of("Interval 1 neighbors interval 2",
                        List.of(JAN_MAY_2000, JUN_JUL_2000),
                        List.of(JAN_MAY_2000, JUN_JUL_2000)),
                Arguments.of("Interval 1 has gap to interval 2",
                        List.of(TimeInterval.forIsoDates("2000-01-01", "2000-03-31"), JUN_JUL_2000),
                        List.of(TimeInterval.forIsoDates("2000-01-01", "2000-03-31"), JUN_JUL_2000)),
                Arguments.of("Interval 1 contains interval 2",
                        List.of(JAN_MAY_2000, TimeInterval.forIsoDates("2000-03-15", "2000-03-31")),
                        List.of(TimeInterval.forIsoDates("2000-01-01", "2000-03-14"),
                                TimeInterval.forIsoDates("2000-03-15", "2000-03-31"),
                                APR_MAY_2000)),
                Arguments.of("Split in middle of day: Interval 1 contains interval 2",
                        List.of(JAN_MAY_2000, TimeInterval.forIsoDateTimes("2000-03-15T12:31:12.255", "2000-03-31T23:59:59.999")),
                        List.of(TimeInterval.forIsoDateTimes("2000-01-01T00:00:00.000", "2000-03-15T12:31:12.254"),
                                TimeInterval.forIsoDateTimes("2000-03-15T12:31:12.255", "2000-03-31T23:59:59.999"),
                                APR_MAY_2000)),
                Arguments.of("Interval 1 ends at one-day interval 2",
                        List.of(JAN_MAY_2000, TimeInterval.forIsoDates("2000-05-31", "2000-05-31")),
                        List.of(TimeInterval.forIsoDates("2000-01-01", "2000-05-30"), TimeInterval.forIsoDates("2000-05-31", "2000-05-31"))),
                Arguments.of("Ensemble scenario",
                        List.of(TimeInterval.forIsoDates("2000-01-01", "2000-01-05"),
                                TimeInterval.forIsoDates("2000-01-03", "2000-01-07"),
                                TimeInterval.forIsoDates("2000-01-06", "2000-01-08"),
                                TimeInterval.forIsoDates("2000-01-08", "2000-01-08"),
                                TimeInterval.forIsoDates("2000-01-08", "2000-01-10"),
                                TimeInterval.forIsoDates("2000-01-10", "2000-01-12"),
                                TimeInterval.forIsoDates("2000-01-15", "2000-01-19"),
                                TimeInterval.forIsoDates("2000-01-16", "2000-01-18")),
                        List.of(TimeInterval.forIsoDates("2000-01-01", "2000-01-02"),
                                TimeInterval.forIsoDates("2000-01-03", "2000-01-05"),
                                TimeInterval.forIsoDates("2000-01-06", "2000-01-07"),
                                TimeInterval.forIsoDates("2000-01-08", "2000-01-08"),
                                TimeInterval.forIsoDates("2000-01-09", "2000-01-09"),
                                TimeInterval.forIsoDates("2000-01-10", "2000-01-10"),
                                TimeInterval.forIsoDates("2000-01-11", "2000-01-12"),
                                TimeInterval.forIsoDates("2000-01-15", "2000-01-15"),
                                TimeInterval.forIsoDates("2000-01-16", "2000-01-18"),
                                TimeInterval.forIsoDates("2000-01-19", "2000-01-19"))),
                Arguments.of("Start inside and end inside",
                        List.of(JAN_OCT_2000, JAN_NOV_2000, FEB_OCT_2000),
                        List.of(INTERVAL_200001, FEB_OCT_2000, NOV_2000)),
                Arguments.of("Year interval + overlapping month intervals",
                        List.of(FEB_2024, APR_2024, JUN_2024, NOV_2024, JAN_NOV_2024),
                        List.of(JAN_2024, FEB_2024, APR_2024, APR_MAY_2024, JUN_2024, JUL_OCT_2024, NOV_2024)),
                Arguments.of("Interval 1 (null start) partially overlaps interval 2 (null end)",
                        List.of(TimeInterval.forIsoDates(null, "2000-05-31"), TimeInterval.forIsoDateToNull("2000-03-31")),
                        List.of(TimeInterval.forIsoDates(null, "2000-03-30"),
                                TimeInterval.forIsoDates("2000-03-31", "2000-05-31"),
                                TimeInterval.forIsoDates("2000-06-01", null))),
                Arguments.of("Interval 1 (null start) neighbors interval 2 (null end)",
                        List.of(TimeInterval.forIsoDates(null, "2000-05-31"), JUN_2000_TO_EOW),
                        List.of(TimeInterval.forIsoDates(null, "2000-05-31"), JUN_2000_TO_EOW)),
                Arguments.of("Interval 1 (null start) has gap to interval 2 (null end)",
                        List.of(TimeInterval.forIsoDates(null, "2000-03-31"), JUN_2000_TO_EOW),
                        List.of(TimeInterval.forIsoDates(null, "2000-03-31"), JUN_2000_TO_EOW)),
                Arguments.of("Interval 1 (null start) contains interval 2",
                        List.of(TimeInterval.forIsoDates(null, "2000-05-31"), TimeInterval.forIsoDates("2000-03-15", "2000-03-31")),
                        List.of(TimeInterval.forIsoDates(null, "2000-03-14"),
                                TimeInterval.forIsoDates("2000-03-15", "2000-03-31"),
                                APR_MAY_2000)),
                Arguments.of("Interval 1 (null end) contains interval 2",
                        List.of(TimeInterval.forIsoDates("2000-01-01", null), TimeInterval.forIsoDates("2000-03-15", "2000-03-31")),
                        List.of(TimeInterval.forIsoDates("2000-01-01", "2000-03-14"),
                                TimeInterval.forIsoDates("2000-03-15", "2000-03-31"),
                                TimeInterval.forIsoDates("2000-04-01", null))),
                Arguments.of("Interval 1 (null to null) contains interval 2",
                        List.of(TimeInterval.forIsoDates(null, null), TimeInterval.forIsoDates("2000-03-15", "2000-03-31")),
                        List.of(TimeInterval.forIsoDates(null, "2000-03-14"),
                                TimeInterval.forIsoDates("2000-03-15", "2000-03-31"),
                                TimeInterval.forIsoDates("2000-04-01", null)))
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("splitFactory")
    void split(String description, Collection<TimeInterval> input, Collection<TimeInterval> expected) {
        Collection<TimeInterval> actual = TimeInterval.split(input);
        Assertions.assertEquals(expected, actual);
    }

    private static Stream<Arguments> normalizeParams() {
        return Stream.of(
                Arguments.argumentSet("Empty list", Collections.emptyList(), true, Collections.emptyList()),
                Arguments.argumentSet("One interval",
                        List.of(OPEN_END), true,
                        List.of(OPEN_END)),
                Arguments.argumentSet("Same interval",
                        List.of(YEAR_2000, YEAR_2000), true,
                        List.of(YEAR_2000)),
                Arguments.argumentSet("Two intervals, no overlap",
                        List.of(YEAR_2000, YEAR_3333), true,
                        List.of(YEAR_2000, YEAR_3333)),
                Arguments.argumentSet("Two intervals, overlap",
                        List.of(YEAR_2000, TimeInterval.forIsoDates("2000-05-01", "2001-05-31")), true,
                        List.of(TimeInterval.forIsoDates("2000-01-01", "2001-05-31"))),
                Arguments.argumentSet("Two intervals, fully contain",
                        List.of(YEAR_2000, MAY_2000), true,
                        List.of(YEAR_2000)),
                Arguments.argumentSet("Two intervals, reverse overlap",
                        List.of(TimeInterval.forIsoDates("2000-05-01", "2001-05-31"), YEAR_2000), true,
                        List.of(TimeInterval.forIsoDates("2000-01-01", "2001-05-31"))),
                Arguments.argumentSet("Two intervals, neighboring",
                        List.of(YEAR_2000, YEAR_2001), true,
                        List.of(YEARS_2000_2001)),
                Arguments.argumentSet("Two intervals, neighboring (don't merge adjacent intervals)",
                        List.of(YEAR_2000, YEAR_2001), false,
                        List.of(YEAR_2000, YEAR_2001)),
                Arguments.argumentSet("Two intervals, overlap (don't merge adjacent intervals)",
                        List.of(YEAR_2000, TimeInterval.forIsoDates("2000-05-01", "2001-05-31")), false,
                        List.of(TimeInterval.forIsoDates("2000-01-01", "2001-05-31")))
        );
    }

    @ParameterizedTest
    @MethodSource("normalizeParams")
    void normalize(List<TimeInterval> intervals, boolean mergeAdjacentIntervals, List<TimeInterval> expected) {
        Assertions.assertEquals(expected, TimeInterval.normalize(intervals, mergeAdjacentIntervals));
    }
}