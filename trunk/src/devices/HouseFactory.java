/* =============================================================================
 * CS544 - Computer Networks
 * Drexel University, Spring 2013
 * Protocol Implementation: Remote Smart House Control
 * Group 12:
 * - Ryan Corcoran
 * - Amber Heilman
 * - Michael Mersic
 * - Ariel Stolerman
 * 
 * -----------------------------------------------------------------------------
 * File name: HouseFactory.java
 * 
 * Purpose:
 * Interface for house instance generator.
 * 
 * Relevant requirements:
 * - SERVICE - house generation and representation are part of the functionality
 *   required by the protocol implementation.
 * 
 * =============================================================================
 */

package devices;

public interface HouseFactory {
	
	/**
	 * @return a generated house object.
	 */
	public House createHouse();
}
