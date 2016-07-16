package hc.teacher.entity;

import hc.teacher.utils.MethodUtils;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class Information {
    public static int TYPE_FIND = 1;
    public static int TYPE_DO = 2;
    public static int GENDER_MALE = 1;
    public static int GENDER_FEMALE = 2;
    public static int GENDER_BOTH = 2;

    private int id;
    private int user_id;
    private int type;
    private String title;
    private String subject;
    private String grade;
    private String worktime;
    private String salary;
    private int gender;
    private int needGender;
    private String description;
    private String address;
    private String tel;
    private int permit;
    private String contactname;
    private Timestamp commit_date;
    private int isChecked;
    private String nickname;

    public Information() {
        this.user_id = 0;
        this.type = 0;
        this.title = "";
        this.subject = "";
        this.grade = "";
        this.worktime = "";
        this.salary = "";
        this.gender = 0;
        this.needGender = 0;
        this.description = "";
        this.address = "";
        this.tel = "";
        this.permit = 0;
        this.contactname = "";
        this.commit_date = new Timestamp(0);
        this.isChecked = 2;
        this.nickname = "";
    }

    public Information(int user_id,String nickname, int type, String title,
                       String subject, String grade, String worktime, String salary,
                       int gender,int need_gender, String description, String address,
                       String tel, int is_checked,int permit, String contactname, Timestamp commit_date) {
        this.user_id = user_id;
        this.nickname = nickname;
        this.type = type;
        this.title = title;
        this.subject = subject;
        this.grade = grade;
        this.worktime = worktime;
        this.salary = salary;
        this.gender = gender;
        this.needGender = 0;
        this.description = description;
        this.address = address;
        this.tel = tel;
        this.isChecked = is_checked;
        this.permit = permit;
        this.contactname = contactname;
        this.commit_date = commit_date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_Id() {
        return user_id;
    }

    public void setUser_Id(int user_id) {
        this.user_id = user_id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getWorktime() {
        return worktime;
    }

    public void setWorktime(String worktime) {
        this.worktime = worktime;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int sex) {
        this.gender = sex;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getNeedGender() {
        return needGender;
    }

    public void setNeedGender(int need_sex) {
        this.needGender = need_sex;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public int getPermit() {
        return permit;
    }

    public void setPermit(int permit) {
        this.permit = permit;
    }

    public String getContactname() {
        return contactname;
    }

    public void setContactname(String contactname) {
        this.contactname = contactname;
    }

    public Timestamp getCommit_date() {
        return commit_date;
    }

    public void setCommit_date(Timestamp commit_date) {
        this.commit_date = commit_date;
    }

    public int getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(int isChecked) {
        this.isChecked = isChecked;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Map<String, Object> getInformationMap(){
        Map<String, Object> informationMap = new HashMap<String, Object>();
        informationMap.put("user_id", user_id);
        informationMap.put("nickname",nickname);
        informationMap.put("type", type);
        informationMap.put("title", title);
        informationMap.put("subject", subject);
        informationMap.put("grade", grade);
        informationMap.put("worktime", worktime);
        informationMap.put("salary", salary);
        informationMap.put("gender", gender);
        informationMap.put("need_gender", needGender);
        informationMap.put("description", description);
        informationMap.put("address", address);
        informationMap.put("tel", tel);
        informationMap.put("is_checked", isChecked);
        informationMap.put("permit",permit);
        informationMap.put("contact_name", contactname);
        informationMap.put("commit_date", commit_date);
        return informationMap;
    }
}
