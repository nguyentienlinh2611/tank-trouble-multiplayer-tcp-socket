package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import model.Maze;

public class Server {
	public Server(int port) throws IOException {
		ServerSocket listener = new ServerSocket(port);
		Socket client1 = listener.accept();
		ObjectOutputStream writer = new ObjectOutputStream(client1.getOutputStream());
		ObjectInputStream reader = new ObjectInputStream(client1.getInputStream());
		Maze maze = new Maze(40);
		writer.writeUnshared(maze.getWalls());
		writer.flush();
	}
	public static void main(String[] args) {
		try {
			Server sv = new Server(9000);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
