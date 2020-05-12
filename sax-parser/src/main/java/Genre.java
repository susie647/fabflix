public class Genre {

	private String name;

	private int id;


	public Genre(){

	}

	public Genre(String name, int id) {
		this.name = name;
		this.id = id;
		
	}
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Id Details - ");
		sb.append("Name:" + getName());
		sb.append(", ");
		sb.append("Id:" + getId());
		sb.append(".");
		
		return sb.toString();
	}
}
