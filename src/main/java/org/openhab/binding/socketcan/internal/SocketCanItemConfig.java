package org.openhab.binding.socketcan.internal;

import org.openhab.core.binding.BindingConfig;
import org.openhab.core.items.Item;

public class SocketCanItemConfig implements BindingConfig {
	
	int destId;
	String canInterfaceId;

	public SocketCanItemConfig(Item item, String bindingConfig) {
		// TODO Auto-generated constructor stub
	}

}
