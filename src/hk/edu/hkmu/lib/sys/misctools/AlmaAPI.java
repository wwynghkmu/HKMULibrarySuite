package hk.edu.hkmu.lib.sys.misctools;

public class AlmaAPI {
	protected String json;
	protected String base;
	protected String APIKey;

	public AlmaAPI() {
		this.json = "";
		this.base = "";
		this.APIKey = "";
	}

	public AlmaAPI(String base, String APIKey, String query, String json) {
		this.json = json;
		this.base = base;
		this.APIKey = APIKey;
	}

}
