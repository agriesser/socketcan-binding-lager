package org.openhab.binding.socketcan.internal;

public interface LagerMessageReceivedListener {

	public void messageReceived(int senderId, int receiverId, byte[] data);
	
}