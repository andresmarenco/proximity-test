package com.xyz.vendingmachine.machine;

import com.xyz.vendingmachine.machine.model.MachineType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.Clock;
import java.util.List;

/**
 * Configuration for the machine
 * @author amarenco
 */
@Configuration
@ConfigurationProperties(prefix = "vendingmachine")
@Getter
@Setter
public class MachineConfig {
    /** Default math context */
    private static final MathContext DEFAULT_MATH_CONTEXT = new MathContext(2, RoundingMode.HALF_UP);

    private MachineType machineType;
    private String securityCode;
    private int maxOpenAttempts;
    private List<Integer> validCoins;
    private List<Integer> validBills;
    private BigDecimal alertThreshold;

    private Clock defaultClock = Clock.systemDefaultZone();


    /**
     * @return the default math context
     */
    public MathContext getDefaultMathContext() {
        return DEFAULT_MATH_CONTEXT;
    }
}
