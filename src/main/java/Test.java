import org.openhab.binding.socketcan.internal.LagerProtocol;


public class Test {
	
	public static void main(String[] args) {
		short id = 0b00001;
		short targetId = 0b11000;
		byte headerPart1 = (byte) ((id & 0xFF) << 3);
		System.out.println(headerPart1);
		headerPart1 |= (byte) ((targetId & 0xFF) >> 2);
		System.out.println(headerPart1);
		byte headerPart2 = (byte) ((targetId & 0b00111) << 5);
		System.out.println(headerPart2);
		headerPart2 &= 0b01110000;
		System.out.println(headerPart2);
		
		// 1 bit bleibt frei
		int canId = LagerProtocol.constructCanId(0b1, 0b10);
		int senderId = LagerProtocol.getSenderId(canId);
		int destId = LagerProtocol.getDestinationId(canId);
		System.out.println(canId);
		System.out.println(senderId);
		System.out.println(destId);
		
		
		
		byte b = -1;
		int i = b;
		if (i < 0) {
			i = 256 + b;
		}
		System.out.println(i);
	}
	
}
