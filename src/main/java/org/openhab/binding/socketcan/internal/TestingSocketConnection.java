package org.openhab.binding.socketcan.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestingSocketConnection implements ISocketConnection {
	
	private static final Logger logger = LoggerFactory.getLogger(TestingSocketConnection.class);

	private List<LagerMessageReceivedListener> listeners = new ArrayList<>();
	private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
	
	private Map<Integer, Integer> currentValues = new HashMap<>();
	
	private final static long BROADCAST_DELAY = 20; // 20ms delay
	
	@Override
	public void open() throws Exception {
		// nothing happens
	}

	@Override
	public void close() {
		// nothing happens ;)
	}

	@Override
	public void addMessageReceivedListener(LagerMessageReceivedListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeMessageReceivedListener(
			LagerMessageReceivedListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void send(int destinationId, byte[] data) {
		// in data[0] we always find the operation and output id for the operation.
		int opId = data[0] & LagerProtocol.OP_MASK;
		int outputId = data[0] & LagerProtocol.OUTPUT_MASK;
		
		int currentValue = getValue(destinationId, outputId);
		// boundary checks.
		if (opId == LagerProtocol.OP_INCR) {
			if (currentValue < 255) {
				currentValue++;
			}
		} else if (opId == LagerProtocol.OP_DECR) {
			if (currentValue > 0) {
				currentValue--;
			}
		} else if (opId == LagerProtocol.OP_SET_VALUE) {
			currentValue = data[1];
			if (currentValue < 0) {
				currentValue += 256;
			}
		} else if (opId == LagerProtocol.OP_GET_VALUE) {
			// nothing to do :)
		} else {
			logger.debug("illegal operation!");
			return;
		}
		
		setValue(destinationId, outputId, currentValue);
		sendFakedBroadcastAnswer(destinationId, outputId, currentValue);
	}
	
	private void sendFakedBroadcastAnswer(final int destinationId, final int outputId, final int currentValue) {
		executor.schedule(new Runnable() {
			
			@Override
			public void run() {
				byte[] data = new byte[2];
				// in byte[0] we have only the outputid
				data[0] = (byte) outputId;
				data[1] = (byte) ((currentValue > 127) ? currentValue - 256 : currentValue);
				
				for (LagerMessageReceivedListener list : listeners) {
					list.messageReceived(destinationId, LagerProtocol.BROADCAST_ID, data);
				}
			}
		}, BROADCAST_DELAY, TimeUnit.MILLISECONDS);
	}
	
	private int getValue(int destinationId, int outputId) {
		int combined = destinationId * 100 + outputId;
		Integer currentValue = currentValues.get(combined);
		if (currentValue == null) {
			currentValue = 255;
			currentValues.put(Integer.valueOf(combined), currentValue);
		}
		return currentValue;
	}
	
	private void setValue(int destinationId, int outputId, int newValue) {
		int combined = destinationId * 100 + outputId;
		currentValues.put(combined, newValue);
	}

}
