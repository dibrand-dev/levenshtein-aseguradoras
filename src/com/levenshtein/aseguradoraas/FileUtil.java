package com.levenshtein.aseguradoraas;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class FileUtil {
	
	private static BufferedWriter br;

	public static List<Record> buildFromFile(String filePath) throws Exception {
		List<Record> lines = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			String line;

			while ((line = br.readLine()) != null) {

				Record record = new Record();

				// Separar la línea por el caracter separador
				//				String[] fields = line.split(separator);

				try {
					String[] fields = parseLine(line);

					record.setIdEntidad(Integer.parseInt(fields[0]));
					record.setName(fields[1]);
					record.setPostal(fields[5]);
					record.setAddress(fields[6]);

					lines.add(record);					
				}
				catch(NumberFormatException e) {
					System.out.println("Error procesing readFile: " + e.toString());
					continue;
				}
			}
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		
		if(br == null)
			br = new BufferedWriter(new FileWriter(filePath + ".out", false));
		
		return lines;
	}  
	
	public static void closeFile() throws Exception {
		br.close();
	}

	private static String[] parseLine(String line) {
		List<String> fields = new ArrayList<>();
		StringBuilder currentField = new StringBuilder();
		boolean inQuotes = false;

		for (char ch : line.toCharArray()) {
			if (ch == '\"') {
				inQuotes = !inQuotes; // Toggle the inQuotes flag
			} else if (ch == ',' && !inQuotes) {
				// End of field
				fields.add(currentField.toString());
				currentField.setLength(0); // Clear the current field
			} else {
				currentField.append(ch);
			}
		}
		fields.add(currentField.toString()); // Add the last field

		return fields.toArray(new String[0]);
	}

	public static synchronized void write(Collection<Record> coincidences) throws Exception{
		Iterator<Record> it = coincidences.iterator();
		while(it.hasNext()) {
			Record next = it.next();

			System.out.println(next.toString());
			br.write(next.toString());
			br.newLine();
		}

		System.out.println();
		br.newLine();
		br.flush();

	}

}
