
public enum Protocol {
	udp("udp"), tcp("tcp");

	String name;

	Protocol(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
