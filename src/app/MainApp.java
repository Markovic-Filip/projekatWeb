package app;

import static spark.Spark.port;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.staticFiles;

import java.io.File;
import java.io.IOException;

import com.google.gson.Gson;

import beans.Domacin;
import beans.Gost;
import beans.Odgovor;
import dao.KorisniciDAO;

public class MainApp {

	private static Gson gson = new Gson();
	private static KorisniciDAO korisnici = null;
	
	public static void main(String[] args) throws IOException {

		// Inicijalizacija
		
		port(8080);
		
		staticFiles.externalLocation(new File("./static").getCanonicalPath());
		
		korisnici = new KorisniciDAO("./static/baza/");
		
		// Obrada HTTP zahteva
		
		post("/app/registracija/domacin", (req, res) ->	{
			res.type("application/json");
			String payload = req.body();
			Domacin noviDomacin = gson.fromJson(payload, Domacin.class);
			if (noviDomacin != null)	{
				System.out.println(noviDomacin.getIme() + ", " + noviDomacin.getKorisnickoIme());
				if (korisnici.dodajNovogKorisnika(noviDomacin, noviDomacin.getUloga()))	{
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
				if (korisnici.dodajNovogKorisnika(noviGost, noviGost.getUloga()))	{
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
}
