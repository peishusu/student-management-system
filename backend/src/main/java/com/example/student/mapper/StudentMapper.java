package com.example.student.mapper;

import com.example.student.dto.StudentQuery;
import com.example.student.entity.Student;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StudentMapper {
    List<Student> selectList(@Param("query") StudentQuery query);

    Student selectById(@Param("id") Long id);

    Student selectByStudentNo(@Param("studentNo") String studentNo);

    List<String> selectClassNames();

    int insert(Student student);

    int update(Student student);

    int deleteById(@Param("id") Long id);
}
