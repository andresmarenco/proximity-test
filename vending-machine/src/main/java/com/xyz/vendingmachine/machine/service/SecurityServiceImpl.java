package com.xyz.vendingmachine.machine.service;

import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * Default implementation of {@link SecurityService}
 * @author amarenco
 */
@Service("securityService")
@Slf4j
public class SecurityServiceImpl implements SecurityService {
    /** The name of the server application */
    private static final String SERVER_APPLICATION = "server";

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private EurekaClient eurekaClient;


    @Override
    public boolean isServerRequest() {
        boolean result = false;

        Application application = eurekaClient.getApplication(SERVER_APPLICATION);
        if (application != null) {
            if (application.getInstances().stream().anyMatch(instance -> instance.getIPAddr().equals(request.getRemoteAddr()))) {
                log.info("Request from server...");
                result = true;
            }
        }

        return result;
    }
}
