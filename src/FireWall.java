import java.io.*;
import java.util.*;

public class FireWall {
	private long ADDR_SHIFT = 100000L; // port occupies five digits
	private Map<Direction, Map<Protocol, Set<Long>>> validDirections;
	
	public static void main(String[] args) {
		FireWall fw = new FireWall("");
    System.out.println("*** test for encoding address ***");
    System.out.println(fw.encodeAddress("0.0.0.0") == 0L); // true
    System.out.println(fw.encodeAddress("0.0.0.255") == 255L); // true
    System.out.println(fw.encodeAddress("0.0.1.0") == 256L); // true
    System.out.println(fw.encodeAddress("1.1.1.1") == 16843009L); // true
    
    // test for inbound,tcp,80,192.168.1.2
    System.out.println("*** test for inbound,tcp,80,192.168.1.2 ***");
    System.out.println(fw.acceptPacket("inbound", "tcp", 80, "192.168.1.2")); // true
    System.out.println(fw.acceptPacket("outbound", "tcp", 80, "192.168.1.2")); // false
    System.out.println(fw.acceptPacket("inbound", "udp", 80, "192.168.1.2")); // false

    // test for inbound,udp,53,192.168.1.1-192.168.2.5
    System.out.println("*** test for inbound,udp,53,192.168.1.1-192.168.2.5 ***");
    System.out.println(fw.acceptPacket("inbound", "udp", 53, "192.168.1.1")); // true
    System.out.println(fw.acceptPacket("inbound", "udp", 53, "192.168.2.1")); // true
    System.out.println(fw.acceptPacket("inbound", "udp", 53, "192.168.2.5")); // true
    System.out.println(fw.acceptPacket("inbound", "udp", 53, "192.168.2.6")); // false

    // test for outbound,tcp,10000-20000,192.168.10.11
    System.out.println("*** test for outbound,tcp,10000-20000,192.168.10.11 ***");
    System.out.println(fw.acceptPacket("outbound", "tcp", 10234, "192.168.10.11")); // true
    System.out.println(fw.acceptPacket("outbound", "tcp", 10000, "192.168.10.11")); // true
    System.out.println(fw.acceptPacket("outbound", "tcp", 20000, "192.168.10.11")); // true
    System.out.println(fw.acceptPacket("outbound", "tcp", 20001, "192.168.10.11")); // false
    
    // test for outbound,udp,1000-2000,52.12.48.92
    System.out.println("*** test for outbound,udp,1000-2000,52.12.48.92 ***");
    System.out.println(fw.acceptPacket("outbound", "udp", 1000, "52.12.48.92")); // true
    System.out.println(fw.acceptPacket("outbound", "udp", 2001, "52.12.48.92")); // false
    System.out.println(fw.acceptPacket("inbound", "udp", 24, "52.12.48.92")); // false 
	}
	
	public FireWall(String pathToFile) {
		this.validDirections = new HashMap<>();
    String csvFile = "/fw.csv";
		parseFile(csvFile);
	}
	
	public void parseFile(String pathToFile) {
    String line = "";
    File file = new File(getClass().getResource(pathToFile).getFile());
    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
        while ((line = br.readLine()) != null) {
        	parseInput(line.trim());
        }
    } catch (IOException e) {
        e.printStackTrace();
    }				
	}
	
	public void parseInput(String input) {
    String splitter = ",";
		String[] arr = input.split(splitter);
		String port = arr[2], address = arr[3];
  	Direction direction = Direction.valueOf(arr[0]);
  	Protocol portocol = Protocol.valueOf(arr[1]);
  	validDirections.putIfAbsent(direction, new HashMap<>());
  	Map<Protocol, Set<Long>> validProtocols = validDirections.get(direction);
  	validProtocols.putIfAbsent(portocol, new HashSet<>());
  	Set<Long> validAddrPort = validProtocols.get(portocol);
  	
		long addrFrom = -1, addrTo = -1;
		int portFrom = -1, portTo = -1; 
		
		// deal with range addresses and specific address
		if(address.contains("-")) {
			addrFrom = encodeAddress(address.split("-")[0]);
			addrTo = encodeAddress(address.split("-")[1]);
		} else {
			addrFrom = encodeAddress(address);
			addrTo = addrFrom;
		}
		// deal with range ports and specific port
		if(port.contains("-")) {
			portFrom = Integer.valueOf(port.split("-")[0]);
			portTo = Integer.valueOf(port.split("-")[1]);
		} else {
			portFrom = Integer.valueOf(port);
			portTo = portFrom;
		}		
		
		// put into validArrPort set
		// addr:      0.0.0.1         -> 1
		// port:      6000            -> 6000
		// encoded:   1 *10000 + 6000 -> 16000
		for(long i = addrFrom; i <= addrTo; i++) {
			for(int j = portFrom; j <= portTo; j++) {
				Long encodedValue = i * ADDR_SHIFT + Long.valueOf(j); 
				validAddrPort.add(encodedValue);
			}
		}
	}
	
	public boolean acceptPacket(String direction, String protocol, int port, String address) {
		Direction dir = Direction.valueOf(direction);
		Protocol proto = Protocol.valueOf(protocol);
		Long encodedValue = encodeAddress(address) * ADDR_SHIFT + Long.valueOf(port);
		
		if(!validDirections.containsKey(dir)) {
			return false;
		}
		Map<Protocol, Set<Long>> validProtocols = validDirections.get(dir);
		if(!validProtocols.containsKey(proto)) {
			return false;
		}
		
		Set<Long> validAddrPort = validProtocols.get(proto);
		return validAddrPort.contains(encodedValue);
	}
	
	public Map<Direction, Map<Protocol, Set<Long>>> getValidPackets() {
		return this.validDirections;
	}
	
	/* 0.0.0.0 -> 0
	 * 0.0.0.0 -> 255
	 * 0.0.1.1 -> 256 * 1 + 1  = 257
	 * */
	private Long encodeAddress(String address) {
		Long res = 0L;
		Long factor = 1L;
		String[] arr = address.split("\\.");
		for(int i = arr.length - 1; i >= 0; i--) {
			res += (Long.valueOf(arr[i]) * factor);
			factor *= 256L;
		}
		return res;
	}		
	
}
