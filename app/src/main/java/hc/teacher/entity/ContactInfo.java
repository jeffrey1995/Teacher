package hc.teacher.entity;

public class ContactInfo {

    private Integer id;
    private Integer user_id;
    private String contact_name;
    private String tel;
    private String address;

    public ContactInfo() {
        id = 0;
        user_id = 0;
        contact_name = "";
        tel = "";
        address = "";
    }

    public ContactInfo(int id, int user_id, String contact_name, String tel, String address) {
        this.id = id;
        this.user_id = user_id;
        this.contact_name = contact_name;
        this.tel = tel;
        this.address = address;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setUserId(int userId) {
        this.user_id = userId;
    }

    public Integer getUserId() {
        return this.user_id;
    }

    public void setContactName(String contractName) {
        this.contact_name = contractName;
    }

    public String getContactName() {
        return this.contact_name;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getTel() {
        return this.tel;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return this.address;
    }
}
