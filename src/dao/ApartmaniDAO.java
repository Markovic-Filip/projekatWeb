package dao;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import beans.Adresa;
import beans.Apartman;

import beans.Lokacija;

import enums.Status;
import enums.Tip;

public class ApartmaniDAO {
	private HashMap<Integer, Apartman> apartmani;

	private int idApartmani;

	
	public ApartmaniDAO(String putanja) {
		apartmani = new HashMap<Integer, Apartman>();
		
		ucitajApartmane(putanja);
		ucitajApartmaniBrojac();
	
		
	}

	public HashMap<Integer, Apartman> mapaApartmana()	{
		return apartmani;
	}

	
	public ArrayList<Apartman> sviApartmani()	{
		ArrayList<Apartman> retVal = new ArrayList<Apartman>();
		
		for (Apartman apartman : apartmani.values())	{
			retVal.add(apartman);
		}
		
		return retVal;
	}
	
	
	
	public void dodajNoviApartman(Apartman noviApartman) {
		noviApartman.setId(idApartmani);
		noviApartman.setStatus(Status.NEAKTIVNO);
		apartmani.put(idApartmani, noviApartman);
		inkrementirajApartmaniBrojac();
		upisiNoviApartman(noviApartman);
	}
	
	public boolean obrisiApartman(int id)	{
		Apartman obrisaniApartman =  apartmani.remove(id);
		if (obrisaniApartman != null) {
			System.out.println("ApartmaniDAO: Apartman " + obrisaniApartman.getId() + " uspesno obrisan iz base.\r\n");
			azurirajBazu();
			return true;
		} else	{
			System.out.println("ApartmaniDAO: " + id + " nije pronadjen u bazi.\r\n");
			return false;
		}
	}
	
	
	public ArrayList<Apartman> dobaviAktivneApartmane()	{
		ArrayList<Apartman> retVal = new ArrayList<Apartman>();

		for (Apartman apartman : apartmani.values())	{
			if (apartman.getStatus().equals(Status.AKTIVNO)) {
				retVal.add(apartman);
			}
		}

		return retVal;
	}

	public ArrayList<Apartman> dobaviNeaktivneApartmane()	{
		ArrayList<Apartman> retVal = new ArrayList<Apartman>();

		for (Apartman apartman : apartmani.values())	{
			if (apartman.getStatus().equals(Status.NEAKTIVNO)) {
				retVal.add(apartman);
			}
		}

		return retVal;
	}

	public ArrayList<Apartman> dobaviAktivneApartmaneZaDomacina(String korisnickoIme)	{
		ArrayList<Apartman> retVal = new ArrayList<Apartman>();

		for (Apartman apartman : apartmani.values())	{
			if(apartman.getKorisnickoImeDomacina().equals(korisnickoIme)) {
				if (apartman.getStatus().equals(Status.AKTIVNO)) {
					retVal.add(apartman);
				}
			}
		}

		return retVal;
	}

	public ArrayList<Apartman> dobaviNeaktivneApartmaneZaDomacina(String korisnickoIme)	{
		ArrayList<Apartman> retVal = new ArrayList<Apartman>();

		for (Apartman apartman : apartmani.values())	{
			if(apartman.getKorisnickoImeDomacina().equals(korisnickoIme)) {
				if (apartman.getStatus().equals(Status.NEAKTIVNO)) {
					retVal.add(apartman);
				}
			}
		}

		return retVal;
	}
	
	public void promeniStatusApartmana(int id, Status noviStatus)	{
		apartmani.get(id).setStatus(noviStatus);
		azurirajBazu();
	}

	public boolean izmeniApartman(Apartman izmenjenApartman)	{
		if (apartmani.containsKey(izmenjenApartman.getId()))	{
			apartmani.put(izmenjenApartman.getId(), izmenjenApartman);
			azurirajBazu();
			return true;
		} else	{
			System.out.println("APARTMANI DAO: Apartman id:" + izmenjenApartman.getId() + " nije pronadjen u bazi.\r\n");
			return false;
		}
	}
	
	private void upisiNoviApartman(Apartman novApartman) {
		String putanja = "./static/baza/apartmani.txt";
		try {
			FileWriter writer = new FileWriter(putanja, true);
			writer.append(novApartman.toString());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Fajl " + putanja + " nije pronadjen!\r\n");
		}
	}
	
	
	
	// TODO: thread koji poziva azuriranje na svakih 2min npr. ili posebna funkcija koja prima novi status i id rezervacije i menja status pa poziva azuriranje
	private void azurirajBazu()	{
		String putanja = "./static/baza/apartmani.txt";
		try {
			FileWriter writer = new FileWriter(putanja, false);
			for (Apartman apartman : apartmani.values()) {
				writer.write(apartman.toString());
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Fajl " + putanja + " nije pronadjen!\r\n");
		}
	}
	

	private void ucitajApartmane(String putanja) {
		BufferedReader bafer;
		try	{
			bafer = new BufferedReader(new FileReader(putanja));
			String red;
			while ((red = bafer.readLine()) != null)	{
				String[] tokeni = red.split(";");
				int id = Integer.parseInt(tokeni[0]);
				double geografskaSirina = Double.parseDouble(tokeni[5]);
				double geografskaDuzina = Double.parseDouble(tokeni[6]);
				String[] adresa = tokeni[4].split(",");
				String ulica = adresa[0];
				int broj = Integer.parseInt(adresa[1]);
				String mesto = adresa[2];
				int postanskiBroj = Integer.parseInt(adresa[3]);
				Adresa adresaApartmana = new Adresa(ulica,broj,mesto,postanskiBroj);
				Lokacija lokacija = new Lokacija(geografskaSirina,geografskaDuzina,adresaApartmana);
				ArrayList<Integer> idSadrzaja = new ArrayList<Integer>();
				ArrayList<Integer> idRezervacije = new ArrayList<Integer>();
				
				// TODO: Moze se desiti da apartman nema sadrzaj i rezervacije, treba prvo proveriti da li ima pa onda nastaviti s ovim kodom ispod
				if (tokeni.length >= 13)	{
					String sadrzaj = tokeni[12];
					String[] sadrzaji = sadrzaj.split(",");
					for (String s : sadrzaji) {
						idSadrzaja.add(Integer.parseInt(s));
					}
				}
				
				if (tokeni.length >= 14)	{
					String rezervacija = tokeni[13];
					if(rezervacija.length()>1) {
						String[] rezrevacije = rezervacija.split(",");
						for (String r : rezrevacije) {
							idRezervacije.add(Integer.parseInt(r));
						}	
					}else {
						idRezervacije.add(Integer.parseInt(rezervacija));
					}
				}
				
				apartmani.put(id, new Apartman(id, Tip.valueOf(tokeni[1]), Integer.parseInt(tokeni[2]), Integer.parseInt(tokeni[3]),  lokacija, tokeni[7], Double.parseDouble(tokeni[8]), Integer.parseInt(tokeni[9]), Integer.parseInt(tokeni[10]), Status.valueOf(tokeni[11]),idSadrzaja,idRezervacije ));
				System.out.println("ApartmaniDAO: " + apartmani.get(id).toString() + "\r\n");
			}
			
			bafer.close();
		} catch (Exception e)	{
			e.printStackTrace();
			System.out.println("Fajl " + putanja + " nije pronadjen.\r\n");
		}
	}
	
	
	private void ucitajApartmaniBrojac() {
		String putanja = "./static/baza/idApartmani.txt";
		BufferedReader bafer;
		try	{
			bafer = new BufferedReader(new FileReader(putanja));
			String red;
			if ((red = bafer.readLine()) != null)	{
				idApartmani = Integer.parseInt(red);
			}
			bafer.close();
		} catch (Exception e)	{
			e.printStackTrace();
			System.out.println("Fajl " + putanja + " nije pronadjen.\r\n");
		}
	}
	
	private void inkrementirajApartmaniBrojac() {
		idApartmani++;
		String putanja = "./static/baza/idApartmani.txt";
		try {
			FileWriter writer = new FileWriter(putanja, false);
			writer.write(idApartmani + "\r\n");
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Fajl " + putanja + " nije pronadjen.\r\n");
		}
	}
	
	
}
