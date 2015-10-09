package org.kairus.api;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;
import org.kairus.kairusTeam.EmptyTeam;
import org.kairus.kairusTeam.KairusTeam;

import gnu.io.*;

public class Server {
	static ServerSocket serverSocket;
	static Socket socket;
	static DataInputStream in;
	static DataOutputStream out;
	static Game gameInstance;
	
	static String state = "reset";
	
	static boolean useSerial = false;
	
	public static String getState() {return state;}
	public static void setState(String newState) {
		if (newState != state)
			state = newState;
		else
			state = "";
	}
	

	static ArrayList<ServerThread> threads = new ArrayList<ServerThread>();
	Server() throws IOException{
		if (useSerial){
			ServerThread.initSerial();
			ServerThread.writeSerial(true);
			try {Thread.sleep(200);} catch (InterruptedException e1) {}
			ServerThread.writeSerial(true);
			try {Thread.sleep(200);} catch (InterruptedException e1) {}
			ServerThread.writeSerial(true);
		}
		
		int port = 10055;
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(port);
			System.out.println("Soccer robots server active on port "+port+".");
		} catch (IOException e) {
			System.err.println("Could not listen on port: "+port+".");
			System.exit(-1);
		}
		while (true)
		{
			ServerThread tmp = new ServerThread(serverSocket.accept());
			threads.add(tmp);
		}

	}





	public static void main(String[] args) throws IOException {
		new Server();
	}

}

class gameRunner extends Thread{
	Team[] teams;
	gameRunner(Team[] teams){
		this.teams=teams;
	}
	public void run(){
		Server.gameInstance = new Game(teams);
	}
}

class ServerThread extends Thread implements constants{
	static Team[] teams = new Team[2];
	DataInputStream in;

	ServerThread(Socket socket){
		try {
			in = new DataInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		teams[0] = new KairusTeam(0);
		teams[1] = new EmptyTeam(1);
		this.start();
		new gameRunner(new Team[]{teams[0], teams[1]}).start();
	}
	long lastTime = System.currentTimeMillis();
	double[][][] robotVelocitys = new double[2][5][3];
	double[][][] robotPositions = new double[2][5][3];
	double[] ballPosition = new double[2];
	double[] ballVelocity = new double[2];


	public double toBigEndian(double d){
		ByteBuffer bb = ByteBuffer.wrap(new byte[8]);
		bb.putDouble(d);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.position(0);
		return bb.getDouble();
	}
	public double toRadians(double d){
		return (d*Math.PI)/180;
	}

	static DataOutputStream comOS = null;
	public static void initSerial(){
		Enumeration portList = CommPortIdentifier.getPortIdentifiers();
		CommPortIdentifier portId = null;
		boolean portFound = false;
		while (portList.hasMoreElements()) {
			portId = (CommPortIdentifier) portList.nextElement();
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				if (portId.getName().equals("COM3")) {
					portFound = true;
				} 
			}
		}
		if (!portFound) {
			System.out.println("port COM3 not found.");
			return;
		}
		SerialPort port;
		try {
			port = (SerialPort)portId.open("COM3", 2000);
			port.setSerialPortParams(115200,SerialPort.DATABITS_8, SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
			comOS = new DataOutputStream(port.getOutputStream());
		} catch (PortInUseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnsupportedCommOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static int[] inputs = new int[10];
	
	public static void writeSerial( boolean stop){
		byte[] toSend = new byte[48];
		toSend[0] = (byte) 0xff;
		toSend[1] = (byte) 0xff;
		toSend[2] = (byte) 0x01;

		int MAX_SPEED = 8196;//8196
		int accelerationJump = 30;
		
		if (!stop){
			
			for (int i = 0;i<5;i++){
				//if (i == 0)
				//	System.out.println("speed: "+teams[0].robot[i].GetSpeedInput()+" spin: "+teams[0].robot[i].GetSpinInput());
				inputs[i*2]   = (int) (teams[0].robot[i].GetSpeedInput()*MAX_SPEED*-1);
				inputs[i*2+1] = (int) (teams[0].robot[i].GetSpinInput() *MAX_SPEED*-1);
				//if (i == 0)
				//	System.out.println("new speed: "+inputs[i*2]+" spin: "+inputs[i*2+1]);
			}
	
			for (int i = 0;i<10;i++){
				if (inputs[i] > MAX_SPEED) inputs[i] = MAX_SPEED;
				if (inputs[i] < -MAX_SPEED) inputs[i] = -MAX_SPEED;
				toSend[2*i+0+3] = (byte)( (inputs[i]   ) & 0xff);
				toSend[2*i+1+3] = (byte)( (inputs[i]>>8) & 0xff);
			}
		}
		/*
		for (int team = 0;team<1;team++)
			for (int i = 0;i<5;i++){
				int linV = (int) (teams[team].robot[i].GetSpeedInput()*MAX_SPEED);
				int angV = (int) (teams[team].robot[i].GetSpinInput() *MAX_SPEED);
				
				toSend[team*20+ 4*i+0+3] = (byte)( (linV   ) & 0xff);
				toSend[team*20+ 4*i+1+3] = (byte)( (linV>>8) & 0xff);
				toSend[team*20+ 4*i+2+3] = (byte)( (angV   ) & 0xff);
				toSend[team*20+ 4*i+3+3] = (byte)( (angV>>8) & 0xff);
			}
		*/
		byte checksum = 0;
		for (int i = 0; i < 47; i++){
			checksum += toSend[i];
		}
		toSend[47] = (byte) -checksum;
		
		try {
			comOS.write(toSend);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	int upto = 0;
	

	float goalX1 = 0.001f;
	float wallX1 = 0.095f;
	float wallX2 = 2.111f;
	float goalX2 = 2.208f;

	float Xoffset = -wallX1;
	float Xmultiplier = (FIELD_WIDTH/100)/(wallX2-wallX1);
	
	public void run(){
		try {
			Thread.sleep(1000);
			//writeSerial(false);
			//Thread.sleep(1000);
			//writeSerial(true);
			//System.exit(0);
			
			while (true){

				long delta = System.currentTimeMillis()-lastTime;
				lastTime = System.currentTimeMillis();
				double multiplier = (delta==0?(1000/1):(1000/delta)) / SCALE;
				for (int team = 0; team < 2; team++)
					for (int i = 0; i < 5; i++){
						//System.out.print("[ ");
						for (int o = 0; o<3; o++){
							double read = in.readDouble();
							//System.out.print(o+" Read: "+read);
							double newValue = toBigEndian(read);
							//System.out.print(" Converted: "+newValue);
							if (o == 0){//Convert X
								newValue += Xoffset;
								newValue *= Xmultiplier;
							}
							if (o == 1){//Flip Y
								newValue = (1.8-newValue);
							}
							if (o == 2){//Convert angle
								newValue = -1*toRadians(newValue);
							}
							robotVelocitys[team][i][o] = (newValue-robotPositions[team][i][o]) * multiplier;
							robotPositions[team][i][o] = newValue;
							//System.out.print(" "+newValue+" ("+robotVelocitys[team][i][o]+") ");
							
							/*
							if (team == 0 && i == 0){
								System.out.print(newValue+" ");
								if (o == 2)
									System.out.println();
							}*/

							//System.out.println();

						}
						//System.out.print("] ");
					}

				//Get the ball
				for (int i = 0; i < 2; i++){
					double newValue = toBigEndian(in.readDouble());
					if (i == 0){//Convert X
						newValue += Xoffset;
						newValue *= Xmultiplier;
					}
					if (i == 1){//Flip Y
						newValue = 1.8-newValue;
					}
					ballVelocity[i] = (newValue-ballPosition[i]) * multiplier;
					ballPosition[i] = newValue;
				}

				// Put unknown positions (enemies) at -10000
				for (int i = 0; i < 2; i++)
					for (int o = 0; o < 5; o++)
						for (int p = 0; p<2;p++){
							if (robotPositions[i][o][p] < -1 || robotPositions[i][o][p] > 2.6){
								System.out.println(i+" "+o+" "+p+" Out of bounds! "+robotPositions[i][o][p]);
								robotPositions[i][o][p] = -10000;
							}
						}

				// Set frame data!
				for (int i = 0; i < 2; i++)
					for (int o = 0; o < 5; o++){
						teams[i].robot[o].body.setType(BodyType.STATIC);
						teams[i].robot[o].body.setTransform(new Vec2((float)robotPositions[i][o][0]*100,(float)robotPositions[i][o][1]*100), (float) (robotPositions[i][o][2]));
						//System.out.println("Setting to "+(float)robotPositions[i][o][0]*100+" "+(float)robotPositions[i][o][1]*100);
						teams[i].robot[o].body.setType(BodyType.DYNAMIC);
						teams[i].robot[o].body.setLinearVelocity(new Vec2((float)robotVelocitys[i][o][0]*100,(float)robotVelocitys[i][o][1]*100));
						teams[i].robot[o].body.setAngularVelocity((float)robotVelocitys[i][o][2]);
					}
				teams[1].ball.setType(BodyType.STATIC);
				teams[1].ball.setTransform(new Vec2((float)ballPosition[0]*100, (float)ballPosition[1]*100), 0);
				teams[1].ball.setType(BodyType.DYNAMIC);
				teams[1].ball.setLinearVelocity(new Vec2((float)ballVelocity[0]*100, (float)ballVelocity[1]*100));
				teams[1].ball.setAngularVelocity(0); // Not sent, reset it!

				// Simulate a frame!
				for (int i = 0; i < 2; i++)
					teams[i].gameFrame();

				// Send information to serial port!
				
				if (Server.useSerial)
					writeSerial(false);
				
			}
		} catch (IOException e) {
			Server.threads.remove(this);
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}