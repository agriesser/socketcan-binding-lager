package org.openhab.binding.socketcan.internal;

public class Util {

	public static String formatData(byte[] data) {
		if (data == null) {
			return "data == null";
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			sb.append("data[");
			sb.append(i);
			sb.append("] = ");
			sb.append(data[i]);
			sb.append("; as byte = ");
			int v = (data[i] < 0) ? data[i] + 256 : data[i];
			sb.append(Integer.toBinaryString(v));
			sb.append('\n');
		}
		
		return sb.toString();
	}
}
