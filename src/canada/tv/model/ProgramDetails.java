package canada.tv.model;

import org.json.JSONException;
import org.json.JSONObject;

import canada.tv.utilities.JSONizable;
import canada.tv.utilities.Logger;

public class ProgramDetails extends JSONizable {
	private final String BANNER = "Banner";
	private final String DESCRIPTION = "Description";
	private final String PROGRAM_TYPE = "ProgType";
	private final String TITLE = "Title";
	private final String SHOW_CARD_ID = "ShowcardId";
	private final String TMS_ID = "TmsID";
	
	private String banner;
	private String description;
	private String programType;
	private String title;
	private int showCardID;
	private String tmsID;

	public ProgramDetails(JSONObject json) throws JSONException{
		createFromJSON(json);
	}

	@Override
	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();
		json.put(BANNER, banner);
		json.put(DESCRIPTION, description);
		json.put(PROGRAM_TYPE, programType);
		json.put(TITLE, title);
		json.put(SHOW_CARD_ID, showCardID);
		json.put(TMS_ID, tmsID);
		
		return json;
	}

	@Override
	public void createFromJSON(JSONObject json) throws JSONException {
		banner = json.getString(BANNER);
		description = json.getString(DESCRIPTION);
		programType = json.getString(PROGRAM_TYPE);
		title = json.getString(TITLE);
		showCardID = json.getInt(SHOW_CARD_ID);
		tmsID = json.getString(TMS_ID);
	}

	@Override
	protected void debug(String message) {
		Logger.debug(getClass(), message);
	}

	public String getBanner() {
		return banner;
	}

	public String getDescription() {
		return description;
	}

	public String getProgramType() {
		return programType;
	}

	public String getTitle() {
		return title;
	}

	public int getShowCardID() {
		return showCardID;
	}

	public String getTmsID() {
		return tmsID;
	}
}
