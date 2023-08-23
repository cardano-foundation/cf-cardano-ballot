package org.cardano.foundation.voting.service.health;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class YaciStoreTipHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        // TODO Yaci-Store integration to check if we have joined the tip

        return Health
                .up()
                .withDetail("message", "Yaci-Store integration not implemented yet!")
                .build();
    }

}
