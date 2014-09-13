/**
 * Copyright (c) 2010-2014, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.socketcan.internal;

import java.util.Collection;

import org.openhab.binding.socketcan.SocketCanBindingProvider;
import org.openhab.core.items.Item;
import org.openhab.core.library.items.DimmerItem;
import org.openhab.model.item.binding.AbstractGenericBindingProvider;
import org.openhab.model.item.binding.BindingConfigParseException;


/**
 * This class is responsible for parsing the binding configuration.
 * 
 * The format of the binding configuration is as following:
 * <pre>
 * [socketcaninterface]:[destinationId]
 * </pre>
 * 
 * @author agriesser
 * @since 0.0.1
 */
public class SocketCanGenericBindingProvider extends AbstractGenericBindingProvider implements SocketCanBindingProvider {

	/**
	 * {@inheritDoc}
	 */
	public String getBindingType() {
		return "socketcan";
	}
	
	/**
	 * @{inheritDoc}
	 */
	@Override
	public void validateItemType(Item item, String bindingConfig) throws BindingConfigParseException {
		if (!(item instanceof DimmerItem)) {
			throw new BindingConfigParseException("item '" + item.getName()
				+ "' is of type '" + item.getClass().getSimpleName()
				+ "', only DimmerItems are allowed - please check your *.items configuration");
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void processBindingConfiguration(String context, Item item, String bindingConfig) throws BindingConfigParseException {
		super.processBindingConfiguration(context, item, bindingConfig);
		SocketCanItemConfig config = new SocketCanItemConfig(item, bindingConfig);
		addBindingConfig(item, config);
		
		// Initialization is done in SocketCanBinding!
	}

	@Override
	public SocketCanItemConfig getItemConfig(String itemName) {
		return (SocketCanItemConfig) bindingConfigs.get(itemName);
	}

	@Override
	public Collection<String> getPolledItems() {
		return bindingConfigs.keySet();
	}

	@Override
	public int getDestinationId(String polledItem) {
		return getItemConfig(polledItem).getDestId();
	}
	
	
}
