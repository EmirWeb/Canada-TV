package canada.tv.utilities;

public class Statics {
	public final String APPLICATION_NAME = "CanadaTV";
	public final String GUIDE = "Guide";
	public final String PROGRAM = "program";
	public final String CHANNEL = "CHANNEL";
	public final String NA = "N/A";

	private static Statics instance;

	private Statics() {
	};

	public static Statics getInstance() {
		if (instance == null)
			instance = new Statics();
		return instance;
	}

	public static Statics s() {
		return getInstance();
	}
}
