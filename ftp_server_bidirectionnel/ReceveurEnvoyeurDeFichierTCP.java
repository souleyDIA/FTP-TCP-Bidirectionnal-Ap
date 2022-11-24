import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ReceveurEnvoyeurDeFichierTCP {

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = null;
        int port = args.length == 0 ? 1234 : Integer.parseInt(args[0]); // Si aucun argument n'est passé en paramètre, le port par défaut est 1234
        try {
            serverSocket = new ServerSocket(port); // Création du socket serveur
        } catch (IOException e) { // Si le port est déjà utilisé
            System.err.println("Could not listen on port: " + port);
            System.exit(1);
        }

        Socket clientSocket = null; // Socket client
        System.out.println("Server is listening on port " + port); // Affichage du port d'écoute
        try { // Attente d'une connexion
            clientSocket = serverSocket.accept(); // Création du socket client
            System.out.println("Client connected");
        } catch (IOException e) {
            System.err.println("Connexion échouée");
            System.exit(1);
        }

        String inputLine; // Chaîne de caractères reçue
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true); // Flux de sortie vers le client
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); // Flux d'entrée depuis le client

      try {

        while (true) { // Scanner pour saisir le nom du fichier à envoyer

            // check if we have data to get from the client
            if (in.ready()) {
                // read the data from the client
                inputLine = in.readLine();
                System.out.println("Fichier recu: " + inputLine); // Affichage du nom du fichier reçu
                File file = new File(inputLine); // Création du fichier à partir du nom reçu
                file.createNewFile(); 
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    while ((inputLine = in.readLine()) != null) { 
                        System.out.println("contenu du fichier: " + inputLine); // Affichage du contenu du fichier reçu
                        fos.write(inputLine.getBytes()); // Ecriture du contenu du fichier
                    }
                }
            }
            Scanner sc = new Scanner(System.in);
            System.out.println("Entrez une commande (PUT cheminFichier) : "); 
            if(sc.hasNextLine()) { 
                String commande = sc.nextLine(); // Saisie de la commande
                if(commande.startsWith("PUT")) { 
                    String nomFichierEnvoter = commande.substring(4); // Récupération du nom du fichier à envoyer
                    File fichierEnvoye = new File(nomFichierEnvoter); // Création du fichier à partir du nom saisi
                    if(fichierEnvoye.exists()) { // Vérification de l'existence du fichier
                        out.println(nomFichierEnvoter); // Envoi du nom du fichier
                        out.flush();
                        FileInputStream fis = new FileInputStream(fichierEnvoye); // Flux d'entrée depuis le fichier
                        int octetLu = 0; // Octet lu
                        while((octetLu = fis.read()) != -1) { // Lecture du fichier
                            clientSocket.getOutputStream().write(octetLu); // Envoi de l'octet lu
                        }
                        clientSocket.getOutputStream().flush();
                        fis.close(); // Fermeture du flux
                        System.out.println("Fichier envoyé : " + nomFichierEnvoter); // Affichage du nom du fichier envoyé
                    }
                    else {
                        System.out.println("Le fichier n'existe pas"); // Affichage d'un message d'erreur si le fichier n'existe pas
                    }
                }
                else {
                    System.out.println("Commande non reconnue");
                }
                System.out.println("Entrez une commande (PUT cheminFichier) : ");
            }
        }
        
    } catch (IOException e) {
        System.err.println("Connexion échouée");
        System.exit(1);
    }

    }
}


// java ReceveurEnvoyeurDeFichierTCP.java && java ReceveurEnvoyeurDeFichierTCP <port? : optionnel>