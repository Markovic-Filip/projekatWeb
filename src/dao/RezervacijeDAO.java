package dao;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import beans.Rezervacija;
import enums.StatusRezervacije;

public class RezervacijeDAO {

	private HashMap<Integer, Rezervacija> rezervacije;
	private int idBrojac;
	
	public RezervacijeDAO(String putanja) {
		rezervacije = new HashMap<Integer, Rezervacija>();
		
		ucitajRezervacije(putanja);
		ucitajIdBrojac();
	}

	public HashMap<Integer, Rezervacija> mapaRezervacija()	{
		return rezervacije;
	}
	
	public ArrayList<Rezervacija> sveRezervacije()	{
		ArrayList<Rezervacija> retVal = new ArrayList<Rezervacija>();
		
		for (Rezervacija rezervacija : rezervacije.values())	{
			retVal.add(rezervacija);
		}
		
		return retVal;
	}
	
	// TODO: Proveri da li su datumi ispravni (slobodni) pa kreiraj rezervaciju i stavi u mapu
	public boolean dodajNovuRezervaciju(Rezervacija novaRezervacija) {
		upisiNovuRezervaciju(novaRezervacija);
		inkrementirajIdBrojac();
		return false;
	}

	private void upisiNovuRezervaciju(Rezervacija novaRezervacija) {
		String putanja = "./static/baza/rezervacije.txt";
		
		try {
			FileWriter writer = new FileWriter(putanja, true);
			writer.append(novaRezervacija.toString());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Fajl " + putanja + " nije pronadjen!\r\n");
		}
	}

	private void ucitajRezervacije(String putanja) {
		BufferedReader bafer;
		try	{
			bafer = new BufferedReader(new FileReader(putanja));
			String red;
			while ((red = bafer.readLine()) != null)	{
				String[] tokeni = red.split(";");
				int id = Integer.parseInt(tokeni[0]);
				rezervacije.put(id, new Rezervacija(id, Integer.parseInt(tokeni[1]), new Date(Long.parseLong(tokeni[2])), Integer.parseInt(tokeni[3]), Double.parseDouble(tokeni[4]), tokeni[5], tokeni[6], StatusRezervacije.valueOf(tokeni[7])));
				System.out.println("RezervacijeDAO: " + rezervacije.get(id).toString() + "\r\n");
			}
			
			bafer.close();
		} catch (Exception e)	{
			e.printStackTrace();
			System.out.println("Fajl " + putanja + " nije pronadjen.\r\n");
		}
	}
	
	private void ucitajIdBrojac() {
		String putanja = "./static/baza/idBrojac.txt";
		BufferedReader bafer;
		try	{
			bafer = new BufferedReader(new FileReader(putanja));
			String red;
			if ((red = bafer.readLine()) != null)	{
				idBrojac = Integer.parseInt(red);
			}
			bafer.close();
		} catch (Exception e)	{
			e.printStackTrace();
			System.out.println("Fajl " + putanja + " nije pronadjen.\r\n");
		}
	}
	
	private void inkrementirajIdBrojac() {
		idBrojac++;
		String putanja = "./static/baza/idBrojac.txt";
		try {
			FileWriter writer = new FileWriter(putanja, false);
			writer.write(idBrojac + "\r\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
