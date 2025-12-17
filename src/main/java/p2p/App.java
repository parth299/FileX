package p2p;


import p2p.controller.FileController;

public class App {
    public static void main(String[] args) {
        try {
            FileController fileController = new FileController(8080);
            fileController.start();
            System.out.println("peerlink server started on port 8080");
            System.out.println("ui available at localhost:8080");

            Runtime.getRuntime().addShutdownHook(
                    new Thread(() -> {
                        System.out.println("shutting down the server");
                        fileController.stop();
                    })
            );
        }
        catch (Exception ex) {
            System.err.println("Failed to start the server on port 8080");
            ex.printStackTrace();
        }

    }
}
