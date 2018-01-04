/*
* @author Sean Lawlor, Stepan Salenikovich, Francois OD
* @date November 6, 2013
* @class ECSE 211 - Design Principle and Methods
* 
* Modified by F.P. Ferrie, February 28, 2014
* new parameters for the Winter 2014 competition
*/
package transmission;

import java.io.*;

public class Transmission {

	private DataOutputStream dos;
	
	// store the output stream to write to the channel in the default constructor
	public Transmission(DataOutputStream dos) {
		this.dos = dos;
	}
	
	// transmit the data specified, and return true if data transmitted successfully, otherwise false which signifies and error
	//public boolean transmit(char role, int startingCorner, int[] greenZoneCoords, int[] redZoneCoords, int[] greenDropCoords, int[] redDropCoords, int[] flags) {
	public boolean transmit(int startingCorner, int[] homeZoneCoords, int[] opponentHomeZoneCoords, int[] dropZoneCoords, int[] flags) {
		try {
			dos.writeInt(startingCorner);
			dos.flush();
			for(int i = 0; i < homeZoneCoords.length; i++) {
				dos.writeChar(',');
				dos.flush();
				dos.writeInt(homeZoneCoords[i]);
				dos.flush();
			}
			for(int i = 0; i < opponentHomeZoneCoords.length; i++) {
				dos.writeChar(',');
				dos.flush();
				dos.writeInt(opponentHomeZoneCoords[i]);
				dos.flush();
			}
			for(int i = 0; i < dropZoneCoords.length; i++) {
				dos.writeChar(',');
				dos.flush();
				dos.writeInt(dropZoneCoords[i]);
				dos.flush();
			}
			for(int i = 0; i < flags.length; i++) {
				dos.writeChar(',');
				dos.flush();
				dos.writeInt(flags[i]);
				dos.flush();
			}
			return true;
		} catch (IOException e) {
			return false;
		}
	}	
	
}
