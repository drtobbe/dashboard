package org.sagebionetworks.dashboard.dao.redis;

import static org.sagebionetworks.dashboard.dao.redis.Key.SEPARATOR;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.sagebionetworks.dashboard.model.Aggregation;
import org.sagebionetworks.dashboard.model.Statistic;
import org.sagebionetworks.dashboard.util.PosixTimeUtil;

/**
 * Assembles Redis keys at runtime in the format of
 * {statistic}:{aggregation}:{name-space}:{metric}:{time-stamp}.
 */
class KeyAssembler {

    KeyAssembler(Statistic statistic, Aggregation aggregation, NameSpace nameSpace) {

        if (statistic == null) {
            throw new IllegalArgumentException("Statistic cannot be null.");
        }
        if (aggregation == null) {
            throw new IllegalArgumentException("Aggregation cannot be null.");
        }
        if (nameSpace == null) {
            throw new IllegalArgumentException("Name space cannot be null.");
        }

        this.aggregation = aggregation;
        prefix = statistic + SEPARATOR + aggregation + SEPARATOR + nameSpace;
    }

    String getKey(String metricId, long flooredPosixTime) {
        return prefix + SEPARATOR + metricId + SEPARATOR + flooredPosixTime;
    }

    String getKey(final String metricId, final DateTime timestamp) {

        long ts = -1L;
        switch(aggregation) {
            case m3:
                ts = PosixTimeUtil.floorToMinute3(timestamp);
                break;
            case hour:
                ts = PosixTimeUtil.floorToHour(timestamp);
                break;
            case day:
                ts = PosixTimeUtil.floorToDay(timestamp);
                break;
            default:
                throw new RuntimeException("Aggregation " + aggregation + " not supported.");
        }
        return getKey(metricId, ts);
    }

    List<Long> getTimestamps(final String metricId, final DateTime from, final DateTime to) {

        long start = -1L;
        long end = -1L;
        long step = -1L;
        switch (aggregation) {
            case m3:
                 start = PosixTimeUtil.floorToMinute3(from);
                 end = PosixTimeUtil.floorToMinute3(to);
                 step = PosixTimeUtil.MINUTE_3;
                 break;
            case hour:
                start = PosixTimeUtil.floorToHour(from);
                end = PosixTimeUtil.floorToHour(to);
                step = PosixTimeUtil.HOUR;
                break;
            case day:
                start = PosixTimeUtil.floorToDay(from);
                end = PosixTimeUtil.floorToDay(to);
                step = PosixTimeUtil.DAY;
                break;
            default:
                throw new RuntimeException("Aggregation " + aggregation + " not supported.");
        }

        List<Long> timestamps = new ArrayList<Long>();
        for (long i = start; i <= end; i += step) {
            timestamps.add(i);
        }
        return timestamps;
    }

    private final Aggregation aggregation;
    private final String prefix; // Cached prefix string
}
