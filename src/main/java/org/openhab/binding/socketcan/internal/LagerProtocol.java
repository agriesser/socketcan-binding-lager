package org.openhab.binding.socketcan.internal;

import org.openhab.core.library.types.IncreaseDecreaseType;
import org.openhab.core.types.Command;

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
	
	public static boolean isBroadcastId(int senderOrDestination) {
		return senderOrDestination == 0b11111;
	}
	
	public static byte[] commandToCanData(Command cmd, SocketCanItemConfig config) {
		if (cmd instanceof IncreaseDecreaseType) {
			byte[] returnedArray = new byte[1];
			if (cmd.equals(IncreaseDecreaseType.INCREASE)) {
				returnedArray[0] = 0b01000000;
			} else {
				returnedArray[0] = 0b01100000;
			}
			byte outputWithMask = (byte) (config.getOutputId() & 0b11111);
			returnedArray[0] = (byte) (returnedArray[0] | outputWithMask);
		}
		return null;
		
	}
}
