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
	
	public boolean dodajNovuRezervaciju(Rezervacija novaRezervacija) {
		boolean retVal = true;
		
		// Prolaz kroz sve rezervacije u sistemu
		for (Rezervacija rezervacija : rezervacije.values()) {
			// Ako se trenutna rezervacija odnosi na isti apartman koji trenutno zelim da rezervisem 
			if (rezervacija.getApartmanId() == novaRezervacija.getApartmanId())	{
				// Ako je pocetni datum trenutne rezervacije posle pocetnog datuma nove rezervacije  
				if (rezervacija.getPocetniDatum().compareTo(novaRezervacija.getPocetniDatum()) > 0)	{
					// Ako je pocetni datum trenutne rezervacije posle krajnjeg datuma nove rezervacije -> ne preklapaju se
					if (rezervacija.getPocetniDatum().compareTo(novaRezervacija.getKrajnjiDatum()) >= 0)	{
						retVal = true;
					// Inace se preklapaju stoga rezervacija nije validna
					} else	{
						retVal = false;
						break;
					}
				// Ako je pocetni datum trenutne rezervacije pre pocetnog datuma nove rezervacije
				} else if (rezervacija.getPocetniDatum().compareTo(novaRezervacija.getPocetniDatum()) < 0)	{
					// Ako je krajnji datum trenutne rezervacije pre pocetnog datuma nove rezervacije -> nepreklapaju se
					if (rezervacija.getKrajnjiDatum().compareTo(novaRezervacija.getPocetniDatum()) <= 0)	{
						retVal = true;
					// Inace se preklapaju stoga rezervacija nije validna
					} else	{
						retVal = false;
						break;
					}
				// Inace se rezervacije odnose na isti datum -> preklapaju se
				} else	{
					retVal = false;
					break;
				}
			}
		}
		
		if (retVal) {
			novaRezervacija.setId(idBrojac);
			novaRezervacija.setStatus(StatusRezervacije.KREIRANA);
			rezervacije.put(idBrojac, novaRezervacija);
			System.out.println("RezervacijeDAO: Nova rezervacija id:" + novaRezervacija.getId() + " kreirana.\r\n");
			inkrementirajIdBrojac();
			upisiNovuRezervaciju(novaRezervacija);
		}
		
		return retVal;
	}
	
	public void promeniStatusRezervacije(int id, StatusRezervacije noviStatus)	{
		rezervacije.get(id).setStatus(noviStatus);
		azurirajBazu();
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
	
	private void azurirajBazu()	{
		String putanja = "./static/baza/rezervacije.txt";
		try {
			FileWriter writer = new FileWriter(putanja, false);
			for (Rezervacija rezervacija : rezervacije.values()) {
				writer.write(rezervacija.toString());
			}
			writer.close();
		} catch (Exception e) {
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
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Fajl " + putanja + " nije pronadjen.\r\n");
		}
	}
}
