package canada.tv.utilities;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class JSONizable implements Serializable {

	public abstract JSONObject toJSON() throws JSONException;

	public abstract void createFromJSON(JSONObject json) throws JSONException;
	
	protected abstract void debug(String message);
	
	public String toString(){
		try {
			return toJSON().toString();
		} catch (JSONException e) {
			debug(e.getMessage());
		}
		return null;
	}	
	
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();

		try {
			String saveObject = toJSON().toString();
			out.writeObject(saveObject);
		} catch (JSONException e) {
			debug(e.getMessage());
		}
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		try {
			String json = (String) in.readObject();
			createFromJSON(new JSONObject(json));
		} catch (OptionalDataException e) {
			debug(e.getMessage());
		} catch (ClassNotFoundException e) {
			debug(e.getMessage());
		} catch (IOException e) {
			debug(e.getMessage());
		} catch (JSONException e) {
			debug(e.getMessage());
		}
	}

}
