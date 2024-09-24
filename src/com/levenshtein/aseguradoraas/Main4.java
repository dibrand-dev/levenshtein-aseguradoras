package com.levenshtein.aseguradoraas;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class Main4 {

	final static int PORCENTAJE_TOLERANCIA = 25;
	final static int PROCESOS_CONCURRENTES = 30;

	static List<RunnerX> runners = new ArrayList();
	static List<Record> data;
	/*
	public static void main(String[] args) throws Exception{
		String original  = "3K MANAGEMENT AND CONSTRUCTION LLC-URB GLENVIEW GDNS-730";
		String comparado = "3K MANAGEMENT & CONSTRUCTION LLC-PO BOX 105-715";

		double distance = Levenshtein.calculate(original, comparado);

		double similitud = ( distance / original.length()) * 100;

		System.out.println("v1 distance: " + distance + " similitud: "  + similitud);

		distance = Levenshtein.calculate(comparado, original);

		similitud = ( distance / comparado.length()) * 100;

		System.out.println("v2 distance: " + distance + " similitud: "  + similitud);
	}
	 */

	public static void main(String[] args) throws Exception{
		String filePath = "/Users/leonardo.larraquy/eclipse-workspace/levenshtein-aseguradoras/polizas.csv";

		data = FileUtil.buildFromFile(filePath);

		int posicion = 0;

		while(data.size() != 0) {		
			Record record = data.get(posicion);

			if(existeAlgunoSimilar(record)) {
				posicion ++;
				continue;
			}
			else {

				addNewWorker(posicion, record);

				posicion = 0;

				while(runners.size() > PROCESOS_CONCURRENTES)
					Thread.currentThread().sleep(1000);

			}
		}

		FileUtil.closeFile();
	}

	private static synchronized void addNewWorker(int posicion, Record record) {
		removeData(posicion, null, null);		

		synchronized (runners) {
			record.setPadre(true);

			RunnerX th = new RunnerX();
			th.setRecord(record);

			List<Record> newList = new ArrayList();
			newList.addAll(data);
			th.setData(newList); // nueva lista de objetos copiados

			runners.add(th);
			th.start();
		}
	}


	private static boolean existeAlgunoSimilar(Record record) {
		synchronized (runners) {

			Iterator<RunnerX> it = runners.iterator();
			while(it.hasNext()) {
				RunnerX other = it.next();

				if(record.getIdEntidad() == other.getRecord().getIdEntidad() && other.getRecord().isPadre()) 
					return true;

				if(record.getName().trim().equals(other.getRecord().getName().trim()) && other.getRecord().isPadre()) 
					return true;

				String comparado  = record.getName() + "-" + record.getAddress()  + "-" + record.getPostal();
				String original   = other.getRecord().getName()  + "-" + other.getRecord().getAddress()   + "-" + other.getRecord().getPostal();

				double distance = Levenshtein.calculate(original, comparado);

				double similitud = ( distance / original.length()) * 100;

				if(similitud <= PORCENTAJE_TOLERANCIA && other.getRecord().isPadre()) 
					return true;					
			}

			return false;
		}	
	}

	public synchronized static void removeData(int posicion, Collection concidences, RunnerX th) {
		synchronized (data) {
			if(concidences == null )
				data.remove(posicion);
			else {

				data.removeAll(concidences);

				synchronized (runners) {
					runners.remove(th);					
				}
			}
		}
	}
}

class RunnerX extends Thread{

	private Record record;
	private List<Record> data;

	public Record getRecord() {
		return record;
	}
	public void setData(List<Record> data) {
		this.data = data;
	}	
	public void setRecord(Record record) {
		this.record = record;
	}

	private void addCoincidence(Record other, Collection coincidences, int distance, double similitud, String reason) {
		other.setPadre(false);
		coincidences.add(other);

		other.setDistance(distance);
		other.setSimilitud(similitud);

		other.setFound(reason);
	}

	public void run() {
		super.run();

		List<Record> coincidences = new ArrayList();

		coincidences.add(this.record);

		try {

			while(data.size() != 0) {		
				Record other = data.remove(0);

				if(record.getIdEntidad() == other.getIdEntidad()) 
					this.addCoincidence(other, coincidences, 0, 100, "By same ID");
				else {
					String original  = record.getName() + "-" + record.getAddress()  + "-" + record.getPostal();
					String comparado = other.getName()  + "-" + other.getAddress()   + "-" + other.getPostal();

					if(record.getName().trim().equals(other.getName().trim())) {
						double distance = Levenshtein.calculate(original, comparado);

						double similitud = ( distance / original.length()) * 100;

						if(other.getFound() == null)
							this.addCoincidence(other, coincidences, (int) distance, (100 - similitud), "By same name");
					}
					else {

						//						double caracteresDistintos = Math.abs(original.length() - comparado.length());

						//						if(( caracteresDistintos / original.length() * 100) > Main4.PORCENTAJE_TOLERANCIA) 
						//							continue;

						double distance = Levenshtein.calculate(original, comparado);

						double similitud = ( distance / original.length()) * 100;

						if(similitud <= Main4.PORCENTAJE_TOLERANCIA && other.getFound() == null) 
							this.addCoincidence(other, coincidences, (int) distance, (100 - similitud), "By levenshtein");
					}
				}

			}

			if(coincidences.size() > 1) 
				FileUtil.write(coincidences);

			Main4.removeData(-1, coincidences, this);
		}
		catch(Exception e) {
			e.printStackTrace();
			System.out.println("record: " + this.record.toString() + " - " + e.toString());
		}
	}
}