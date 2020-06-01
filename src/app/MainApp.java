package app;

import static spark.Spark.port;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.staticFiles;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import com.google.gson.Gson;

import beans.Domacin;
import beans.Gost;
import beans.Korisnik;

public class MainApp {

	private static Gson gson = new Gson();
	private static HashMap<String, Korisnik> korisnici = new HashMap<String, Korisnik>();
	
	public static void main(String[] args) throws IOException {

		port(8080);
		
		staticFiles.externalLocation(new File("./static").getCanonicalPath());
		
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
					// TODO: Ovo treba nekako handleovati
					// Error 400: Bad Request - vec postoji korisnik sa datim korisnickim imenom
					res.status(400);
					return null;
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
					// TODO: Ovo treba nekako handleovati
					// Error 400: Bad Request - vec postoji korisnik sa datim korisnickim imenom
					res.status(400);
					return null;
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
}
