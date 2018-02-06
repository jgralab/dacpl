/**
 * 
 */
package com.dac.model_repository.port.function;

/**
 * @author Mahdi Derakhshanmanesh
 *
 */
public interface ISensorInfo {
	String getDoorIdForCardReaderId(String sensorId);
	String getProductId(String sensorId);

	String getRoomIdForRoomSensorId(String sensorId);
}
