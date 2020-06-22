package com.xyz.vendingmachine.machine.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA Entity for the attempts to open the machine
 */
@Entity
@Table(name = "open_attempt")
@Data
@Builder(builderClassName = "Builder")
@AllArgsConstructor
@NoArgsConstructor
public class OpenAttempt {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "attempt_timestamp")
    @CreationTimestamp
    private LocalDateTime timestamp;

    @Column
    private boolean success;
}
