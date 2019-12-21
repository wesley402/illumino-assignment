
public enum Direction {
	inbound("inbound"), outbound("outbound");

	private String name;

	Direction(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
