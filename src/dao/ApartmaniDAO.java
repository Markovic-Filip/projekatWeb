package dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
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
		System.out.println("ApartmaniDAO: Apartman id:" + id + " novi status: " + noviStatus.name() + "\r\n");
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
	
	public ArrayList<Integer> obrisiApartmaneDomacina(String korisnickoIme)	{
		ArrayList<Integer> zaBrisanje = new ArrayList<Integer>();
		for (Apartman apartman : apartmani.values())	{
			if (apartman.getKorisnickoImeDomacina().equals(korisnickoIme)) {
				zaBrisanje.add(apartman.getId());
				System.out.println("ApartmaniDAO: Apartman id:" + apartman.getId() + " obrisan.\r\n");
			}
		}
		
		if (!zaBrisanje.isEmpty()) {
			for (int i : zaBrisanje)	{
				apartmani.remove(i);
			}
			
			azurirajBazu();
		}
		
		return zaBrisanje;
	}
	
	public ArrayList<Apartman> apartmaniGdeJeGostBio(ArrayList<Integer> lista){
		ArrayList<Apartman> retVal = new ArrayList<Apartman>();
		for(Apartman apartman : apartmani.values()) {
			for(int i : lista) {
				if(apartman.getId() == i && !retVal.contains(apartman)) {
					retVal.add(apartman);
				}
			}
		}
		return retVal;
	}
	
	public void dodajSliku(int idApartmana, String imeSlike)	{
		apartmani.get(idApartmana).getImenaSlika().add(imeSlike);
		apartmani.get(idApartmana).getSlike().add(ucitajSliku("./static/slike/" + imeSlike));
		azurirajBazu();
	}
	
	public Apartman dobaviApartman(int idApartmana)	{
		return apartmani.get(idApartmana);
	}
	
	public ArrayList<Integer> deaktivirajApartmaneDomacina(String korisnickoIme)	{
		ArrayList<Integer> retVal = new ArrayList<Integer>();
		
		for (Apartman apartman : apartmani.values())	{
			if (apartman.getKorisnickoImeDomacina().equals(korisnickoIme))	{
				apartman.setStatus(Status.NEAKTIVNO);
				System.out.println("ApartmaniDAO: Apartman id:" + apartman.getId() + " novi status: " + apartman.getStatus().name() + "\r\n");
				retVal.add(apartman.getId());
			}
		}
		
		azurirajBazu();
		
		return retVal;
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
				
				String sadrzaj = tokeni[12];
				String[] sadrzaji = sadrzaj.split(",");
				if (sadrzaji[0].equals("sadrzaji"))	{
					for (int i = 1; i < sadrzaji.length; i++) {
						idSadrzaja.add(Integer.parseInt(sadrzaji[i]));
					}
				}
				
				String rezervacija = tokeni[13];
				String[] rezervacije = rezervacija.split(",");
				if (rezervacije[0].equals("rezervacije"))	{
					for (int i = 1; i < rezervacije.length; i++) {
						idRezervacije.add(Integer.parseInt(rezervacije[i]));
					}	
				}
				
				ArrayList<String> imenaSlika = new ArrayList<String>();
				ArrayList<String> slike = new ArrayList<String>();
				String slika = tokeni[14];
				String[] slikePutanja = slika.split(",");
				if (slikePutanja[0].equals("slike"))	{
					for (int i = 1; i < slikePutanja.length; i++)	{
						slike.add(ucitajSliku("./static/slike/" + slikePutanja[i]));
						imenaSlika.add(slikePutanja[i]);
					}
				}
				// TODO: ubaciti slike u konstruktor
				apartmani.put(id, new Apartman(id, Tip.valueOf(tokeni[1]), Integer.parseInt(tokeni[2]), Integer.parseInt(tokeni[3]),  lokacija, tokeni[7], Double.parseDouble(tokeni[8]), Integer.parseInt(tokeni[9]), Integer.parseInt(tokeni[10]), Status.valueOf(tokeni[11]),idSadrzaja,idRezervacije, imenaSlika, slike));
				//apartmani.put(id, new Apartman(id, Tip.valueOf(tokeni[1]), Integer.parseInt(tokeni[2]), Integer.parseInt(tokeni[3]),  lokacija, tokeni[7], Double.parseDouble(tokeni[8]), Integer.parseInt(tokeni[9]), Integer.parseInt(tokeni[10]), Status.valueOf(tokeni[11]),idSadrzaja,idRezervacije ));
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
	
	private String ucitajSliku(String putanja)	{
		try {
			//File file = new ClassPathResource(putanja).getFile();
			File file = new File(putanja);
	    
			String encodeImage = Base64.getEncoder().withoutPadding().encodeToString(Files.readAllBytes(file.toPath()));
			
			return encodeImage;
		} catch (Exception e) {
			System.out.println("ApartmaniDAO: Fajl " + putanja + " nije pronadjen.\r\n");
			return null;
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
