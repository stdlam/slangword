
public class Slang {
	private String slag;
	private String meaning;
	
	public String getSlag() {
		return slag;
	}
	public void setSlag(String slag) {
		this.slag = slag;
	}
	public String getMeaning() {
		return meaning;
	}
	public void setMeaning(String meaning) {
		this.meaning = meaning;
	}
	
	@Override
	public String toString() {
		return slag + ": " + meaning;
	}
	
	
}
