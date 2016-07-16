package hc.teacher.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by star on 2016/1/14.
 */
public class Question implements Serializable {

    private Integer id;
    private Integer user_id;
    private String description; //简述
    private String details;

    /*
    * 1 ---------> 已采纳
    * 2 ---------> 未采纳
    * */
    private Integer state;
    private String nickname;
    private Timestamp publish_time;
    private Timestamp invalidate_time;



    public Question() {
        this.user_id = 0;
        this.description = "";
        this.details = "";
        this.state = 0;
        this.nickname = "";
        this.publish_time = new Timestamp(0);
        this.invalidate_time = new Timestamp(0);

    }

    public Question(Integer user_id, String description, String details,
                    Integer state, String nickname, Timestamp publish_time, Timestamp invalidate_time) {
        this.user_id = user_id;
        this.description = description;
        this.details = details;
        this.state = state;
        this.nickname = nickname;
        this.publish_time = publish_time;
        this.invalidate_time = invalidate_time;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Timestamp getPublish_time() {
        return publish_time;
    }

    public void setPublish_time(Timestamp publish_time) {
        this.publish_time = publish_time;
    }

    public Timestamp getInvalidate_time() {
        return invalidate_time;
    }

    public void setInvalidate_time(Timestamp invalidate_time) {
        this.invalidate_time = invalidate_time;
    }

    public static Question getQuestionByMap(Map questionMap){

        Question newQuestion = new Question();



        newQuestion.setId(Integer.parseInt(new java.text.DecimalFormat("0").format(questionMap.get("ID"))));
        newQuestion.setUser_id(Integer.parseInt(new java.text.DecimalFormat("0").format(questionMap.get("USER_ID"))));
        newQuestion.setDescription((String) questionMap.get("DESCRIPTION"));
        newQuestion.setDetails((String) questionMap.get("DETAILS"));
        newQuestion.setState(Integer.parseInt(new java.text.DecimalFormat("0").format(questionMap.get("STATE"))));
        newQuestion.setPublish_time(Timestamp.valueOf((String) questionMap.get("PUBLISH_TIME")));
        newQuestion.setInvalidate_time(Timestamp.valueOf((String) questionMap.get("PUBLISH_TIME")));
        newQuestion.setNickname((String)questionMap.get("NICKNAME"));



        return newQuestion;
    }

    public Map<String, Object> getQuestionMap(){
        Map<String, Object> questionMap = new HashMap<String, Object>();
        questionMap.put("ID", id);
        questionMap.put("USER_ID", user_id);
        questionMap.put("DESCRIPTION", description);
        questionMap.put("DETAILS", details);
        questionMap.put("STATE", state);
        questionMap.put("PUBLISH_TIME", publish_time);
        questionMap.put("INVALIDATE_TIME", invalidate_time);
        questionMap.put("NICKNAME", nickname);
        return questionMap;
    }

}
