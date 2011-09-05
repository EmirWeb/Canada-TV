package canada.tv.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import canada.tv.utilities.JSONizable;
import canada.tv.utilities.Logger;

public class Guide extends JSONizable {
	private final String PROVIDER_ID = "HeadendID";
	private final String PROVIDER = "HeadendName";
	private final String CHANNELS = "Channels";
	private final String START_TIME = "StartTime";
	private final String END_TIME = "EndTime";
	private final String POSTAL_CODE = "PostalCode";
	
	private String postalCode;
	private String providerID;
	private String provider;
	private List<Channel> channels;
	private String startTime;
	private String endTime;
	
	public Guide() {
	}

	public Guide(JSONObject json, String postalCode) throws JSONException {
		this.postalCode = postalCode;
		createFromJSON(json);
	}
	
	public Guide(JSONObject json) throws JSONException {
		createFromJSON(json);
	}

	@Override
	public JSONObject toJSON() throws JSONException {
		JSONArray jsonChannels = new JSONArray();
		
		for (Channel channel: channels)
			jsonChannels.put(channel.toJSON());
		
		JSONObject json = new JSONObject();
		json.put(CHANNELS, jsonChannels);
		json.put(PROVIDER, provider);
		json.put(PROVIDER_ID, providerID);
		json.put(START_TIME, startTime);
		json.put(END_TIME, endTime);
		json.put(POSTAL_CODE, postalCode);
		
		return json;
	}

	@Override
	public void createFromJSON(JSONObject json) throws JSONException {
		JSONArray jsonChannels = json.getJSONArray(CHANNELS);
		channels = new ArrayList<Channel>();
		
		if (!json.isNull(POSTAL_CODE))
			postalCode = json.getString(POSTAL_CODE);
		
		for (int i = 0; i < jsonChannels.length(); i++) {
			Channel channel = new Channel(jsonChannels.getJSONObject(i), i, postalCode);
			channels.add(channel);
		}
		
		startTime = json.getString(START_TIME);
		endTime = json.getString(END_TIME);
		provider = json.getString(PROVIDER);
		providerID = json.getString(PROVIDER_ID);
		
	}

	@Override
	protected void debug(String message) {
		Logger.debug(getClass(), message);
	}
	

	public String getProviderID() {
		return providerID;
	}

	public String getProvider() {
		return provider;
	}

	public List<Channel> getChannels() {
		return channels;
	}
}