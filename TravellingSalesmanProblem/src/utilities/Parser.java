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
			 * ��� ������� parse �� root element ��� ������� ��� �� ����� �� ��������� ��� TSP instance ���
			 * �� ��������� error �����������. ������ ������� skip ��� ���� ������.
			 */
			String currentLine = fileReader.readLine();
			if(!currentLine.contains("travellingSalesmanProblemInstance")) {
				throw new UnsupportedDataTypeException("The specified instance is not a TSP instance.");
			}
			skipLines(fileReader, 1);
			
			/*
			 * ��� �������� ������� parse �� ����� ��� ������� �� ����� �������� �� ������ ��� ������ ��� ��
			 * ������������.
			 */
			int totalVertexCount = getTotalVertexCount(fileReader);
			
			/*
			 * ���� ���� �������� ������ ��� ��� �������� ��� �����, ������� �� graph �� ���� ������� ��� ���
			 * ���������� ������� ��� ���� ����������.
			 */
			fillGraphWithVertexes(graph, fileReader, totalVertexCount);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/*
		 * ��� ������������ �� ������ ����� graph.
		 */
		return graph;
	}
	
	/**
	 * ����� skip �� ��������� ������ �������.
	 * 
	 * @param fileReader � reader ��� �������� �� ����������� ��� �������
	 * @param lineCountToBeSkipped �� ������ ��� ������� ��� ������� �� �������������
	 * @throws IOException
	 */
	private static void skipLines(BufferedReader fileReader, int lineCountToBeSkipped) throws IOException {
		for(int i = 0; i < lineCountToBeSkipped; i++) {
			fileReader.readLine();
		}
	}
	
	/**
	 * ����� parse �� ����� ��� �������, ������ ��� ���������� �� �������� ������ ������ ��� �����������.
	 * 
	 * @param fileReader � reader ��� �������� �� ����������� ��� �������
	 * @return ��� �������� ������ ������ ��� �����������
	 * @throws IOException
	 */
	private static int getTotalVertexCount(BufferedReader fileReader) throws IOException {
		int totalVertexCount = 0;
		
		/*
		 * �� ������� ��� ������� ����� ��� ������ <�����><�������>.xml, ����� ����������� ��
		 * ������������ regular expression ���� �� �������� ��� ������ ���� ��� ����� ���
		 * ������� ��� �� ��� �����.
		 */
		Matcher nameMatcher = NAME_PATTERN.matcher(fileReader.readLine());
		nameMatcher.find();
		totalVertexCount = Integer.parseInt(nameMatcher.group());
		
		skipLines(fileReader, 11);
		
		return totalVertexCount;
	}
	
	/**
	 * ����� parse ����� ���� ������� ��� ����������� ��� ���� ���������� ��� graph ���� �� ��� 
	 * ����������� ����� ����. 
	 * 
	 * @param graph �� ����������� ����� Graph ��� ������� �� ���������
	 * @param fileReader � reader ��� �������� �� ����������� ��� �������
	 * @param totalVertexCount o ��������� ������� ������ ��� �����������
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
	 * ����� parse ���� ��� ����� ��� ��� �������� ����� ��� ��� ��������� �� �����.
	 * 
	 * @param vertex � ������ ��� ��� ����� ������� �� ����������� �����
	 * @param fileReader � reader ��� �������� �� ����������� ��� �������
	 * @param totalVertexCount o ��������� ������� ������ ��� �����������
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
			 * ������� �� ��� ��������� ��� XML ������� ��� TSP, �� ����� ��� ����� ���
			 * ������ ���� ��� ����� ����������������� ���� ������ �� �����������������
			 * ��� �� ������� �� ���������� �� ������������ �� ��� �������� TSPLIB.
			 * 
			 * ����������� ��� �� ��������������� ��� ������� ��� ����� �� ���������������
			 * � ������� ��� TSPLIB, � ������ �������� ��� ������� ����� ��� ����������
			 * nint(x) ��� ������ �� �������������� �� (int) (x + 0.5) ������� �� ��� �������
			 * ������ ��� TSPLIB.
			 */
			Edge edge = new Edge(id, (int) (cost + 0.5));
			vertex.addEdge(edge);
		}
		/*
		 * �������� �� ����� ��� ������ �� ������� ����� �� ���� �� ������ ��� ������ ���� �� 
		 * ����� ������ �� ������� �� ������������.
		 */
		Collections.sort(vertex.getEdgeList());
	}
}
