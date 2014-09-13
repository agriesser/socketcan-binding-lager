package org.openhab.binding.socketcan.internal;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.entropia.can.CanSocket;
import de.entropia.can.CanSocket.CanFrame;
import de.entropia.can.CanSocket.CanId;
import de.entropia.can.CanSocket.CanInterface;
import de.entropia.can.CanSocket.Mode;

/**
 * 
 * @author alexander
 */
public class SocketConnection {
	
	private static final Logger logger = 
			LoggerFactory.getLogger(SocketConnection.class);

	private String canInterfaceName;
	
	private CanSocket canSocket;
	private CanInterface canIf;
	
	// TODO: make configurable!
	private short sourceId = 0b00001;
	
	private ReaderThread readingThread;
	
	private Set<LagerMessageReceivedListener> listeners = new HashSet<>();
		
	public SocketConnection(String canInterfaceName) {
		this.canInterfaceName = canInterfaceName;
	}
	
	public void addMessageReceivedListener(LagerMessageReceivedListener listener) {
		this.listeners.add(listener);
	}
	
	public void removeMessageReceivedListener(LagerMessageReceivedListener listener) {
		this.listeners.remove(listener);
	}
	
	public void open() throws Exception {
		if (canSocket != null) {
			try {
				canSocket = new CanSocket(Mode.RAW);
				canIf = new CanInterface(canSocket, canInterfaceName);
				canSocket.bind(canIf);
				readingThread = new ReaderThread();
				readingThread.start();
			} catch (Exception e) {
				logger.error("Couldn't open connection to CAN interface!", e);
				throw e;
			}
		}
	}
	
	public void close() {
		if (canSocket != null) {
			readingThread.doStop();
			try {
				canSocket.close();
			} catch (IOException e) {
				logger.error("Failed to close cansocket!", e);
			} finally {
				canSocket = null;
			}
		}
	}
	
	public void send(int destinationId, byte[] data) {
		CanId canId = new CanId(LagerProtocol.constructCanId(sourceId, destinationId)); 
		try {
			canSocket.send(new CanFrame(canIf, canId, data));
		} catch (IOException e) {
			logger.error("Failed so send CanFrame: ", e);
		}
	}
	
	public interface LagerMessageReceivedListener {

		public void messageReceived(int senderId, int receiverId, byte[] data);
		
	}
	
	private class ReaderThread extends Thread {
		
		private boolean run = true;
		
		public ReaderThread() {
			super("SocketCan reading thread");
		}
		
		public void run() {
			while (run) {
				if (canSocket == null) {
					return;
				}
				try {
					CanFrame frame = canSocket.recv();
					boolean isErrorFrame =  frame.getCanId().isSetERR();
					if (isErrorFrame) {
						logger.info("Received an error frame!");
						continue;
					}
					int canID = frame.getCanId().getCanId_SFF();
					int source = LagerProtocol.getSenderId(canID);
					int destination = LagerProtocol.getDestinationId(canID);
					if (destination != sourceId && !LagerProtocol.isBroadcastId(destination)) {
						logger.info("Received frame not for me!");
						continue;
					}
					if (listeners.size() > 0) {
						notifyListeners(source, destination, frame.getData());
					}
				} catch (IOException e) {
					logger.error("Error receiving packet", e);
				} 
			}
		}
		
		public void doStop() {
			run = false;
			interrupt();
		}
	}
	
	protected void notifyListeners(int source, int destination, byte[] data) {
		for (LagerMessageReceivedListener listener : listeners) {
			try {
				listener.messageReceived(source, destination, data);
			} catch (Throwable t) {
				logger.error("Error in the listener for can frames!", t);
			}
		}
	}
}
