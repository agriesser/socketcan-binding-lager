/**
 * Copyright (c) 2010-2014, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.socketcan.internal;

import java.util.Dictionary;

import org.apache.commons.lang.StringUtils;
import org.openhab.binding.socketcan.SocketCanBindingProvider;
import org.openhab.binding.socketcan.internal.SocketConnection.LagerMessageReceivedListener;
import org.openhab.core.binding.AbstractActiveBinding;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
	

/**
 * Implement this class if you are going create an actively polling service
 * like querying a Website/Device.
 * 
 * @author agriesser
 * @since 0.0.1
 */
public class SocketCanBinding extends AbstractActiveBinding<SocketCanBindingProvider> implements ManagedService, LagerMessageReceivedListener {

	private static final Logger logger = 
		LoggerFactory.getLogger(SocketCanBinding.class);

	
	/** 
	 * the refresh interval which is used to poll values from the SocketCan
	 * server (optional, defaults to 60000ms)
	 */
	private long refreshInterval = 60000;
	
	
	public SocketCanBinding() {
	}
		
	
	public void activate() {
	}
	
	public void deactivate() {
		// deallocate resources here that are no longer needed and 
		// should be reset when activating this binding again
	}


	public void bindingChanged(SocketCanBindingProvider provider, String itemName) {
		super.bindingChanged(provider, itemName);
		
		SocketCanItemConfig itemConfig = provider.getItemConfig(itemName);
		initializeItem(itemConfig);
	}
	
	private void initializeItem(SocketCanItemConfig itemConfig) {
		// TODO: here we need to init the current value of the item!
	}


	/**
	 * @{inheritDoc}
	 */
	@Override
	protected long getRefreshInterval() {
		return refreshInterval;
	}

	/**
	 * @{inheritDoc}
	 */
	@Override
	protected String getName() {
		return "SocketCan Refresh Service";
	}
	
	/**
	 * @{inheritDoc}
	 */
	@Override
	protected void execute() {
		// the frequently executed code (polling) goes here ...
		logger.debug("execute() method is called!");
		for (SocketCanBindingProvider provider : providers) {
			for (String polledItem : provider.getPolledItems()) {
				eventPublisher.postUpdate(polledItem, new DecimalType(100));
				int destinationId = provider.getDestinationId(polledItem);
			}
		}
	}

	/**
	 * @{inheritDoc}
	 */
	@Override
	protected void internalReceiveCommand(String itemName, Command command) {
		logger.debug("internalReceiveCommand() is called!");
		for (SocketCanBindingProvider provider : providers) {
			if (provider.providesBindingFor(itemName)) {
				SocketCanItemConfig config = provider.getItemConfig(itemName);
				if (config.supportsCommand(command)) {
					sendCommand(config);
				}
				return;
			}
		}
	}
	
	private void sendCommand(SocketCanItemConfig config) {
		// TODO get the socket and the interface, send the fckn data!
	}


	/**
	 * @{inheritDoc}
	 */
	@Override
	protected void internalReceiveUpdate(String itemName, State newState) {
		logger.debug("internalReceiveCommand() is called!");
	}
		
	/**
	 * @{inheritDoc}
	 */
	@Override
	public void updated(Dictionary<String, ?> config) throws ConfigurationException {
		if (config != null) {
			
			// to override the default refresh interval one has to add a 
			// parameter to openhab.cfg like <bindingName>:refresh=<intervalInMs>
			String refreshIntervalString = (String) config.get("refresh");
			if (StringUtils.isNotBlank(refreshIntervalString)) {
				refreshInterval = Long.parseLong(refreshIntervalString);
			}
			
			// read further config parameters here ...

			setProperlyConfigured(true);
		}
	}

	@Override
	public void messageReceived(int senderId, int receiverId, byte[] data) {	
		// traverse all registered items and then act on those whose senderId matches their receiverId...
		for (SocketCanBindingProvider provider : providers) {
			for (String itemName : provider.getItemNames()) {
				SocketCanItemConfig config = provider.getItemConfig(itemName);
				if (config.destId == senderId) {
					handleMessageFrom(config, data);
				}
			}
		}
	}

	private void handleMessageFrom(SocketCanItemConfig config, byte[] data) {
		State newState = config.transformToNewState(data);
		eventPublisher.postUpdate(config.getItemName(), newState);
	}
	

}
