package com.rubicon.Rubicon.Water.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull
    private long farmId;

    @NotNull
    private Date startDateTime;

    @NotNull
    private int duration;

    @NotNull
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_time")
    private Date creationDate;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_modified_time")
    private Date lastModifiedDate;
}
