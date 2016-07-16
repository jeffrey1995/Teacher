package hc.teacher.entity;

import android.widget.TextView;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

public class Order {

    private Integer id;
    private Integer stu_id;
    private Integer teacher_id;
    private String teacher_name;
    private String contactName;
    private String contactTel;
    private String contactAddress;
    private String section;
    private String subject;
    private String grade;
    private String workTime;
    private String salary;
    private String description;
    private Integer state;
    private Date init_time;
    private Date accept_time;
    private Date invalidate_time;

    public Order(){
        teacher_name = "";
        contactName = "";
        contactTel = "";
        contactAddress = "";
        section = "";
        subject = "";
        grade = "";
        workTime = "";
        salary = "";
        description = "";
        state = 0;
        init_time = new Date(0);
        accept_time = new Date(0);
        invalidate_time = new Date(0);
    }

    public Order(Integer id, Integer stu_id, Integer teacher_id,
                 String teacher_name, String contactName, String contactTel,
                 String contactAddress, String section, String subject,
                 String grade, String work_time, String salary,
                 String description, Integer state, Date init_time,
                 Date accept_time, Date invalidate_time) {
        this.id = id;
        this.stu_id = stu_id;
        this.teacher_id = teacher_id;
        this.teacher_name = teacher_name;
        this.contactName = contactName;
        this.contactTel = contactTel;
        this.contactAddress = contactAddress;
        this.section = section;
        this.subject = subject;
        this.grade = grade;
        this.workTime = work_time;
        this.salary = salary;
        this.description = description;
        this.state = state;
        this.init_time = init_time;
        this.accept_time = accept_time;
        this.invalidate_time = invalidate_time;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStu_id() {
        return stu_id;
    }

    public void setStu_id(Integer stu_id) {
        this.stu_id = stu_id;
    }

    public Integer getTeacher_id() {
        return teacher_id;
    }

    public void setTeacher_id(Integer teacher_id) {
        this.teacher_id = teacher_id;
    }

    public String getTeacher_name() {
        return teacher_name;
    }

    public void setTeacher_name(String teacher_name) {
        this.teacher_name = teacher_name;
    }

    public String getContactName()
    {
        return this.contactName;
    }

    public void setContactName(String contactName)
    {
        this.contactName = contactName;
    }

    public String getContactTel()
    {
        return this.contactTel;
    }

    public void setContactTel(String contactTel)
    {
        this.contactTel = contactTel;
    }

    public String getContactAddress()
    {
        return this.contactAddress;
    }

    public void setContactAddress(String contactAddress)
    {
        this.contactAddress = contactAddress;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getGrade()
    {
        return grade;
    }

    public void setGrade(String grade)
    {
        this.grade = grade;
    }

    public String getWorkTime()
    {
        return workTime;
    }

    public void setWorkTime(String workTime)
    {
        this.workTime = workTime;
    }


    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Date getInit_time() {
        return init_time;
    }

    public void setInit_time(Date init_time) {
        this.init_time = init_time;
    }

    public Date getAccept_time() {
        return accept_time;
    }

    public void setAccept_time(Date accept_time) {
        this.accept_time = accept_time;
    }

    public Date getInvalidate_time() {
        return invalidate_time;
    }

    public void setInvalidate_time(Date invalidate_time) {
        this.invalidate_time = invalidate_time;
    }

    public Map<String, Object> getOrderMap(){
        Map<String, Object> orderMap = new HashMap<String, Object>();
        orderMap.put("id",id);
        orderMap.put("stu_id", stu_id);
        orderMap.put("teacher_id", teacher_id);
        orderMap.put("teacher_name", teacher_name);
        orderMap.put("contact_name", contactName);
        orderMap.put("contact_tel", contactTel);
        orderMap.put("contact_address", contactAddress);
        orderMap.put("section", section);
        orderMap.put("subject", subject);
        orderMap.put("grade", grade);
        orderMap.put("work_time", workTime);
        orderMap.put("salary", salary);
        orderMap.put("description", description);
        orderMap.put("state", state);
        orderMap.put("init_time",init_time);
        orderMap.put("accept_time",accept_time);
        orderMap.put("invalidate_time",invalidate_time);

        return orderMap;
    }

}
