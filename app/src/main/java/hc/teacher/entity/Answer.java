package hc.teacher.entity;


import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import hc.teacher.utils.MethodUtils;

/**
 * Created by xjs on 2015/12/15.
 */
public class Answer {

    private Integer id;
    private String details;
    private Integer user_id;
    private Timestamp publish_time;

    /*
    * 2 <-------- 未采用
    * 1 <-------- 采用
    * */
    private Integer isAccepted;
    private Integer question_id;
    private Integer group_num;

    public Answer() {
        this.details = "";
        this.user_id = 0;
        this.publish_time = new Timestamp(0);
        this.isAccepted = 0;
        this.question_id = 0;
        this.group_num = 0;
    }

    public Answer(String details, Integer user_id, Timestamp publish_time, Integer isAccepted, Integer question_id, Integer group_num) {
        this.details = details;
        this.user_id = user_id;
        this.publish_time = publish_time;
        this.isAccepted = isAccepted;
        this.question_id = question_id;
        this.group_num = group_num;
    }

    public Integer getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(Integer question_id) {
        this.question_id = question_id;
    }

    public Integer getGroup_num() {
        return group_num;
    }

    public void setGroup_num(Integer group_num) {
        this.group_num = group_num;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public Timestamp getPublish_time() {
        return publish_time;
    }

    public void setPublish_time(Timestamp publish_time) {
        this.publish_time = publish_time;
    }

    public Integer getIsAccepted() {
        return isAccepted;
    }

    public void setIsAccepted(Integer isAccepted) {
        this.isAccepted = isAccepted;
    }

    public static Answer getAnswerByMap(Map answerMap){

        Answer newAnswer = new Answer();

        newAnswer.setId(Integer.parseInt(new java.text.DecimalFormat("0").format(answerMap.get("ID"))));
        newAnswer.setUser_id(Integer.parseInt(new java.text.DecimalFormat("0").format(answerMap.get("USER_ID"))));
        newAnswer.setDetails((String) answerMap.get("DETAILS"));
        newAnswer.setPublish_time(Timestamp.valueOf((String) answerMap.get("PUBLISH_TIME")));
        newAnswer.setIsAccepted(Integer.parseInt(new java.text.DecimalFormat("0").format(answerMap.get("ISACCEPTED"))));
        newAnswer.setQuestion_id(Integer.parseInt(new java.text.DecimalFormat("0").format(answerMap.get("QUESTION_ID"))));
        newAnswer.setGroup_num(Integer.parseInt(new java.text.DecimalFormat("0").format(answerMap.get("GROUP_NUM"))));

        /*String[] attributes = {"details","user_id","publish_time", "isAccepted","question_id","group_num"};

        try {

            for (String attribute : attributes) {

                if (null == answerMap.get(attribute)){
                    return null;
                }else{
                    String methodName = MethodUtils.getSetMethodNameByParam(attribute);
                    MethodUtils.call("com.teacher.entity.Answer", newAnswer, methodName, answerMap.get(attribute));
                }

            }
        }
        catch (Exception e){

        }*/


        return newAnswer;
    }

    public Map<String, Object> getAnswerMap(){
        Map<String, Object> answerMap = new HashMap<String, Object>();
        answerMap.put("details", details);
        answerMap.put("user_id", user_id);
        answerMap.put("publish_time", publish_time);
        answerMap.put("isAccepted", isAccepted);
        answerMap.put("question_id",question_id);
        answerMap.put("group_num", group_num);
        return answerMap;
    }

    /*private Integer id;
        private String details;
        private Integer user_id;
        private Timestamp publish_time;
        private Integer isAccepted;
        private Integer question_id;
        private Integer group_num;*/

    @Override
    public String toString() {
        return "GROUP_NUM:" + getGroup_num() + "USER_ID:" + getUser_id() + "DETAILS:" + getDetails() + "ID" + getId()
                + "PUBLISH_TIME:" + getPublish_time() + "ISACCEPTED:" + getIsAccepted() + "QUESTION_ID" + getQuestion_id();
    }
}
