package com.xyz.vendingmachine.machine;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.HealthCheckCallback;
import com.netflix.appinfo.HealthCheckHandler;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.EurekaClientConfig;
import com.netflix.discovery.EurekaEventListener;
import com.netflix.discovery.shared.Application;
import com.netflix.discovery.shared.Applications;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * Mock implementation for {@link EurekaClient}
 * @author amarenco
 */
@Component
@Primary
@Profile("test")
public class EurekaClientMock implements EurekaClient {
    @Override
    public Applications getApplicationsForARegion(String s) {
        return null;
    }

    @Override
    public Applications getApplications(String s) {
        return null;
    }

    @Override
    public List<InstanceInfo> getInstancesByVipAddress(String s, boolean b) {
        return null;
    }

    @Override
    public List<InstanceInfo> getInstancesByVipAddress(String s, boolean b, String s1) {
        return null;
    }

    @Override
    public List<InstanceInfo> getInstancesByVipAddressAndAppName(String s, String s1, boolean b) {
        return null;
    }

    @Override
    public Set<String> getAllKnownRegions() {
        return null;
    }

    @Override
    public InstanceInfo.InstanceStatus getInstanceRemoteStatus() {
        return null;
    }

    @Override
    public List<String> getDiscoveryServiceUrls(String s) {
        return null;
    }

    @Override
    public List<String> getServiceUrlsFromConfig(String s, boolean b) {
        return null;
    }

    @Override
    public List<String> getServiceUrlsFromDNS(String s, boolean b) {
        return null;
    }

    @Override
    public void registerHealthCheckCallback(HealthCheckCallback healthCheckCallback) {

    }

    @Override
    public void registerHealthCheck(HealthCheckHandler healthCheckHandler) {

    }

    @Override
    public void registerEventListener(EurekaEventListener eurekaEventListener) {

    }

    @Override
    public boolean unregisterEventListener(EurekaEventListener eurekaEventListener) {
        return false;
    }

    @Override
    public HealthCheckHandler getHealthCheckHandler() {
        return null;
    }

    @Override
    public void shutdown() {

    }

    @Override
    public EurekaClientConfig getEurekaClientConfig() {
        return null;
    }

    @Override
    public ApplicationInfoManager getApplicationInfoManager() {
        return null;
    }

    @Override
    public Application getApplication(String s) {
        return null;
    }

    @Override
    public Applications getApplications() {
        return null;
    }

    @Override
    public List<InstanceInfo> getInstancesById(String s) {
        return null;
    }

    @Override
    public InstanceInfo getNextServerFromEureka(String s, boolean b) {
        return null;
    }
}
