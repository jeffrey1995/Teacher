package hc.teacher.entity;

import hc.teacher.utils.MethodUtils;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

public class RealInfo {

    public static final int STATE_IS_CHECKING = 2;
    public static final int STATE_HAS_CHECKED = 1;
    public static final int GENDER_MALE = 2;
    public static final int GENDER_FEMALE = 2;

    private int user_id;
    private String name;
    private int gender;
    private Date birthday;
    private String identity_number; //���֤��
    private String school;
    private String real_head;
    private String introduction;
    private String email;
    private String tel;
    private int state;

    public RealInfo() {

        user_id = -1;
        name = "";
        gender = 0;
        birthday = new Date(0);
        identity_number = "";
        school = "";
        real_head = "";
        introduction = "";
        email = "";
        tel = "";
        state = 2;

    }

    public RealInfo(int user_id, String name, int gender, Date birthday, String identity_number, String school, String real_head, String introduction, String email, String tel) {
        this.user_id = user_id;
        this.name = name;
        this.gender = gender;
        this.birthday = birthday;
        this.identity_number = identity_number;
        this.school = school;
        this.real_head = real_head;
        this.introduction = introduction;
        this.email = email;
        this.tel = tel;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getIdentity_number() {
        return identity_number;
    }

    public void setIdentity_number(String identity_number) {
        this.identity_number = identity_number;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getReal_head() {
        return real_head;
    }

    public void setReal_head(String real_head) {
        this.real_head = real_head;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public static RealInfo getRealInfoByMap(Map infoMap) {

        RealInfo real_info = new RealInfo();

        String[] attributes = {"name", "gender", "birthday", "identity_number",
                "school", "real_head", "introduction", "email", "tel"};
        try {

            for (String attribute : attributes) {
                if (null == infoMap.get(attribute)) {
                    return null;
                } else {
                    String methodName = MethodUtils.getSetMethodNameByParam(attribute);
                    MethodUtils.call("com.teacher.entity.User", real_info, methodName, infoMap.get(attribute));
                }

            }
        } catch (Exception e) {

        }
        return real_info;
    }


    public Map<String, Object> getUserMap() {
        Map<String, Object> infoMap = new HashMap<String, Object>();

        infoMap.put("name", name);
        infoMap.put("gender", gender);
        infoMap.put("birthday", birthday);
        infoMap.put("identity_number", identity_number);
        infoMap.put("school", school);
        infoMap.put("real_head", real_head);
        infoMap.put("introduction", introduction);
        infoMap.put("email", email);
        infoMap.put("tel", tel);

        return infoMap;
    }

}
