package com.example.forum.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;

@Entity
@Table(name = "comments")
@Getter
@Setter
public class Comment {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String text;

    @Column
    private int reportId;

    @Column(name = "created_date", insertable = false, updatable = false)
    private Date createdDate;

}
