package canada.tv.model;

import java.util.Calendar;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;

import canada.tv.utilities.JSONizable;
import canada.tv.utilities.Logger;

public class Program extends JSONizable {

	private final String DURATION = "GridDuration";
	private final String START_TIME = "StartTime";
	private final String END_TIME = "EndTime";
	private final String TMS_ID = "TmsID";
	private final String TYPE = "ProgType";
	private final String TITLE = "Title";
	private final String QUALS = "Quals";
	private final String EPISODE_TITLE = "EpisodeTitle";
	private final String PROGRAM_DETAILS = "ProgramDetails";

	private int duration;
	private String startTime;
	private String endTime;
	private String tmsID;
	private String type;
	private String title;
	private String quals;
	private String episodeTitle;
	private ProgramDetails programDetails;

	public Program() {
	}

	public Program(JSONObject json) throws JSONException {
		createFromJSON(json);
	}

	@Override
	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();
		json.put(DURATION, duration);
		json.put(START_TIME, startTime);
		json.put(END_TIME, endTime);
		json.put(TMS_ID, tmsID);
		json.put(TYPE, type);
		json.put(TITLE, title);
		json.put(QUALS, quals);
		json.put(EPISODE_TITLE, episodeTitle);
		if (programDetails != null)
			json.put(PROGRAM_DETAILS, programDetails.toJSON());

		return json;
	}

	@Override
	public void createFromJSON(JSONObject json) throws JSONException {
		duration = json.getInt(DURATION);
		startTime = json.getString(START_TIME);
		endTime = json.getString(END_TIME);
		tmsID = json.getString(TMS_ID);
		type = json.getString(TYPE);
		title = json.getString(TITLE);
		quals = json.getString(QUALS);
		episodeTitle = json.getString(EPISODE_TITLE);

		if (!json.isNull(PROGRAM_DETAILS)) {
			JSONObject programDetailsJson = json.getJSONObject((PROGRAM_DETAILS));
			programDetails = new ProgramDetails(programDetailsJson);
		}
	}

	public int getDuration() {
		return duration;
	}

	public String getStartTime() {
		return startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public String getTmsID() {
		return tmsID;
	}

	public String getType() {
		return type;
	}

	public String getTitle() {
		return title;
	}

	public String getQuals() {
		return quals;
	}

	public String getEpisodeTitle() {
		return episodeTitle;
	}

	@Override
	protected void debug(String message) {
		Logger.debug(getClass(), message);
	}

	public String getUserTimes() {
		Calendar c1 = getCalendar(startTime);
		Calendar c2 = getCalendar(endTime);

		return calendarToString(c1) + " - " + calendarToString(c2);
	}

	private String calendarToString(Calendar calendar) {
		String hour = Integer.toString(calendar.get(Calendar.HOUR_OF_DAY));
		if (hour.length() == 1)
			hour = "0" + hour;

		String minute = Integer.toString(calendar.get(Calendar.MINUTE));
		if (minute.length() == 1)
			minute = "0" + minute;
		return hour + ":" + minute;
	}

	private Calendar getCalendar(String stringTime) {
		int bracket = stringTime.indexOf('(');
		String tempTime = stringTime.substring(bracket + 1);

		bracket = tempTime.indexOf(')');
		tempTime = tempTime.substring(0, bracket);

		String[] timeInfo = tempTime.split("\\+");
		long time = Long.parseLong(timeInfo[0]);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		calendar.setTimeZone(TimeZone.getTimeZone("GMT+" + timeInfo[1].substring(0, 2) + ":" + timeInfo[1].substring(2, 4)));
		return calendar;
	}

	public void setProgramDetails(ProgramDetails programDetails) {
		this.programDetails = programDetails;
	}

	public ProgramDetails getProgramDetails() {
		return programDetails;
	}

	public boolean hasDetails() {
		return programDetails != null;
	}

}
