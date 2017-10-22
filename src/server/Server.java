package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import model.GameEngine;
import model.Maze;
import model.Player;

public class Server {
	private Socket client1,client2;
	private ServerSocket listener;
	public Server(int port) throws IOException {
		listener = new ServerSocket(port);
		client1 = listener.accept();
		client2 = listener.accept();
		GameEngine game = new GameEngine(client1, client2);
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
