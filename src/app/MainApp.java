package app;

import static spark.Spark.port;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.staticFiles;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import com.google.gson.Gson;

import beans.Administrator;
import beans.Domacin;
import beans.Gost;
import beans.Korisnik;
import beans.Odgovor;
import enums.Pol;
import enums.Uloga;

public class MainApp {

	private static Gson gson = new Gson();
	private static HashMap<String, Korisnik> korisnici = new HashMap<String, Korisnik>();
	
	public static void main(String[] args) throws IOException {

		// Inicijalizacija
		
		port(8080);
		
		staticFiles.externalLocation(new File("./static").getCanonicalPath());
		
		ucitajKorisnike("./static/baza/gosti.txt", Uloga.GOST);
		ucitajKorisnike("./static/baza/domacini.txt", Uloga.DOMACIN);
		ucitajKorisnike("./static/baza/admini.txt", Uloga.ADMINISTRATOR);
		
		// Obrada HTTP zahteva
		
		post("/app/registracija/domacin", (req, res) ->	{
			res.type("application/json");
			String payload = req.body();
			Domacin noviDomacin = gson.fromJson(payload, Domacin.class);
			if (noviDomacin != null)	{
				System.out.println(noviDomacin.getIme() + ", " + noviDomacin.getKorisnickoIme());
				if (dodajNovogKorisnika(noviDomacin))	{
					System.out.println("Domacin " + noviDomacin.getKorisnickoIme() + " uspesno registrovan.");
					return gson.toJson(noviDomacin);
				} else	{
					System.out.println("Korisničko ime " + noviDomacin.getKorisnickoIme() + " je zauzeto. Pokušajte ponovo.");
					return gson.toJson(new Odgovor("Korisničko ime " + noviDomacin.getKorisnickoIme() + " je zauzeto. Pokušajte ponovo."));
				}
			} else	{
				System.out.println("Objekat korisnika ne moze da se kreira.");
				// TODO: Ovo treba nekako handleovati
				// Error 500: Internal Server Error - iz nekog razloga ne moze da parsira JSON objekat
				res.status(500);
				return null;
			}
		});
		
		post("/app/registracija/gost", (req, res) -> {
			res.type("application/json");
			String payload = req.body();
			Gost noviGost = gson.fromJson(payload, Gost.class);
			if (noviGost != null)	{
				System.out.println(noviGost.getIme() + ", " + noviGost.getKorisnickoIme());
				if (dodajNovogKorisnika(noviGost))	{
					System.out.println("Gost " + noviGost.getKorisnickoIme() + " uspesno registrovan.");
					return gson.toJson(noviGost);
				} else	{
					System.out.println("Korisničko ime " + noviGost.getKorisnickoIme() + " je zauzeto. Pokušajte ponovo.");
					return gson.toJson(new Odgovor("Korisničko ime " + noviGost.getKorisnickoIme() + " je zauzeto. Pokušajte ponovo."));
				}
			} else	{
				System.out.println("Objekat korisnika ne moze da se kreira.");
				// TODO: Ovo treba nekako handleovati
				// Error 500: Internal Server Error - iz nekog razloga ne moze da parsira JSON objekat
				res.status(500);
				return null;
			}
		});
	}

	private static boolean dodajNovogKorisnika(Korisnik noviKorisnik)	{
		
		if (!korisnici.containsKey(noviKorisnik.getKorisnickoIme()))	{
			korisnici.put(noviKorisnik.getKorisnickoIme(), noviKorisnik);
			return true;
		}
		else	{
			System.out.println("Korisnicko ime " + noviKorisnik.getKorisnickoIme() + " je vec zauzeto.");
			return false;
		}
	}
	
	private static void ucitajKorisnike(String putanja, Uloga uloga)	{
		BufferedReader br;
		try	{
			br = new BufferedReader(new FileReader(putanja));
			String red;
			while ((red = br.readLine()) != null)	{
				String[] tokeni = red.split(";");
				if (uloga == Uloga.GOST)	{
					korisnici.put(tokeni[0], new Gost(tokeni[0], tokeni[1], tokeni[2], tokeni[3], Pol.valueOf(tokeni[4])));
				} else if (uloga == Uloga.DOMACIN)	{
					korisnici.put(tokeni[0], new Domacin(tokeni[0], tokeni[1], tokeni[2], tokeni[3], Pol.valueOf(tokeni[4])));
				} else if (uloga == Uloga.ADMINISTRATOR)	{
					korisnici.put(tokeni[0], new Administrator(tokeni[0], tokeni[1], tokeni[2], tokeni[3], Pol.valueOf(tokeni[4])));
				}
				// TODO: dijagnostika, moze se obrisati kasnije
				System.out.println(korisnici.get(tokeni[0]).getLozinka() + ", " + korisnici.get(tokeni[0]).getPrezime() + ", " + korisnici.get(tokeni[0]).getPol());
			}
		} catch (Exception e)	{
			e.printStackTrace();
			System.out.println("Fajl " + putanja + " nije pronadjen.");
		}
	}
}
