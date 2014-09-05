/**
 * Copyright (c) 2010-2014, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.socketcan;

import java.util.Collection;

import org.openhab.binding.socketcan.internal.SocketCanItemConfig;
import org.openhab.core.binding.BindingProvider;

/**
 * @author agriesser
 * @since 0.0.1
 */
public interface SocketCanBindingProvider extends BindingProvider {

	public SocketCanItemConfig getItemConfig(String itemName);
	
	public Collection<String> getPolledItems();
	
	public int getDestinationId(String polledItem);
}
