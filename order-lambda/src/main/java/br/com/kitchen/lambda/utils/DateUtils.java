package br.com.kitchen.lambda.utils;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

public class DateUtils {
    public static OffsetDateTime toOffset(LocalDateTime ldt) {
        return ldt != null
                ? ldt.atOffset(ZoneOffset.UTC)
                .truncatedTo(ChronoUnit.MILLIS)
                : null;
    }
}
