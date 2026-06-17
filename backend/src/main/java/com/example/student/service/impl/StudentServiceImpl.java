package com.example.student.service.impl;

import com.example.student.dto.StudentQuery;
import com.example.student.dto.StudentRequest;
import com.example.student.dto.StudentStats;
import com.example.student.entity.Student;
import com.example.student.exception.BusinessException;
import com.example.student.mapper.StudentMapper;
import com.example.student.service.StudentService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class StudentServiceImpl implements StudentService {
    private final StudentMapper studentMapper;

    public StudentServiceImpl(StudentMapper studentMapper) {
        this.studentMapper = studentMapper;
    }

    @Override
    @Cacheable(value = "student:list", key = "#query == null ? 'all' : (#query.keyword + ':' + #query.className + ':' + #query.status)")
    public List<Student> list(StudentQuery query) {
        return studentMapper.selectList(query);
    }

    @Override
    @Cacheable(value = "student:detail", key = "#id")
    public Student getById(Long id) {
        Student student = studentMapper.selectById(id);
        if (student == null) {
            throw new BusinessException("学生不存在");
        }
        return student;
    }

    @Override
    @Transactional
    @CacheEvict(value = {"student:list", "student:detail", "student:classNames", "student:stats"}, allEntries = true)
    public Student create(StudentRequest request) {
        assertStudentNoAvailable(request.getStudentNo(), null);
        Student student = toEntity(request);
        studentMapper.insert(student);
        return studentMapper.selectById(student.getId());
    }

    @Override
    @Transactional
    @CacheEvict(value = {"student:list", "student:detail", "student:classNames", "student:stats"}, allEntries = true)
    public Student update(Long id, StudentRequest request) {
        Student old = studentMapper.selectById(id);
        if (old == null) {
            throw new BusinessException("学生不存在");
        }
        assertStudentNoAvailable(request.getStudentNo(), id);
        Student student = toEntity(request);
        student.setId(id);
        studentMapper.update(student);
        return studentMapper.selectById(id);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"student:list", "student:detail", "student:classNames", "student:stats"}, allEntries = true)
    public void delete(Long id) {
        if (studentMapper.deleteById(id) == 0) {
            throw new BusinessException("学生不存在");
        }
    }

    @Override
    @Cacheable(value = "student:classNames", key = "'all'")
    public List<String> classNames() {
        return studentMapper.selectClassNames();
    }

    @Override
    @Cacheable(value = "student:stats", key = "'overview'")
    public StudentStats stats() {
        List<Student> students = studentMapper.selectList(new StudentQuery());
        StudentStats stats = new StudentStats();
        stats.setTotal(students.size());
        stats.setAverageScore(averageScore(students));
        stats.setExcellentCount(students.stream().filter(student -> student.getScore().compareTo(new BigDecimal("90")) >= 0).count());
        stats.setClassDistribution(classDistribution(students));
        stats.setScoreDistribution(scoreDistribution(students));
        stats.setClassCount(stats.getClassDistribution().size());
        return stats;
    }

    private void assertStudentNoAvailable(String studentNo, Long currentId) {
        Student existing = studentMapper.selectByStudentNo(studentNo);
        if (existing != null && !existing.getId().equals(currentId)) {
            throw new BusinessException("学号已存在");
        }
    }

    private Student toEntity(StudentRequest request) {
        Student student = new Student();
        student.setStudentNo(request.getStudentNo());
        student.setName(request.getName());
        student.setGender(request.getGender());
        student.setAge(request.getAge());
        student.setClassName(request.getClassName());
        student.setPhone(request.getPhone());
        student.setEmail(request.getEmail());
        student.setAddress(request.getAddress());
        student.setScore(request.getScore());
        student.setStatus(request.getStatus());
        student.setRemark(request.getRemark());
        return student;
    }

    private BigDecimal averageScore(List<Student> students) {
        if (students.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (Student student : students) {
            total = total.add(student.getScore());
        }
        return total.divide(new BigDecimal(students.size()), 2, RoundingMode.HALF_UP);
    }

    private Map<String, Long> classDistribution(List<Student> students) {
        Map<String, Long> result = new LinkedHashMap<String, Long>();
        for (Student student : students) {
            result.put(student.getClassName(), result.getOrDefault(student.getClassName(), 0L) + 1);
        }
        return result;
    }

    private Map<String, Long> scoreDistribution(List<Student> students) {
        Map<String, Long> result = new LinkedHashMap<String, Long>();
        result.put("90-100", 0L);
        result.put("80-89", 0L);
        result.put("60-79", 0L);
        result.put("0-59", 0L);
        for (Student student : students) {
            BigDecimal score = student.getScore();
            if (score.compareTo(new BigDecimal("90")) >= 0) {
                result.put("90-100", result.get("90-100") + 1);
            } else if (score.compareTo(new BigDecimal("80")) >= 0) {
                result.put("80-89", result.get("80-89") + 1);
            } else if (score.compareTo(new BigDecimal("60")) >= 0) {
                result.put("60-79", result.get("60-79") + 1);
            } else {
                result.put("0-59", result.get("0-59") + 1);
            }
        }
        return result;
    }
}
