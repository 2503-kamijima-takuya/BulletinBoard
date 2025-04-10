package com.example.forum.controller.form;

import com.example.forum.repository.entity.Report;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CommentForm {

    private int id;

    private String text;

    private int reportId;

    private Date createdDate;

}
