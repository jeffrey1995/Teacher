package hc.teacher.entity;


import hc.teacher.utils.MethodUtils;
import java.util.HashMap;
import java.util.Map;

public class Contact {
	
	private int id;
	private int first_id;
	private int second_id;
	private int stage;

	public Contact(){
		this.first_id = 0;
		this.second_id = 0;
		this.stage = 0;
	}

	public Contact(int first_id, int second_id, int stage) {
		this.first_id = first_id;
		this.second_id = second_id;
		this.stage = stage;
	}

	public static Contact getContactByMap(Map contactMap){

		Contact newContact = new Contact();

		String[] attributes = {"first_id", "second_id", "stage"};
		try {

			for (String attribute : attributes) {

				if (null == contactMap.get(attribute)){
					return null;
				}else{

					String methodName = MethodUtils.getSetMethodNameByParam(attribute);
					MethodUtils.call("com.teacher.entity.Contact", newContact, methodName, contactMap.get(attribute));

				}

			}
		}
		catch (Exception e){

		}
		return newContact;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getFirst_id() {
		return first_id;
	}
	public void setFirst_id(int first_id) {
		this.first_id = first_id;
	}
	public int getSecond_id() {
		return second_id;
	}
	public void setSecond_id(int second_id) {
		this.second_id = second_id;
	}
	public int getStage() {
		return stage;
	}
	public void setStage(int stage) {
		this.stage = stage;
	}

	public Map<String, Object> getContactMap(){
		Map<String, Object> contactMap = new HashMap<String, Object>();
		contactMap.put("first_id", first_id);
		contactMap.put("second_id", second_id);
		contactMap.put("stage", stage);
		return contactMap;
	}
}
