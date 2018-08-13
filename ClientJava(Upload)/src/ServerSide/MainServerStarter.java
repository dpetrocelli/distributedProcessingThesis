package ServerSide;



public class MainServerStarter {

	public static void main(String[] args) {
		MainServer ms = new MainServer(9000);
		Thread threadMs = new Thread(ms);
		threadMs.start();

	}

}
