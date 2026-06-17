use student_management;

insert into student
(student_no, name, gender, age, class_name, phone, email, address, score, status, remark)
values
('2026001', '张明', '男', 16, '高一1班', '13800000001', 'zhangming@example.com', '教学楼 A 区', 92.00, '在读', '数学成绩突出'),
('2026002', '李雨', '女', 15, '高一1班', '13800000002', 'liyu@example.com', '教学楼 A 区', 88.50, '在读', '班级学习委员'),
('2026003', '王浩', '男', 16, '高一2班', '13800000003', 'wanghao@example.com', '教学楼 B 区', 76.00, '在读', null),
('2026004', '陈思', '女', 16, '高一2班', '13800000004', 'chensi@example.com', '教学楼 B 区', 95.00, '在读', '综合表现优秀'),
('2026005', '赵宁', '男', 17, '高二1班', '13800000005', 'zhaoning@example.com', '教学楼 C 区', 67.00, '休学', '身体原因休学'),
('2026006', '周悦', '女', 17, '高二1班', '13800000006', 'zhouyue@example.com', '教学楼 C 区', 84.00, '毕业', null);
