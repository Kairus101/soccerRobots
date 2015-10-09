import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;


public class tester {
	static DataInputStream in;
	static DataOutputStream out;
	public static void main(String[] args) {
		try {
			int port = 10055;
			System.out.println("opening port "+port+" socket");
			Socket kkSocket = new Socket("127.0.0.1", port);
			System.out.println("opening output stream");
			out = new DataOutputStream(kkSocket.getOutputStream());
			System.out.println("found connection to 127.0.0.1, opening input stream.");
			in = new DataInputStream(kkSocket.getInputStream());
			Scanner s = new Scanner(System.in);
			
			double[] output = new double[32];

			
			for (int i = 0; i < 32; i++)
				//output[i] = Double.parseDouble(s.nextLine());
				output[i] = Math.random()*400;
			
			new reciever(in).start();
			
			while (true){
				Thread.sleep(33);
				
				for (int i = 0; i < 32; i++){
					if (i%3==2)
						output[i] += Math.random()*0.3-0.15;
					else
						output[i] += Math.random()*10-5;
					if (output[i]<0) output[i] = 0;
					if (output[i]>500) output[i] = 500;
				}
				out.writeByte(5);
				for (int i = 0; i < 32; i++)
					out.writeDouble(output[i]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

class reciever extends Thread{
	DataInputStream in;
	reciever(DataInputStream in){
		this.in = in;
	}
	public void run(){
		try {
			while (true)
					in.readInt();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
