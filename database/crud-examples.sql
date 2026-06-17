use student_management;

-- 新增学生
insert into student
(student_no, name, gender, age, class_name, phone, email, address, score, status, remark)
values
('2026999', '测试学生', '男', 16, '高一3班', '13900009999', 'test@example.com', '教学楼 D 区', 86.50, '在读', 'SQL 示例数据');

-- 查询全部学生
select *
from student
order by updated_at desc, id desc;

-- 按关键字查询学生
select *
from student
where student_no like '%2026%'
   or name like '%张%'
   or class_name like '%高一%'
   or phone like '%138%';

-- 按班级和状态筛选
select *
from student
where class_name = '高一1班'
  and status = '在读';

-- 更新学生信息
update student
set phone = '13900008888',
    score = 90.00,
    remark = '已更新联系方式和成绩'
where student_no = '2026999';

-- 删除学生
delete from student
where student_no = '2026999';

-- 统计总人数、平均分、优秀人数、班级数
select
  count(*) as total,
  round(avg(score), 2) as average_score,
  sum(case when score >= 90 then 1 else 0 end) as excellent_count,
  count(distinct class_name) as class_count
from student;

-- 班级人数统计
select class_name, count(*) as student_count
from student
group by class_name
order by class_name;

-- 成绩分布统计
select
  case
    when score >= 90 then '90-100'
    when score >= 80 then '80-89'
    when score >= 60 then '60-79'
    else '0-59'
  end as score_range,
  count(*) as student_count
from student
group by score_range
order by min(score) desc;
