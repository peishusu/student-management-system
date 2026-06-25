package com.example.student.service;

import com.example.student.dto.StudentQuery;
import com.example.student.dto.StudentRequest;
import com.example.student.dto.StudentStats;
import com.example.student.dto.PageResult;
import com.example.student.entity.Student;

import java.util.List;

public interface StudentService {
    PageResult<Student> list(StudentQuery query);

    Student getById(Long id);

    Student create(StudentRequest request);

    Student update(Long id, StudentRequest request);

    void delete(Long id);

    List<String> classNames();

    StudentStats stats();
}
