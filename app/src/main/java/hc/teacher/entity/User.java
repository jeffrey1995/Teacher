package hc.teacher.entity;

import hc.teacher.utils.MethodUtils;

import java.sql.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class User {

    public static int IDENTITY_FIND = 1;
    public static int IDENTITY_DO = 2;
    public static int GENDER_MALE = 1;
    public static int GENDER_FEMALE = 2;

    public static final int REALINFO_VERIFY_NO = 3;
    public static final int REALINFO_VERIFY_IS_CHECKING = 2;
    public static final int REALINFO_VERIFY_HAS_CHECKED = 1;

    LinkedList<Contact> contactList = new LinkedList<Contact>(); //ͨѶ¼�����б�

    private Integer id;
    private String account;
    private String password;
    private Integer identity;
    private String nickname;
    private Integer gender;
    private String address;
    private String email;
    private String qqNumber;
    private String tel;
    private Integer isPublic;
    private String description;   //����ǩ��
    private String head;    //ͷ��
    private Date register_date;
    private Integer isOnline;
    private String token;
    private int identify_state;

    public User() {
        this.id = -1;
        this.account = "";
        this.password = "";
        this.identity = 0;
        this.nickname = "创师纪用户";
        this.gender = 1;
        this.address = "";
        this.email = "";
        this.qqNumber = "";
        this.tel = "";
        this.isPublic = -1;
        this.description = "个性签名";
        this.head = "";
        this.register_date = new Date(0);
        this.isOnline = 0;
        this.token = "";
        this.identify_state = User.REALINFO_VERIFY_NO;
    }

    public User(Integer id, String account, String password,
                Integer identity, String nickname, Integer gender,
                String address, String email, String qqnumber, String tel,
                Integer ispublic, String description, String head,
                Date register_date, Integer isonline, String token, int identify_state) {
        this.id = id;
        this.account = account;
        this.password = password;
        this.identity = identity;
        this.nickname = nickname;
        this.gender = gender;
        this.address = address;
        this.email = email;
        this.qqNumber = qqnumber;
        this.tel = tel;
        this.isPublic = ispublic;
        this.description = description;
        this.head = head;
        this.register_date = register_date;
        this.isOnline = isonline;
        this.token = token;
        this.identify_state = identify_state;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getIdentity() {
        return identity;
    }

    public void setIdentity(Integer identity) {
        this.identity = identity;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getQqnumber() {
        return qqNumber;
    }

    public void setQQNumber(String qqnumber) {
        this.qqNumber = qqnumber;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public Integer getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Integer ispublic) {
        this.isPublic = ispublic;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public Date getRegister_date() {
        return register_date;
    }

    public void setRegister_date(Date register_date) {
        this.register_date = register_date;
    }

    public Integer getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(Integer isonline) {
        this.isOnline = isonline;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setIdentify_state(int identify_state) {
        this.identify_state = identify_state;
    }

    public int getIdentify_state() {
        return identify_state;
    }


    public static User getUserByMap(Map userMap) {

        User newUser = new User();

        String[] attributes = {"id", "account", "password", "identity",
                "nickname", "gender", "address", "email", "qqnumber", "tel",
                "ispublic", "description", "head", "register_date", "isonline", "token"};
        try {

            for (String attribute : attributes) {
                if (null == userMap.get(attribute)) {
                    return null;
                } else {
                    String methodName = MethodUtils.getSetMethodNameByParam(attribute);
                    MethodUtils.call("com.teacher.entity.User", newUser, methodName, userMap.get(attribute));
                }

            }
        } catch (Exception e) {

        }
        return newUser;
    }


    public Map<String, Object> getUserMap() {
        Map<String, Object> userMap = new HashMap<String, Object>();
        userMap.put("id", id);
        userMap.put("account", account);
        userMap.put("password", password);
        userMap.put("identity", identity);
        userMap.put("nickname", nickname);
        userMap.put("gender", gender);
        userMap.put("address", address);
        userMap.put("email", email);
        userMap.put("qqnumber", qqNumber);
        userMap.put("tel", tel);
        userMap.put("ispublic", isPublic);
        userMap.put("description", description);
        userMap.put("head", head);
        userMap.put("register_date", register_date);
        userMap.put("isonline", isOnline);
        userMap.put("token", token);

        return userMap;
    }


}











