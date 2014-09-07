package org.openhab.binding.socketcan.internal;

import org.openhab.core.binding.BindingConfig;
import org.openhab.core.items.Item;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;
import org.openhab.model.item.binding.BindingConfigParseException;

public class SocketCanItemConfig implements BindingConfig {
	
	int destId;
	String canInterfaceId;
	String itemName;

	public int getDestId() {
		return destId;
	}

	public void setDestId(int destId) {
		this.destId = destId;
	}

	public String getCanInterfaceId() {
		return canInterfaceId;
	}

	public void setCanInterfaceId(String canInterfaceId) {
		this.canInterfaceId = canInterfaceId;
	}
	
	public String getItemName() {
		return itemName;
	}
	
	public boolean supportsCommand(Command cmd) {
		return true; // TODO implement
	}

	public SocketCanItemConfig(Item item, String bindingConfig) throws BindingConfigParseException {
		// TODO Auto-generated constructor stub
	}

	public State transformToNewState(byte[] data) {
		// TODO implement
		return null;
	}

}
