package dao;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import beans.Gost;
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
			// Ako je rezervacija kreirana/prihvacena (ako je odbijena, zavrsena ili je odustano od nje, ti datumi nisu zauzeti)
			if (rezervacija.getStatus().equals(StatusRezervacije.KREIRANA) || rezervacija.getStatus().equals(StatusRezervacije.PRIHVACENA))	{
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
	
	public ArrayList<Rezervacija> sveRezervacijeGosta(ArrayList<Integer> brojevi)	{
		ArrayList<Rezervacija> retVal = new ArrayList<Rezervacija>();
			for (Rezervacija rezervacija : rezervacije.values())	{
				for(int i : brojevi) {
					if(rezervacija.getId() == i) {
						if(!retVal.contains(rezervacija)) {
							retVal.add(rezervacija);
						}
				}
			}

		}

		return retVal;
	}
	
	public ArrayList<Rezervacija> sveRezervacijeDomacin(ArrayList<Integer> brojevi)	{
		ArrayList<Rezervacija> retVal = new ArrayList<Rezervacija>();
			for (Rezervacija rezervacija : rezervacije.values())	{
				for(int i : brojevi) {
					if(rezervacija.getApartmanId() == i) {
						if(!retVal.contains(rezervacija)) {
							retVal.add(rezervacija);
						}
				}
			}

		}

		return retVal;
	}
	
	public ArrayList<Date> zauzetiDatumiApartmana(int id){
		ArrayList<Date> retVal = new ArrayList<Date>(); 
		for(Rezervacija rezervacija : rezervacije.values()) {
			if(rezervacija.getApartmanId()==id && (rezervacija.getStatus().equals(StatusRezervacije.KREIRANA) || rezervacija.getStatus().equals(StatusRezervacije.PRIHVACENA))) {
				for(int i=0; i<rezervacija.getBrojNocenja();i++) {
					retVal.add(new Date(rezervacija.getPocetniDatum().getTime()+i*86400000));
				}
			}
		}
		return retVal;
	}
	
	public ArrayList<String> sveKorisniciDomacin(ArrayList<Integer> brojevi)	{
		ArrayList<Rezervacija> retVal = new ArrayList<Rezervacija>();
			for (Rezervacija rezervacija : rezervacije.values())	{
				for(int i : brojevi) {
					if(rezervacija.getApartmanId() == i) {
						if(!retVal.contains(rezervacija)) {
							retVal.add(rezervacija);
						}
				}
			}

		}
		ArrayList<String> ret = new ArrayList<String>();
		for(Rezervacija r : retVal) {
			if(!ret.contains(r.getKorisnickoImeGosta())) {
				ret.add(r.getKorisnickoImeGosta());
			}	
		}

		return ret;
	}

	public ArrayList<Integer> sviApartmaniGosta(Gost gost){
		ArrayList<Integer> retVal = new ArrayList<Integer>();
		for(Rezervacija rezervacija : rezervacije.values()) {
			for(int i : gost.getRezervacije()) {
				if(i==rezervacija.getId() && (rezervacija.getStatus()==StatusRezervacije.ODBIJENA || rezervacija.getStatus()==StatusRezervacije.ZAVRSENA) ) {
					if(!retVal.contains(rezervacija.getApartmanId())) {
						retVal.add(rezervacija.getApartmanId());
					}
				}
			}
		}
		return retVal;
	}
	
	/*
	public void promeniStatusRezervacije(int id, StatusRezervacije noviStatus)	{
		rezervacije.get(id).setStatus(noviStatus);
		azurirajBazu();
	}*/
	
	public boolean promeniStatusRezervacije(int id, StatusRezervacije noviStatus)	{
		if(rezervacije.containsKey(id)) {
			rezervacije.get(id).setStatus(noviStatus);
			azurirajBazu();
			return true;
		}else{
			System.out.println("RezervacijeDAO: Ne postoji rezervacija id:" + id + "\r\n");
			return false;

		}
	}
	
	public void obrisiRezervacijeGosta(String korisnickoIme)	{
		ArrayList<Integer> zaBrisanje = new ArrayList<Integer>();
		
		for (Rezervacija rezervacija : rezervacije.values())	{
			if (rezervacija.getKorisnickoImeGosta().equals(korisnickoIme))	{
				zaBrisanje.add(rezervacija.getId());
				System.out.println("RezervacijeDAO: Rezervacija id:" + rezervacija.getId() + " obrisana.\r\n");
			}
		}
		
		if (!zaBrisanje.isEmpty())	{
			for (int i : zaBrisanje)	{
				rezervacije.remove(i);
			}
			
			azurirajBazu();
		}
	}
	
	public void odbijRezervacijeZaApartmane(ArrayList<Integer> idApartmana)	{
		for (Rezervacija rezervacija : rezervacije.values())	{
			if (idApartmana.contains(rezervacija.getApartmanId()))	{
				if (!rezervacija.getStatus().equals(StatusRezervacije.ZAVRSENA) && !rezervacija.getStatus().equals(StatusRezervacije.ODUSTANAK))	{
					rezervacija.setStatus(StatusRezervacije.ODBIJENA);
					System.out.println("RezervacijeDAO: Rezervacija id:" + rezervacija.getId() + " novi status: " + rezervacija.getStatus().name() + "\r\n");
				}
			}
		}
		
		azurirajBazu();
	}
	
	public void odustaniOdRezervacija(String korisnickoIme)	{
		for (Rezervacija rezervacija : rezervacije.values()) {
			if (rezervacija.getKorisnickoImeGosta().equals(korisnickoIme))	{
				if (!rezervacija.getStatus().equals(StatusRezervacije.ZAVRSENA) && !rezervacija.getStatus().equals(StatusRezervacije.ODBIJENA)) {
					rezervacija.setStatus(StatusRezervacije.ODUSTANAK);
					System.out.println("RezervacijeDAO: Rezervacija id:" + rezervacija.getId() + " novi status: " + rezervacija.getStatus().name() + "\r\n");
				}
			}
		}
		
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
