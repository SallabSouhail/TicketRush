package com.ticketrush.event.shared.config;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Single source of the current time. Inject this Clock instead of calling Instant.now(),
 * so tests can fix the time with Clock.fixed(...).
 */
@Configuration(proxyBeanMethods = false)
public class ClockConfig {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }
}
