package org.openhab.binding.socketcan.internal;

import java.math.BigDecimal;

import org.openhab.core.binding.BindingConfig;
import org.openhab.core.items.Item;
import org.openhab.core.library.types.IncreaseDecreaseType;
import org.openhab.core.library.types.PercentType;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;
import org.openhab.model.item.binding.BindingConfigParseException;

public class SocketCanItemConfig implements BindingConfig {
	
	private int destId;
	private int outputId;
	private String canInterfaceId;
	private String itemName;
	
	final static double CONVERSION_TO_PERCENT = 100 / 255.0;
	
	public SocketCanItemConfig(Item item, String bindingConfig) throws BindingConfigParseException {
		itemName = item.getName();
		
		String[] parts = bindingConfig.split(":");
		if (parts.length != 3) {
			throw new BindingConfigParseException("Error parsing binding configuration.");
		}
		
		// TODO validate canInterfaceId in some sensible way...
		canInterfaceId = parts[0];
		try {
			destId = Integer.parseInt(parts[1]);
			outputId = Integer.parseInt(parts[2]);
		} catch (NumberFormatException nfe) {
			throw new BindingConfigParseException("Error parsing binding configuration. Couldn't parse destinationId");
		}
	}

	public int getDestId() {
		return destId;
	}
	
	public int getOutputId() {
		return outputId;
	}

	public String getCanInterfaceId() {
		return canInterfaceId;
	}
	
	public String getItemName() {
		return itemName;
	}
	
	public boolean supportsCommand(Command cmd) {
		return cmd instanceof IncreaseDecreaseType;
	}

	public State transformToNewState(byte[] data) {
		if (data != null && data.length == 2) {
			int newValue = data[1];
			// if the value of newValue is negative add 256 in order to ignore the sign...
			if (newValue < 0) {
				newValue = 256 + data[1];
			}
			// we are a dimmer, so we need to normalize the value to 100% ;)
			double d = newValue;
			double percentage = Math.ceil(CONVERSION_TO_PERCENT * d);
			return new PercentType(new BigDecimal(percentage));
		}
		return null;
	}

	public boolean isSameOutputId(byte b) {
		int outputIdFromByte = b & 0b00011111;
		return outputId == outputIdFromByte;
	}

}
