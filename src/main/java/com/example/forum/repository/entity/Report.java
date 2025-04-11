package com.example.forum.repository.entity;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "report")
@Getter
@Setter
public class Report {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String content;

    @Column(name = "created_date", insertable = false, updatable = false)
    private Date createdDate;

    @Column(name = "updated_date", insertable = false, updatable = true)
    private Date updatedDate;


}

