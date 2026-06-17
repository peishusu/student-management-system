package com.example.student.controller;

import com.example.student.common.ApiResponse;
import com.example.student.dto.StudentQuery;
import com.example.student.dto.StudentRequest;
import com.example.student.dto.StudentStats;
import com.example.student.entity.Student;
import com.example.student.service.StudentService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/students")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public ApiResponse<List<Student>> list(StudentQuery query) {
        return ApiResponse.success(studentService.list(query));
    }

    @GetMapping("/{id}")
    public ApiResponse<Student> getById(@PathVariable Long id) {
        return ApiResponse.success(studentService.getById(id));
    }

    @PostMapping
    public ApiResponse<Student> create(@Valid @RequestBody StudentRequest request) {
        return ApiResponse.success("创建成功", studentService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<Student> update(@PathVariable Long id, @Valid @RequestBody StudentRequest request) {
        return ApiResponse.success("更新成功", studentService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        studentService.delete(id);
        return ApiResponse.success("删除成功", null);
    }

    @GetMapping("/classes")
    public ApiResponse<List<String>> classNames() {
        return ApiResponse.success(studentService.classNames());
    }

    @GetMapping("/stats")
    public ApiResponse<StudentStats> stats() {
        return ApiResponse.success(studentService.stats());
    }
}
