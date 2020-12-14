package org.scheduler.agent.behaviour;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.GeodeticCalculator;
import org.scheduler.Tick;
import org.scheduler.agent.Agent;
import org.scheduler.agent.state.ShipState;

import dk.dma.ais.binary.SixbitException;
import dk.dma.ais.message.AisMessage1;
import dk.dma.ais.message.AisPosition;
import dk.dma.ais.sentence.Vdm;
import dk.dma.enav.model.geometry.Position;

/**
 * NmeaPosReportUdpOutputBehaviour
 * 
 * Broadcasts position reports for given shipState onto given udpPort
 * 
 * @author chris
 *
 */
public class NmeaPosReportUdpOutputBehaviour implements ITickExecution {

	private ShipState shipState = null;

	private int udpPort = 2947;

	private Agent agent = null;

	private DatagramSocket datagramSocket = null;
	private DatagramPacket packet = null;

	private Point2D previousPosition = null;
	private GeodeticCalculator geoCalc = GeodeticCalculator.create(CommonCRS.WGS84.geographic());

	/**
	 * Ctor
	 * 
	 * @param shipState
	 */
	public NmeaPosReportUdpOutputBehaviour(ShipState shipState, int udpPort) {
		this.shipState = shipState;
		this.udpPort = udpPort;
		try {
			this.datagramSocket = new DatagramSocket();
			this.packet = new DatagramPacket(new byte[1], 1, InetAddress.getLocalHost(), this.udpPort);
		} catch (SocketException | UnknownHostException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void execute(Tick tick) {

		double frequency = agent.getScheduler().SchedulerConfiguration().getFreqency_Hz() <= 1.0 ? 1.0
				: agent.getScheduler().SchedulerConfiguration().getFreqency_Hz();
		double simulationSpeed = agent.getScheduler().SchedulerConfiguration().getSimulationSpeed();
		if (tick.getCurrentTick() % (frequency * simulationSpeed) == 0) {
			AisMessage1 msg1 = new AisMessage1();

			msg1.setRepeat(0);
			msg1.setUserId(shipState.getMMSI());
			msg1.setNavStatus(0);
			msg1.setRot(0);
			msg1.setSog((int) (shipState.getSpeed_current_kn() * 10));
			msg1.setPosAcc(1);
			AisPosition pos = new AisPosition(
					Position.create(shipState.getPoint().getY(), shipState.getPoint().getX()));
			msg1.setPos(pos);
			
			if (previousPosition != null) {
				/** calculate CoG **/
				geoCalc.setStartGeographicPoint(previousPosition.getY(), previousPosition.getX());
				geoCalc.setEndGeographicPoint(shipState.getPoint().getY(), shipState.getPoint().getX());
				double azimuth = geoCalc.getStartingAzimuth();
				if(azimuth < 0) azimuth = 360 - (azimuth * -1);
				msg1.setCog((int) (azimuth * 10));
			} else {
				/** CoG == CtW/Heading **/
				msg1.setCog((int) (shipState.getHeading_current_deg() * 10));
			}
			msg1.setTrueHeading((int) (shipState.getHeading_current_deg() * 1));
			msg1.setUtcSec(42);
			msg1.setSpecialManIndicator(0);
			msg1.setSpare(0);
			msg1.setRaim(0);
			msg1.setSyncState(0);
			msg1.setSlotTimeout(0);
			msg1.setSubMessage(2230);

			String[] sentences = null;
			try {
				sentences = Vdm.createSentences(msg1, 1);
			} catch (SixbitException e) {
				e.printStackTrace();
			}
//	        System.out.println("POS VDM: " + StringUtils.join(sentences, "\n"));

			try {

				// DatagramPacket packet = new DatagramPacket(StringUtils.join(sentences,
				// "\n").getBytes(), StringUtils.join(sentences, "\n").length(),
				// InetAddress.getLocalHost(), this.udpPort);
				packet.setData(StringUtils.join(sentences, "\n").getBytes());
				datagramSocket.send(packet);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		/** store previous position **/
		previousPosition = (Point2D) shipState.getPoint().clone();
	}

	@Override
	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	public void stop() {
		datagramSocket.close();
	}

}
