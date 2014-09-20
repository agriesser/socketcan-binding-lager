package org.openhab.binding.socketcan.internal;

public class ConnectionFactory {
	
	private static boolean testMode = true; 

	public static ISocketConnection createConnection(String interfaceId) {
		if (testMode) {
			return new TestingSocketConnection();
		} else {
			return new SocketConnection(interfaceId);
		}
	}
	
}
