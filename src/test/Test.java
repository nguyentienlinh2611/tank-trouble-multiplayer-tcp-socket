package test;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;


public class Test {
	public boolean[][][] maze;
	public Test() throws UnknownHostException, IOException, ClassNotFoundException{
		Socket socket = new Socket("localhost",9000);
		ObjectOutputStream writer = new ObjectOutputStream(socket.getOutputStream());
		ObjectInputStream reader = new ObjectInputStream(socket.getInputStream());
		maze = (boolean[][][]) reader.readUnshared();
	}
}
