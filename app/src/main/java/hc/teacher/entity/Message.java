package hc.teacher.entity;

import hc.teacher.utils.MethodUtils;

import java.sql.Time;
import java.util.HashMap;
import java.util.Map;

public class Message
{
	private Integer id;
	private Integer sender_id;
	private Integer receiver_id;
	private Time time;
	private String content;
	private Integer stage;

	public Message(){
		this.sender_id = 0;
		this.receiver_id = 0;
		this.time = new Time(0);
		this.content = "";
		this.stage = -1;
	}

	public Message(int sender_id, int receiver_id, Time time, String content, int stage) {
		this.sender_id = sender_id;
		this.receiver_id = receiver_id;
		this.time = time;
		this.content = content;
		this.stage = stage;
	}

	public static Message getMessageByMap(Map messageMap){

		Message newMessage = new Message();

		String[] attributes = {"sender_id", "receiver_id", "time", "content", "stage"};
		try {

			for (String attribute : attributes) {

				if (null == messageMap.get(attribute)){
					return null;
				}else{

					String methodName = MethodUtils.getSetMethodNameByParam(attribute);
					MethodUtils.call("com.teacher.entity.Message", newMessage, methodName, messageMap.get(attribute));

				}

			}
		}
		catch (Exception e){

		}
		return newMessage;
	}

	public int getId()
	{
		return id;
	}
	public void setId(int id)
	{
		this.id = id;
	}
	public int getSender_id()
	{
		return sender_id;
	}
	public void setSender_id(int sender_id)
	{
		this.sender_id = sender_id;
	}
	public int getReceiver_id()
	{
		return receiver_id;
	}
	public void setReceiver_id(int receiver_id)
	{
		this.receiver_id = receiver_id;
	}
	public Time getTime()
	{
		return time;
	}
	public void setTime(Time time)
	{
		this.time = time;
	}
	public String getContent()
	{
		return content;
	}
	public void setContent(String content)
	{
		this.content = content;
	}
	public int getStage()
	{
		return stage;
	}
	public void setStage(int stage)
	{
		this.stage = stage;
	}

	public Map<String, Object> getMessageMap(){
		Map<String, Object> messageMap = new HashMap<String, Object>();
		messageMap.put("sender_id", sender_id);
		messageMap.put("receiver_id", receiver_id);
		messageMap.put("time", time);
		messageMap.put("content", content);
		messageMap.put("stage", stage);
		return messageMap;
	}
}
