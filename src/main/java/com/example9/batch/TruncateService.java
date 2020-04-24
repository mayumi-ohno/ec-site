package com.example9.batch;

import org.springframework.batch.core.ExitStatus;

public interface TruncateService {

	ExitStatus execute();
}
