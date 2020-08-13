package dao;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import beans.Administrator;
import beans.Domacin;
import beans.Gost;
import beans.Korisnik;
import enums.Pol;
import enums.Uloga;

public class KorisniciDAO {

	private HashMap<String, Korisnik> korisnici;
	
	public KorisniciDAO(String putanja)	{
		korisnici = new HashMap<String, Korisnik>();

		ucitajKorisnike(putanja + "gosti.txt", Uloga.GOST);
		ucitajKorisnike(putanja + "domacini.txt", Uloga.DOMACIN);
		ucitajKorisnike(putanja + "admini.txt", Uloga.ADMINISTRATOR);
	}
	
	public HashMap<String, Korisnik> mapaKorisnika()	{
		return korisnici;
	}
	
	public ArrayList<Korisnik> sviKorisnici()	{
		ArrayList<Korisnik> retVal = new ArrayList<Korisnik>();
		for (Korisnik korisnik : korisnici.values())	{
			retVal.add(korisnik);
		}
		//return (ArrayList<Korisnik>) korisnici.values();
		return retVal;
	}
	
	// TODO: Ovo uloga je mozda suvisno, ne secam se zasto sam stavio
	public boolean dodajNovogKorisnika(Korisnik noviKorisnik, Uloga uloga)	{
		if (!korisnici.containsKey(noviKorisnik.getKorisnickoIme()))	{
			korisnici.put(noviKorisnik.getKorisnickoIme(), noviKorisnik);
			upisiNovogKorisnika(noviKorisnik);
			return true;
		}
		else	{
			System.out.println("KorisniciDAO: Korisnicko ime " + noviKorisnik.getKorisnickoIme() + " je vec zauzeto.\r\n");
			return false;
		}
	}
	
	public Korisnik dobaviKorisnika(String korisnickoIme)	{
		return korisnici.get(korisnickoIme);
	}
	
	public boolean obrisiKorisnika(String korisnickoIme)	{
		Korisnik obrisaniKorisnik =  korisnici.remove(korisnickoIme);
		if (obrisaniKorisnik != null) {
			System.out.println("KorisniciDAO: Korisnik " + obrisaniKorisnik.getKorisnickoIme() + " uspesno obrisan iz base.\r\n");
			azurirajBazu(obrisaniKorisnik.getUloga());
			return true;
		} else	{
			System.out.println("KorisniciDAO: " + korisnickoIme + " nije pronadjen u bazi.\r\n");
			return false;
		}
	}
	
	private void ucitajKorisnike(String putanja, Uloga uloga)	{
		BufferedReader bafer;
		try	{
			bafer = new BufferedReader(new FileReader(putanja));
			String red;
			while ((red = bafer.readLine()) != null)	{
				String[] tokeni = red.split(";");
				if (uloga == Uloga.GOST)	{
					korisnici.put(tokeni[0], new Gost(tokeni[0], tokeni[1], tokeni[2], tokeni[3], Pol.valueOf(tokeni[4])));
				} else if (uloga == Uloga.DOMACIN)	{
					korisnici.put(tokeni[0], new Domacin(tokeni[0], tokeni[1], tokeni[2], tokeni[3], Pol.valueOf(tokeni[4])));
				} else if (uloga == Uloga.ADMINISTRATOR)	{
					korisnici.put(tokeni[0], new Administrator(tokeni[0], tokeni[1], tokeni[2], tokeni[3], Pol.valueOf(tokeni[4])));
				}
				System.out.println("KorisniciDAO: " + korisnici.get(tokeni[0]).toString() + "\r\n");
			}
			
			bafer.close();
		} catch (Exception e)	{
			e.printStackTrace();
			System.out.println("Fajl " + putanja + " nije pronadjen.\r\n");
		}
	}
	
	private void upisiNovogKorisnika(Korisnik noviKorisnik) {
		String putanja = napraviPutanju(noviKorisnik.getUloga());
		
		try {
			FileWriter writer = new FileWriter(putanja, true);
			writer.append(noviKorisnik.toString());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Fajl " + putanja + " nije pronadjen!\r\n");
		}
	}
	
	private void azurirajBazu(Uloga uloga) {
		String putanja = napraviPutanju(uloga);
		
		// TODO: obrisati korisnika iz spiska korisnika
		try {
			FileWriter writer = new FileWriter(putanja, false);
			for (Korisnik korisnik : korisnici.values())	{
				if (korisnik.getUloga().equals(uloga))	{
					writer.write(korisnik.toString());
				}
			}
			writer.close();
		} catch (IOException e)	{
			e.printStackTrace();
			System.out.println("Fajl " + putanja + " nije pronadjen!\r\n");
		}
	}
	
	private String napraviPutanju(Uloga uloga)	{
		String putanja = "./static/baza/";
		switch(uloga)	{
		case ADMINISTRATOR:
			putanja += "admini.txt";
			break;
		case DOMACIN:
			putanja += "domacini.txt";
			break;
		case GOST:
			putanja += "gosti.txt";
			break;
		}
		
		return putanja;
	}
}
