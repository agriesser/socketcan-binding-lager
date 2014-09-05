package org.openhab.binding.socketcan.internal;

public class LagerProtocol {

	public static int constructCanId(int senderId, int destinationId) {
		int canId = 0;
		canId |= ((senderId & 0b11111) << 6);
		canId |= ((destinationId & 0b11111) << 1);
		return canId;
	}
	
	public static int getSenderId(int canId) {
		return (canId & 0b11111000000) >> 6;
	}
	
	public static int getDestinationId(int canId) {
		return (canId & 0b111110) >> 1;
	}
}
