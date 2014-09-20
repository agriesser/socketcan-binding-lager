package org.openhab.binding.socketcan.internal;

import org.openhab.core.library.types.IncreaseDecreaseType;
import org.openhab.core.library.types.PercentType;
import org.openhab.core.types.Command;

public class LagerProtocol {

	public static final int OP_GET_VALUE = 0b000;
	public static final int OP_SET_VALUE = 0b00100000;
	public static final int BROADCAST_ID = 0b11111;
	public static final int OP_MASK = 0b11100000;
	public static final int OUTPUT_MASK = 0b11111;
	public static final int OP_DECR = 0b01100000;
	public static final int OP_INCR = 0b01000000;

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
		return senderOrDestination == BROADCAST_ID;
	}
	
	public static byte[] commandToCanData(Command cmd, SocketCanItemConfig config) {
		byte[] cmdData = null;
		if (cmd instanceof IncreaseDecreaseType) {
			cmdData = createIncrDecrData(cmd);
		} else if (cmd instanceof PercentType) {
			cmdData = createSetValueData(cmd);
		}
		addOutputInfo(cmdData, config);
		return cmdData;
	}
	
	public static byte[] getValue(SocketCanItemConfig config) {
		byte[] cmdData = new byte[1];
		cmdData[0] = OP_GET_VALUE;
		addOutputInfo(cmdData, config);
		return cmdData;
	}
	
	public static void addOutputInfo(byte[] inoutCmdData, SocketCanItemConfig config) {
		if (inoutCmdData != null) {
			byte outputWithMask = (byte) (config.getOutputId() & OUTPUT_MASK);
			inoutCmdData[0] = (byte) (inoutCmdData[0] | outputWithMask);
		}
	}

	private static byte[] createIncrDecrData(Command cmd) {
		byte[] returnedArray = new byte[1];
		if (cmd.equals(IncreaseDecreaseType.INCREASE)) {
			returnedArray[0] = OP_INCR;
		} else {
			returnedArray[0] = OP_DECR;
		}
		return returnedArray;
	}
	
	private static byte[] createSetValueData(Command cmd) {
		byte[] returnedArray = new byte[2];
		returnedArray[0] = OP_SET_VALUE;
		PercentType pt = (PercentType) cmd;
		long val = Math.round(pt.doubleValue() / 100 * 255);
		if (val > 127) {
			val -= 256;
		}
		returnedArray[1] = (byte) val;
		return returnedArray;
	}
	
	
}
