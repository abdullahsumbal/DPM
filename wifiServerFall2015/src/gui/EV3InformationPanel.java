//  Written by Sean Lawlor, 2011
//  Modified by F.P. Ferrie, February 28, 2014
//
package gui;

import java.awt.*;
import java.awt.event.*;
import transmission.*;
import universal.Universal;
import gui.defaults.*;

@SuppressWarnings("serial")
public class EV3InformationPanel extends DPMPanel implements ActionListener {
	private ServerEV3 server;
	private Button start, stop, clear;
	private TextField gTeamNumber, gStartCorner;
	private TextField rTeamNumber, rStartCorner;
	private TextField greenZone1X, greenZone1Y, greenZone2X, greenZone2Y;
	private TextField redZone1X, redZone1Y, redZone2X, redZone2Y;
	private TextField redDZoneX, redDZoneY;
	private TextField greenDZoneX, greenDZoneY;
	private TextField greenFlag;
	private TextField redFlag;
	private SendSelectPanel sendSelectPanel;

	public EV3InformationPanel(MainWindow mw) {
		// initalize information panel
		super(mw);
		this.layoutPanel(mw);
		server = new ServerEV3(mw);
	}

	private void layoutPanel(MainWindow mw) {
		GridBagConstraints c = new GridBagConstraints();
		this.setFont(new Font("Helvetica", Font.PLAIN, 14));
		this.setLayout(new GridBagLayout());

		// send select check box
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.gridheight = 1;
		c.ipady = 25;
		sendSelectPanel = new SendSelectPanel(mw);
		this.add(sendSelectPanel, c);

		// GREEN PLAYER TEAM NUMBER
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 1;
		c.ipady = 5;
		Label greenTeamNumberLabel = new Label("Green Player Team Number: ", Label.RIGHT);
		this.add(greenTeamNumberLabel, c);

		c.gridx = 1;
		c.gridy = 2;
		c.gridwidth = 2;
		gTeamNumber = new TextField(11);
		new DPMToolTip("Enter the green team's number here", gTeamNumber);
		this.add(gTeamNumber, c);
		
		// GREEN PLAYER CORNER
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 1;
		Label greenStartCornerLabel = new Label("Green Player Start Corner: ",
				Label.RIGHT);
		this.add(greenStartCornerLabel, c);

		c.gridx = 1;
		c.gridy = 3;
		c.gridwidth = 2;
		gStartCorner = new TextField(11);
		new DPMToolTip("Enter the EV3's starting corner (1-4)", gStartCorner);
		this.add(gStartCorner, c);

		
		// RED PLAYER TEAM NUMBER
		c.gridx = 0;
		c.gridy = 4;
		c.gridwidth = 1;
		Label redTeamNumberLabel = new Label("Red Player Team Number: ", Label.RIGHT);
		this.add(redTeamNumberLabel, c);

		c.gridx = 1;
		c.gridy = 4;
		c.gridwidth = 2;
		rTeamNumber = new TextField(11);
		new DPMToolTip("Enter the EV3's Bluetooth name here", rTeamNumber);
		this.add(rTeamNumber, c);
		
		
		// RED PLAYER CORNER
		c.gridx = 0;
		c.gridy = 5;
		c.gridwidth = 1;
		Label redStartCornerLabel = new Label("Red Player Start Corner: ",
				Label.RIGHT);
		this.add(redStartCornerLabel, c);

		c.gridx = 1;
		c.gridy = 5;
		c.gridwidth = 2;
		rStartCorner = new TextField(11);
		new DPMToolTip("Enter the EV3's starting corner (1-4)", rStartCorner);
		this.add(rStartCorner, c);

		
		// GREEN ZONE BOTTOM-LEFT CORNER
		c.gridx = 0;
		c.gridy = 6;
		c.gridwidth = 1;
		Label greenZone1Label = new Label("Green Zone bottom-left corner (x, y): ", Label.RIGHT);
		this.add(greenZone1Label, c);

		c.gridx = 1;
		c.gridy = 6;
		c.gridwidth = 1;
		greenZone1X = new TextField(4);
		new DPMToolTip("Enter X location of the Green Zone bottom-left corner here", greenZone1X);
		this.add(greenZone1X, c);
		
		c.gridx = 2;
		c.gridy = 6;
		c.gridwidth = 1;
		greenZone1Y = new TextField(4);
		new DPMToolTip("Enter Y location of the Green Zone bottom-left corner here", greenZone1Y);
		this.add(greenZone1Y, c);
		
		
		// GREEN ZONE TOP-RIGHT CORNER
		c.gridx = 0;
		c.gridy = 7;
		c.gridwidth = 1;
		Label greenZone2Label = new Label("Green Zone top-right corner (x, y): ", Label.RIGHT);
		this.add(greenZone2Label, c);

		c.gridx = 1;
		c.gridy = 7;
		c.gridwidth = 1;
		greenZone2X = new TextField(4);
		new DPMToolTip("Enter X location of the Green Zone top-right corner here", greenZone2X);
		this.add(greenZone2X, c);
		
		c.gridx = 2;
		c.gridy = 7;
		c.gridwidth = 1;
		greenZone2Y = new TextField(4);
		new DPMToolTip("Enter Y location of the Green Zone top-right corner here", greenZone2Y);
		this.add(greenZone2Y, c);
		
		
		// RED ZONE BOTTOM-LEFT CORNER
		c.gridx = 0;
		c.gridy = 8;
		c.gridwidth = 1;
		Label redZone1Label = new Label("Red Zone bottom-left corner (x, y): ", Label.RIGHT);
		this.add(redZone1Label, c);

		c.gridx = 1;
		c.gridy = 8;
		c.gridwidth = 1;
		redZone1X = new TextField(4);
		new DPMToolTip("Enter X location of the Red Zone bottom-left corner here", redZone1X);
		this.add(redZone1X, c);
		
		c.gridx = 2;
		c.gridy = 8;
		c.gridwidth = 1;
		redZone1Y = new TextField(4);
		new DPMToolTip("Enter Y location of the Red Zone bottom-left corner here", redZone1Y);
		this.add(redZone1Y, c);
		
		
		// RED ZONE TOP-RIGHT CORNER
		c.gridx = 0;
		c.gridy = 9;
		c.gridwidth = 1;
		Label redZone2Label = new Label("Red Zone top-right corner (x, y): ", Label.RIGHT);
		this.add(redZone2Label, c);

		c.gridx = 1;
		c.gridy = 9;
		c.gridwidth = 1;
		redZone2X = new TextField(4);
		new DPMToolTip("Enter X location of the Red Zone top-right corner here", redZone2X);
		this.add(redZone2X, c);
		
		c.gridx = 2;
		c.gridy = 9;
		c.gridwidth = 1;
		redZone2Y = new TextField(4);
		new DPMToolTip("Enter Y location of the Red Zone top-right corner here", redZone2Y);
		this.add(redZone2Y, c);
		
		// GREEN PLAYER DROP ZONE

		c.gridx = 0;
		c.gridy = 10;
		c.gridwidth = 1;
		Label greenDZone1Label = new Label("Green Drop Zone bottom-left corner (x, y): ", Label.RIGHT);
		this.add(greenDZone1Label, c);

		c.gridx = 1;
		c.gridy = 10;
		c.gridwidth = 1;
		greenDZoneX = new TextField(4);
		new DPMToolTip("Enter X location of the Green Drop Zone bottom-left corner here", greenDZoneX);
		this.add(greenDZoneX, c);
		
		c.gridx = 2;
		c.gridy = 10;
		c.gridwidth = 1;
		greenDZoneY = new TextField(4);
		new DPMToolTip("Enter Y location of the Green Drop Zone bottom-left corner here", greenDZoneY);
		this.add(greenDZoneY, c);
		
		// RED PLAYER DROP ZONE
		
		c.gridx = 0;
		c.gridy = 11;
		c.gridwidth = 1;
		Label redDZone1Label = new Label("Red Drop Zone bottom-left corner (x, y): ", Label.RIGHT);
		this.add(redDZone1Label, c);

		c.gridx = 1;
		c.gridy = 11;
		c.gridwidth = 1;
		redDZoneX = new TextField(4);
		new DPMToolTip("Enter X location of the Red Drop Zone bottom-left corner here", redDZoneX);
		this.add(redDZoneX, c);
		
		c.gridx = 2;
		c.gridy = 11;
		c.gridwidth = 1;
		redDZoneY = new TextField(4);
		new DPMToolTip("Enter Y location of the Red Drop Zone bottom-left corner here", redDZoneY);
		this.add(redDZoneY, c);
		
		// GREEN PLAYER FLAG ID
		
		c.gridx = 0;
		c.gridy = 12;
		c.gridwidth = 1;
		Label greenFlagLabel = new Label("Green Flag Type [1-5]: ", Label.RIGHT);
		this.add(greenFlagLabel, c);

		c.gridx = 1;
		c.gridy = 12;
		c.gridwidth = 1;
		greenFlag = new TextField(4);
		new DPMToolTip("Enter value of Green Flag Type here:", greenFlag);
		this.add(greenFlag, c);

		// RED PLAYER FLAG ID
		
		c.gridx = 0;
		c.gridy = 13;
		c.gridwidth = 1;
		Label redFlagLabel = new Label("Red Flag Type [1-5]: ", Label.RIGHT);
		this.add(redFlagLabel, c);

		c.gridx = 1;
		c.gridy = 13;
		c.gridwidth = 1;
		redFlag = new TextField(4);
		new DPMToolTip("Enter value of Red Flag Type here:", redFlag);
		this.add(redFlag, c);
		
		// START, STOP and CLEAR BUTTONS
		
		c.gridx = 0;
		c.gridy = 14;
		c.gridwidth = 1;
		this.start = new Button("Start");
		this.start.addActionListener(this);
		new DPMToolTip("Start the program", this.start);
		this.add(start, c);

		c.gridx = 1;
		this.stop = new Button("Stop");
		this.stop.addActionListener(this);
		new DPMToolTip("Stop the program", this.stop);
		this.add(stop, c);

		c.gridx = 2;
		this.clear = new Button("Clear");
		this.clear.addActionListener(this);
		new DPMToolTip("Clear all entered values", this.clear);
		this.add(clear, c);

	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Button bt = (Button) e.getSource();
		try {
			if (bt.equals(this.start)) {
				Universal.TransmitRule sendTo = this.sendSelectPanel.getSendSelction();
				Universal.TRANSMIT_RULE = sendTo;

				int rStartCornerN = Integer.parseInt(this.rStartCorner.getText().trim());

				if (sendTo == Universal.TransmitRule.BOTH || sendTo == Universal.TransmitRule.RED_ONLY) {
					if (rStartCornerN > 4 || rStartCornerN < 1) {
						new DPMPopupNotification("Red player starting corner of " + rStartCornerN + " is out of the range 1-4", this.mw);
						return;
					}
				}

				int gStartCornerN = Integer.parseInt(this.gStartCorner.getText().trim());

				if (sendTo == Universal.TransmitRule.BOTH || sendTo == Universal.TransmitRule.GREEN_ONLY) {
					if (gStartCornerN > 4 || gStartCornerN < 1) {
						new DPMPopupNotification("Green player starting corner of " + gStartCornerN + " is out of the range 1-4", this.mw);
						return;
					}
				}

				if (sendTo == Universal.TransmitRule.BOTH) {
					if (rStartCornerN == gStartCornerN) {
						new DPMPopupNotification("Green and Red starting positions can't be the same", this.mw);
						return;
					}
				}
				
				//	Note to Maintainers											//
				//																//
				//	If you're using this as a template for other parameter sets	//
				//	you need to do 2 things:									//
				//	1.  Modify the layout of the screen to display and input	//
				//      new parameters (above).									//
				//	2.  Convert text strings to variables (below).				//
				//	3.  Modify the server transmit method in the server class	//
				//		but instantiated below.									//
				//																//
				//	F.P. Ferrie, February 28, 2014.								//
				
				//  Interpretation of test strings to ints  //

				int rTeamNumberN = Integer.parseInt(this.rTeamNumber.getText().trim());
				int gTeamNumberN = Integer.parseInt(this.gTeamNumber.getText().trim());
				int gz1x = Integer.parseInt(this.greenZone1X.getText().trim()), 
						gz1y = Integer.parseInt(this.greenZone1Y.getText().trim()), 
						gz2x = Integer.parseInt(this.greenZone2X.getText().trim()), 
						gz2y = Integer.parseInt(this.greenZone2Y.getText().trim()), 
						rz1x = Integer.parseInt(this.redZone1X.getText().trim()), 
						rz1y = Integer.parseInt(this.redZone1Y.getText().trim()), 
						rz2x = Integer.parseInt(this.redZone2X.getText().trim()), 
						rz2y = Integer.parseInt(this.redZone2Y.getText().trim()),
						gdzx = Integer.parseInt(this.greenDZoneX.getText().trim()),
						gdzy = Integer.parseInt(this.greenDZoneY.getText().trim()),
						rdzx = Integer.parseInt(this.redDZoneX.getText().trim()),
						rdzy = Integer.parseInt(this.redDZoneY.getText().trim()),
						gflg = Integer.parseInt(this.greenFlag.getText().trim()),
						rflg = Integer.parseInt(this.redFlag.getText().trim());
						
				if (gz2x <= gz1x || gz2y <= gz1y) {
					new DPMPopupNotification("Green Zone top-right corner coordinates should be " +
							"strictly larger than bottom-left corner coordinates", this.mw);
					return;
				}
				if (rz2x <= rz1x || rz2y <= rz1y) {
					new DPMPopupNotification("Red Zone top-right corner coordinates should be " +
							"strictly larger than bottom-left corner coordinates", this.mw);
					return;	
				}

				int[]teamNumbers = new int[] {rTeamNumberN, gTeamNumberN};
				char[] roles = new char[] { 'R', 'G' };
				int[] startCorners = new int[] { rStartCornerN, gStartCornerN };
				int[] greenZoneCoords = new int[] { gz1x, gz1y, gz2x, gz2y };
				int[] redZoneCoords = new int[] { rz1x, rz1y, rz2x, rz2y };
				int[] greenDropCoords = new int[] {gdzx, gdzy};
				int[] redDropCoords = new int[] {rdzx, rdzy};
				int[] flags = new int[] {gflg, rflg};

				// try wifi transmission
				int success = 0;
				success = server.transmit(teamNumbers, roles, startCorners, greenZoneCoords, redZoneCoords, greenDropCoords, redDropCoords, flags);
				if (success == 0) {
					this.mw.startTimer();
				} //else
					// "Transmission failed" popup is commented out - clunky and unnecessary feature
					//new DPMPopupNotification("Some wifi error occured trying to transmit data, please retry", this.mw);
			} else if (bt.equals(this.stop)) {
				// stop button pressed
				this.mw.pauseTimer();
			} else if (bt.equals(this.clear)) {
				// clear button pressed, clear fields, reset timer, and clear wifi panel
				this.clearFields();
				this.mw.clearTimer();
				this.mw.clearWifiPanel();
			} else {
				System.out.println("Non-handled event...");
			}
		} catch (NumberFormatException ex) {
			// string where number should be
			new DPMPopupNotification(
					"One of the numerical values was not a number", this.mw);
			return;
		}

	}

	private void clearFields() {
		this.rTeamNumber.setText("");
		this.rStartCorner.setText("");
		this.greenZone1X.setText("");
		this.greenZone1Y.setText("");
		this.greenZone2X.setText("");
		this.greenZone2Y.setText("");
		this.redZone1X.setText("");
		this.redZone1Y.setText("");
		this.redZone2X.setText("");
		this.redZone2Y.setText("");
		this.gTeamNumber.setText("");
		this.gStartCorner.setText("");
		this.greenDZoneX.setText("");
		this.greenDZoneY.setText("");
		this.redDZoneX.setText("");
		this.redDZoneY.setText("");
		this.greenFlag.setText("");
		this.redFlag.setText("");
	}
}
