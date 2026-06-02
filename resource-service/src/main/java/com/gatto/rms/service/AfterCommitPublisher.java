package com.gatto.rms.service;

import com.gatto.rms.contracts.ResourceView;
import com.gatto.rms.publisher.RestPublisherClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
@RequiredArgsConstructor
public class AfterCommitPublisher {

    private final RestPublisherClient restPublisherClient;

    public void publishCreate(ResourceView view) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() { restPublisherClient.publishCreate(view); }
        });
    }

    public void publishUpdate(ResourceView view) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() { restPublisherClient.publishUpdate(view); }
        });
    }

    public void publishDelete(ResourceView view) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() { restPublisherClient.publishDelete(view); }
        });
    }
}
