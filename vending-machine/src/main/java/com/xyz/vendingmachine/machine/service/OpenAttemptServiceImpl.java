package com.xyz.vendingmachine.machine.service;

import com.xyz.vendingmachine.machine.model.OpenAttempt;
import com.xyz.vendingmachine.machine.repository.OpenAttemptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Default implementation of {@link OpenAttemptService}
 * @author amarenco
 */
@Service("openAttemptService")
public class OpenAttemptServiceImpl implements OpenAttemptService {

    @Autowired
    private OpenAttemptRepository openAttemptRepository;


    @Override
    public void logAttempt(boolean success) {
        openAttemptRepository.save(OpenAttempt.builder()
                .success(success)
                .build());
    }


    @Override
    public List<OpenAttempt> getLatestAttempts(int total) {
        return openAttemptRepository.getLatestAttempts(PageRequest.of(0, total));
    }
}
