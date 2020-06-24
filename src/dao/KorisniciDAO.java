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
		return (ArrayList<Korisnik>) korisnici.values();
	}
	
	public boolean dodajNovogKorisnika(Korisnik noviKorisnik, Uloga uloga)	{
		if (!korisnici.containsKey(noviKorisnik.getKorisnickoIme()))	{
			korisnici.put(noviKorisnik.getKorisnickoIme(), noviKorisnik);
			upisiNovogKorisnika(noviKorisnik);
			return true;
		}
		else	{
			System.out.println("Korisnicko ime " + noviKorisnik.getKorisnickoIme() + " je vec zauzeto.");
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
				// TODO: dijagnostika, moze se obrisati kasnije
				System.out.println(korisnici.get(tokeni[0]).toString());
			}
		} catch (Exception e)	{
			e.printStackTrace();
			System.out.println("Fajl " + putanja + " nije pronadjen.");
		}
	}
	
	private void upisiNovogKorisnika(Korisnik noviKorisnik) {
		String putanja = "./static/baza/";
		switch (noviKorisnik.getUloga()) {
		case DOMACIN:
			putanja += "domacini.txt";
			break;
		case GOST:
			putanja += "gosti.txt";
			break;
		default:
			break;
		}
		
		try {
			FileWriter writer = new FileWriter(putanja, true);
			writer.append(noviKorisnik.toString());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Fajl " + putanja + " nije pronadjen!");
		}
	}
}
