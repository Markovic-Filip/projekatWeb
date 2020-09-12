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
import enums.StatusKorisnika;
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
			System.out.println("KorisniciDAO: Korisnik " + noviKorisnik.getKorisnickoIme() + " dodat u sistem.\r\n");
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
	
	// Toggle-uje status korisnika i azurira bazu
	public void promeniStatusKorisnika(String korisnickoIme)	{
		Korisnik korisnik = korisnici.get(korisnickoIme);
		switch (korisnik.getUloga())	{
			case GOST:
				Gost gost = (Gost) korisnik;
				gost.setStatus(gost.getStatus().equals(StatusKorisnika.AKTIVAN) ? StatusKorisnika.BLOKIRAN : StatusKorisnika.AKTIVAN);
				break;
			case DOMACIN:
				Domacin domacin = (Domacin) korisnik;
				domacin.setStatus(domacin.getStatus().equals(StatusKorisnika.AKTIVAN) ? StatusKorisnika.BLOKIRAN : StatusKorisnika.AKTIVAN);
				break;
			default:
				break;
		}
		
		azurirajBazu(korisnik.getUloga());
	}
	
	public void dodajRezervaciju(String korisnickoIme, int idRezervacije)	{
		String putanja = "./static/baza/korisnici/" + korisnickoIme + "-Rezervacije.txt";
		
		Gost gost = (Gost) korisnici.get(korisnickoIme);
		gost.getRezervacije().add(idRezervacije);
		
		try {
			FileWriter writer = new FileWriter(putanja);
			writer.append(idRezervacije + "\n");
			System.out.println("KORISNICI DAO: Rezervacija " + idRezervacije + " dodata u " + korisnickoIme + "-Rezervacije.txt\r\n");
			writer.close();
		} catch (IOException e)	{
			e.printStackTrace();
			System.out.println("Fajl " + putanja + " nije pronadjen!\r\n");
		}
	}
	
	public void dodajApartman(String korisnickoIme, int idApartmana)	{
		String putanja = "./static/baza/korisnici/" + korisnickoIme + "-Apartmani.txt";
		
		Domacin domacin = (Domacin) korisnici.get(korisnickoIme);
		domacin.getApartmani().add(idApartmana);
		
		try {
			FileWriter writer = new FileWriter(putanja);
			writer.append(idApartmana + "\n");
			System.out.println("KORISNICI DAO: Apartman " + idApartmana + " dodat u " + korisnickoIme + "-Rezervacije.txt\r\n");
			writer.close();
		} catch (IOException e)	{
			e.printStackTrace();
			System.out.println("Fajl " + putanja + " nije pronadjen!\r\n");
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
					Gost gost = new Gost(tokeni[0], tokeni[1], tokeni[2], tokeni[3], Pol.valueOf(tokeni[4]), StatusKorisnika.valueOf(tokeni[5]));
					ucitajApartmaneIRezervacije(gost);
					korisnici.put(tokeni[0], gost);
				} else if (uloga == Uloga.DOMACIN)	{
					Domacin domacin = new Domacin(tokeni[0], tokeni[1], tokeni[2], tokeni[3], Pol.valueOf(tokeni[4]), StatusKorisnika.valueOf(tokeni[5]));
					ucitajApartmane(domacin);
					korisnici.put(tokeni[0], domacin);
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
	
	private void ucitajApartmane(Domacin domacin) {
		String putanja = "./static/baza/korisnici/" + domacin.getKorisnickoIme() + "-Apartmani.txt";
		BufferedReader bafer;
		try	{
			bafer = new BufferedReader(new FileReader(putanja));
			String red;
			while ((red = bafer.readLine()) != null)	{
				domacin.getApartmani().add(Integer.parseInt(red));
			}
			bafer.close();
		} catch (Exception e)	{
			e.printStackTrace();
			System.out.println("Fajl " + putanja + " nije pronadjen.\r\n");
		}
	}

	private void ucitajApartmaneIRezervacije(Gost gost) {
		String putanja = "./static/baza/korisnici/" + gost.getKorisnickoIme() + "-IznajmljeniApartmani.txt";
		BufferedReader bafer;
		try	{
			bafer = new BufferedReader(new FileReader(putanja));
			String red;
			while ((red = bafer.readLine()) != null)	{
				gost.getIznajmljeniApartmani().add(Integer.parseInt(red));
			}
			bafer.close();
		} catch (Exception e)	{
			e.printStackTrace();
			System.out.println("KorisniciDAO: Fajl " + putanja + " nije pronadjen.\r\n");
		}
		
		putanja = "./static/baza/korisnici/" + gost.getKorisnickoIme() + "-Rezervacije.txt";
		try	{
			bafer = new BufferedReader(new FileReader(putanja));
			String red;
			while ((red = bafer.readLine()) != null)	{
				gost.getRezervacije().add(Integer.parseInt(red));
			}
			bafer.close();
		} catch (Exception e)	{
			e.printStackTrace();
			System.out.println("KorisniciDAO: Fajl " + putanja + " nije pronadjen.\r\n");
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
