import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class EnvoyeurReceveurDeFichierTCP {

    public static void main(String[] args) throws IOException {
        String ip = args[0]; // Adresse IP du serveur
        int port = Integer.parseInt(args[1]); // Port du serveur
        String chemin = " ";
        String commande = " ";
        String nomFichierRecu = " ";
        String nomFichierEnvoye = " ";

        try {
            try (Socket socket = new Socket(ip, port)) { // Création du socket
                System.out.println("Connexion établie avec le serveur " + ip + " sur le port " + port); // Affichage de la connexion
                while (true) {
                    if (socket.getInputStream().available() > 0) { // Si des données sont présentes dans le socket
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Création d'un buffer pour lire les données du socket
                        nomFichierRecu = in.readLine(); // Lecture du nom du fichier
                        File fichierRecu = new File(chemin + nomFichierRecu); // Création du fichier avec le même nom dans le système de fichier local
                        fichierRecu.createNewFile(); 
                        FileOutputStream out = new FileOutputStream(fichierRecu); // Création d'un buffer pour écrire les données dans le fichier
                        int octet = 0; // Initialisation de l'octet
                        while ((octet = socket.getInputStream().read()) != -1) { // Tant qu'il y a des octets à lire
                            out.write(octet); // Ecriture de l'octet dans le fichier
                        }
                        out.close(); // Fermeture du buffer d'écriture
                        System.out.println("Fichier " + nomFichierRecu + " reçu"); // Affichage du nom du fichier reçu
                    }
                    try (Scanner sc = new Scanner(System.in)) { // Création d'un scanner pour lire les commandes
                        System.out.println("Entrez une commande (PUT cheminFichier) : "); // Affichage de la commande
                        
                        while (sc.hasNextLine()) { // Tant qu'il y a des lignes à lire
                            commande = sc.nextLine(); // Lecture de la commande
                            if (commande.startsWith("PUT")) { // Si la commande commence par PUT
                                nomFichierEnvoye = commande.substring(4); // Récupération du nom du fichier
                                File fichierEnvoye = new File(nomFichierEnvoye); // Création du fichier
                                if (fichierEnvoye.exists()) { // Si le fichier existe
                                    PrintWriter out = new PrintWriter(socket.getOutputStream()); // Création d'un buffer pour écrire les données dans le socket
                                    out.println(nomFichierEnvoye); // Ecriture du nom du fichier dans le socket
                                    out.flush(); // Envoi des données
                                    FileInputStream in = new FileInputStream(fichierEnvoye); // Création d'un buffer pour lire les données du fichier
                                    int octet = 0; // Initialisation de l'octet
                                    while ((octet = in.read()) != -1) { // Tant qu'il y a des octets à lire
                                        socket.getOutputStream().write(octet); // Ecriture de l'octet dans le socket
                                    }
                                    socket.getOutputStream().flush(); // Envoi des données
                                    in.close();    // Fermeture du buffer de lecture
                                    System.out.println("Fichier " + nomFichierEnvoye + " envoyé");
                                } else {
                                    System.out.println("Fichier " + nomFichierEnvoye + " inexistant"); // Affichage d'un message d'erreur si le fichier n'existe pas
                                }
                            } else {
                                System.out.println("Commande incorrecte");
                            }
                            System.out.println("Entrez une commande (PUT cheminFichier) : ");
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Aucun serveur TCP n'est joignable"); // Affichage d'un message d'erreur
        }
    }
}

// javac EnvoyeurReceveurDeFichierTCP.java && java EnvoyeurReceveurDeFichierTCP 127.0.0.1 4444