package utilities;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.UnsupportedDataTypeException;

import xml.representation.classes.Edge;
import xml.representation.classes.Graph;
import xml.representation.classes.Vertex;

public class Parser {

	private final static Pattern NAME_PATTERN = Pattern.compile("[0-9]+");
	
	public static Graph fillTspGraphFromFile(Graph graph, String fileToBeUsed) {
		try(BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(fileToBeUsed)))) {
			skipLines(fileReader, 1);
			
			/*
			 * Εδώ κάνουμε parse το root element του αρχείου για να δούμε αν πρόκειται για TSP instance και
			 * να πετάξουμε error διαφορετικά. Επίσης κάνουμε skip μια κενή γραμμή.
			 */
			String currentLine = fileReader.readLine();
			if(!currentLine.contains("travellingSalesmanProblemInstance")) {
				throw new UnsupportedDataTypeException("The specified instance is not a TSP instance.");
			}
			skipLines(fileReader, 1);
			
			/*
			 * Στο παρακάτω κάνουμε parse το όνομα του αρχείου το οποίο περιέχει το πλήθος των κόμβων και το
			 * αποθηκεύουμε.
			 */
			int totalVertexCount = getTotalVertexCount(fileReader);
			
			/*
			 * Μέσα στην ακόλουθη μέθοδο και τις μεθόδους που καλεί, γεμίζει το graph με τους κόμβους και τις
			 * αποστάσεις καθενός από τους υπόλοιπους.
			 */
			fillGraphWithVertexes(graph, fileReader, totalVertexCount);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/*
		 * Εδώ επιστρέφουμε το γεμάτο πλέον graph.
		 */
		return graph;
	}
	
	/**
	 * Κάνει skip το ζητούμενο πλήθος γραμμών.
	 * 
	 * @param fileReader ο reader που διαβάζει τα περιεχόμενα του αρχείου
	 * @param lineCountToBeSkipped το πλήθος των γραμμών που θέλουμε να προσπεράσουμε
	 * @throws IOException
	 */
	private static void skipLines(BufferedReader fileReader, int lineCountToBeSkipped) throws IOException {
		for(int i = 0; i < lineCountToBeSkipped; i++) {
			fileReader.readLine();
		}
	}
	
	/**
	 * Κάνει parse το όνομα του αρχείου, εξάγει και επιστρέφει το συνολικό αριθμό κόμβων του προβλήματος.
	 * 
	 * @param fileReader ο reader που διαβάζει τα περιεχόμενα του αρχείου
	 * @return τον συνολικό αριθμό κόμβων του προβλήματος
	 * @throws IOException
	 */
	private static int getTotalVertexCount(BufferedReader fileReader) throws IOException {
		int totalVertexCount = 0;
		
		/*
		 * Τα ονόματα των αρχείων είναι της μορφής <όνομα><μέγεθος>.xml, οπότε χρησιμοποιώ το
		 * συγκεκριμένο regular expression ώστε να εντοπίσω τον αριθμό μέσα στο όνομα του
		 * αρχείου και να τον εξάγω.
		 */
		Matcher nameMatcher = NAME_PATTERN.matcher(fileReader.readLine());
		nameMatcher.find();
		totalVertexCount = Integer.parseInt(nameMatcher.group());
		
		skipLines(fileReader, 11);
		
		return totalVertexCount;
	}
	
	/**
	 * Κάνει parse όλους τους κόμβους του προβλήματος και τους αποθηκεύει στο graph μαζί με τις 
	 * αντίστοιχες ακμές τους. 
	 * 
	 * @param graph το αντικείμενο τύπου Graph που θέλουμε να γεμίσουμε
	 * @param fileReader ο reader που διαβάζει τα περιεχόμενα του αρχείου
	 * @param totalVertexCount o συνολικός αριθμός κόμβων του προβλήματος
	 * @throws IOException
	 */
	private static void fillGraphWithVertexes(Graph graph, BufferedReader fileReader, int totalVertexCount) throws IOException {
		for(int vertexIndex = 0; vertexIndex < totalVertexCount; vertexIndex++) {
			Vertex vertex = new Vertex(vertexIndex);
			addEdgesToVertex(vertex, fileReader, totalVertexCount);
			graph.addVertex(vertex);
			skipLines(fileReader, 2);
		}
	}
	
	/**
	 * Κάνει parse όλες τις ακμές για τον τρέχοντα κόμβο και τις προσθέτει σε αυτόν.
	 * 
	 * @param vertex ο κόμβος για τον οποίο θέλουμε να προσθέσουμε ακμές
	 * @param fileReader ο reader που διαβάζει τα περιεχόμενα του αρχείου
	 * @param totalVertexCount o συνολικός αριθμός κόμβων του προβλήματος
	 * @throws IOException
	 */
	private static void addEdgesToVertex(Vertex vertex, BufferedReader fileReader, int totalVertexCount) throws IOException {
		for(int edgeIndex = 0; edgeIndex < totalVertexCount - 1; edgeIndex++) {
			String currentEdge = fileReader.readLine();
			
			int costStartIndex = currentEdge.indexOf("\"");
			int costEndIndex = currentEdge.indexOf("\"", costStartIndex + 1);
			int idStartIndex = currentEdge.indexOf(">");
			int idEndIndex = currentEdge.indexOf("<", idStartIndex + 1);
			
			double cost = Double.parseDouble(currentEdge.substring(costStartIndex + 1, costEndIndex));
			int id = Integer.parseInt(currentEdge.substring(idStartIndex + 1, idEndIndex));
			
			/*
			 * Σύμφωνα με την περιγραφή των XML αρχείων της TSP, τα κόστη των ακμών στα
			 * αρχεία αυτά δεν είναι στρογγυλοποιημένα αλλά πρέπει να στρογγυλοποιηθούν
			 * για να μπορούν να συγκριθούν τα αποτελέσματα με την κλασσική TSPLIB.
			 * 
			 * Προτείνεται για τη στρογγυλοποίηση του κόστους των βαρών να χρησιμοποιείται
			 * ο κανόνας της TSPLIB, ο οποίος αναφέρει ότι γίνεται χρήση της συνάρτησης
			 * nint(x) που μπορεί να αντικατασταθεί με (int) (x + 0.5) σύμφωνα με τις οδηγίες
			 * χρήσης της TSPLIB.
			 */
			Edge edge = new Edge(id, (int) (cost + 0.5));
			vertex.addEdge(edge);
		}
		/*
		 * Ταξινομώ τη λίστα των κόμβων με αύξουσα σειρά ως προς το κόστος των κόμβων ώστε να 
		 * είναι έυκολο να βρεθούν οι κοντινότεροι.
		 */
		Collections.sort(vertex.getEdgeList());
	}
}
