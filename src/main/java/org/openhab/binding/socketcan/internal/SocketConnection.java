package org.openhab.binding.socketcan.internal;

import java.io.IOException;

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
	
	private LagerMessageReceivedListener listener;
		
	public SocketConnection(String canInterfaceName) {
		this.canInterfaceName = canInterfaceName;
	}
	
	public void setMessageReceivedListener(LagerMessageReceivedListener listener) {
		this.listener = listener;
	}
	
	public void open() throws Exception {
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
	
	public void close() {
		if (canSocket != null) {
			readingThread.doStop();
			try {
				canSocket.close();
			} catch (IOException e) {
				logger.error("Failed to close cansocket!", e);
			}
		}
	}
	
	public void send(short destinationId, byte[] data) {
		CanId canId = new CanId(LagerProtocol.constructCanId(sourceId, destinationId)); 
		try {
			canSocket.send(new CanFrame(canIf, canId, data));
		} catch (IOException e) {
			logger.error("Failed so send CanFrame: ", e);
		}
	}
	
	private interface LagerMessageReceivedListener {

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
					if (listener != null) {
						try {
							listener.messageReceived(source, destination, frame.getData());
						} catch (Throwable t) {
							logger.error("Error in the listener for can frames!", t);
						}
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
}
