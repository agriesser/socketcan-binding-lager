package org.openhab.binding.socketcan.internal;


public interface ISocketConnection {

	public void open() throws Exception;
	public void close();
	public void addMessageReceivedListener(LagerMessageReceivedListener listener);
	public void removeMessageReceivedListener(LagerMessageReceivedListener listener);
	public void send(int destinationId, byte[] data);
}
