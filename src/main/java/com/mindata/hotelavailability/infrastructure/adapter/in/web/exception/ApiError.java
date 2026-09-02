package com.mindata.hotelavailability.infrastructure.adapter.in.web.exception;

import java.time.Instant;
import java.util.List;

public record ApiError(Instant timestamp, int status, String error, List<String> details) {
}
