package com.xyz.vendingmachine.server.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA Entity for alerts
 * @author amarenco
 */
@Entity
@Table(name = "alert")
@Data
@Builder(builderClassName = "Builder")
@NoArgsConstructor
@AllArgsConstructor
public class Alert {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "alert_timestamp")
    @CreationTimestamp
    protected LocalDateTime timestamp;

    @ManyToOne
    private VendingMachine machine;

    @Column
    private boolean solved;
}
