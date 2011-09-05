package canada.tv.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import canada.tv.utilities.JSONizable;
import canada.tv.utilities.Logger;

public class Channel extends JSONizable {
	private final String NUMBER = "Number";
	private final String PROGRAM_SERVICE_ID = "PrgSvcID";
	private final String NAME = "Name";
	private final String CALL_SIGN = "CallSign";
	private final String PROGRAMS = "Programs";
	private final String POSITION = "Position";
	private final String POSTAL_CODE = "PostalCode";
	
	private long lastUpdate = -1;

	private int position;
	private int number;
	private int programServiceId;
	private String callSign;
	private String name;
	private List<Program> programs;
	private String postalCode;

	public Channel(JSONObject json, int position, String postalCode) throws JSONException {
		this.postalCode = postalCode;
		this.position = position;
		createFromJSON(json);
	}
	
	public Channel(JSONObject json) throws JSONException {
		createFromJSON(json);
	}

	public int getNumber() {
		return number;
	}

	public int getProgramServiceId() {
		return programServiceId;
	}

	public String getCallSign() {
		return callSign;
	}

	public String getName() {
		return name;
	}

	public List<Program> getPrograms() {
		return programs;
	}

	@Override
	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();
		json.put(NAME, name);
		json.put(PROGRAM_SERVICE_ID, programServiceId);
		json.put(CALL_SIGN, callSign);
		json.put(NUMBER, number);

		JSONArray jsonPrograms = new JSONArray();
		for (Program program : programs)
			jsonPrograms.put(program.toJSON());

		json.put(PROGRAMS, jsonPrograms);
		json.put(POSITION, position);
		json.put(POSTAL_CODE, postalCode);

		return json;
	}

	@Override
	public void createFromJSON(JSONObject json) throws JSONException {
		number = json.getInt(NUMBER);
		programServiceId = json.getInt(PROGRAM_SERVICE_ID);
		callSign = json.getString(CALL_SIGN);
		name = json.getString(NAME);

		JSONArray jsonPrograms = json.getJSONArray(PROGRAMS);
		programs = new ArrayList<Program>();
		for (int i = 0; i < jsonPrograms.length(); i++) {
			Program program = new Program(jsonPrograms.getJSONObject(i));
			programs.add(program);
		}
		
		if (!json.isNull(POSITION))
			position = json.getInt(POSITION);
		
		if (!json.isNull(POSTAL_CODE))
			postalCode = json.getString(POSTAL_CODE);
	}

	@Override
	protected void debug(String message) {
		Logger.debug(getClass(),message);
	}

	public String getPostalCode(){
		return postalCode;
	}
	public int getPosition() {
		return position;
	}
	
	public long getLastUpdate(){
		return lastUpdate;
	}

	public void setLastUpdate(long currentTimeMillis) {
		lastUpdate = currentTimeMillis;
	}

	public void setPorgrams(List<Program> programs) {
		this.programs = programs;
	}
}
