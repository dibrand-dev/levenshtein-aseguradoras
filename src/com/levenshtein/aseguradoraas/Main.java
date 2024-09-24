package com.levenshtein.aseguradoraas;

import java.util.ArrayList;
import java.util.List;

public class Main {

	final static int PORCENTAJE_TOLERANCIA = 30;

	public static void main(String[] args) throws Exception{
		String filePath = "/Users/leonardo.larraquy/eclipse-workspace/levenshtein-aseguradoras/polizas.csv";

		List<Record> data = FileUtil.buildFromFile(filePath);

		while(data.size() != 0) {		
			Record record = data.remove(0);

			record.setPadre(true);

			ArrayList<Record> concidences = new ArrayList<Record>();

			for (Record other : data) {

				String original  = record.getName() + "-" + record.getAddress()  + "-" + record.getPostal();
				String comparado = other.getName()  + "-" + other.getAddress()   + "-" + other.getPostal();

				double caracteresDistintos = Math.abs(original.length() - comparado.length());

				if(( caracteresDistintos / original.length() * 100) > PORCENTAJE_TOLERANCIA) {
					continue;
				}

				double distance = Levenshtein.calculate(original, comparado);

				double similitud = ( distance / original.length()) * 100;

				if(similitud <= PORCENTAJE_TOLERANCIA) {	

					concidences.add(other);

					other.setDistance((int) distance);
					other.setSimilitud(100 - similitud);

				}

			}
			
			if(concidences.size() > 0) {
				concidences.add(0, record);
				
				FileUtil.write(concidences);				
			}

			data.removeAll(concidences); //elimino todas las coincidencias de la lista

		}

		FileUtil.closeFile();
	}

}
