package com.xyz.vendingmachine.machine;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Mock implementation for {@link EurekaClient}
 * @author amarenco
 */
@Component
@Primary
@Profile("test")
public class ApplicationInfoManagerMock extends ApplicationInfoManager {
    public ApplicationInfoManagerMock() {
        super(null, null, null);
    }

    @Override
    public InstanceInfo getInfo() {
        return null;
    }
}
