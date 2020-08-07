package app;

import static spark.Spark.port;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.staticFiles;

import java.io.File;
import java.io.IOException;
import java.security.Key;
import java.util.Date;

import com.google.gson.Gson;

import beans.Domacin;
import beans.Gost;
import beans.Korisnik;
import beans.Odgovor;
import dao.KorisniciDAO;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class MainApp {

	private static Gson gson = new Gson();
	private static KorisniciDAO korisnici = null;
	private static Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
	
	public static void main(String[] args) throws IOException {

		// Inicijalizacija
		
		port(8080);
		
		staticFiles.externalLocation(new File("./static").getCanonicalPath());
		
		korisnici = new KorisniciDAO("./static/baza/");
		
		// Obrada HTTP zahteva
		
		post("/app/registracija/domacin", (req, res) ->	{
			res.type("application/json");
			String payload = req.body();
			System.out.println("REGISTRACIJA DOMACINA: " + payload);
			Domacin noviDomacin = gson.fromJson(payload, Domacin.class);
			if (noviDomacin != null)	{
				System.out.println("REGISTRACIJA DOMACINA: " + noviDomacin.getIme() + ", " + noviDomacin.getKorisnickoIme());
				if (korisnici.dodajNovogKorisnika(noviDomacin, noviDomacin.getUloga()))	{
					System.out.println("REGISTRACIJA DOMACINA: Domacin " + noviDomacin.getKorisnickoIme() + " uspesno registrovan.");
					return gson.toJson(noviDomacin);
				} else	{
					res.status(400);	// Status 400 Bad Request
					System.out.println("REGISTRACIJA DOMACINA: Korisničko ime " + noviDomacin.getKorisnickoIme() + " je zauzeto. Pokušajte ponovo.");
					return gson.toJson(new Odgovor("Korisničko ime " + noviDomacin.getKorisnickoIme() + " je zauzeto. Pokušajte ponovo."));
				}
			} else	{
				System.out.println("REGISTRACIJA DOMACINA: Objekat korisnika ne moze da se kreira.");
				// TODO: Ovo treba nekako handleovati
				// Error 500: Internal Server Error - iz nekog razloga ne moze da parsira JSON objekat
				res.status(500);
				return gson.toJson(new Odgovor("Greška prilikom registracije korisnika. Pokušajte ponovo."));
			}
		});
		
		post("/app/registracija/gost", (req, res) -> {
			res.type("application/json");
			String payload = req.body();
			System.out.println("REGISTRACIJA GOSTA: " + payload);
			Gost noviGost = gson.fromJson(payload, Gost.class);
			if (noviGost != null)	{
				System.out.println("REGISTRACIJA GOSTA: " + noviGost.getIme() + ", " + noviGost.getKorisnickoIme());
				if (korisnici.dodajNovogKorisnika(noviGost, noviGost.getUloga()))	{
					System.out.println("REGISTRACIJA GOSTA: Gost " + noviGost.getKorisnickoIme() + " uspesno registrovan.");
					return gson.toJson(noviGost);
				} else	{
					res.status(400);	// Status 400 Bad Request
					System.out.println("REGISTRACIJA GOSTA: Korisničko ime " + noviGost.getKorisnickoIme() + " je zauzeto. Pokušajte ponovo.");
					return gson.toJson(new Odgovor("Korisničko ime " + noviGost.getKorisnickoIme() + " je zauzeto. Pokušajte ponovo."));
				}
			} else	{
				System.out.println("REGISTRACIJA GOSTA: Objekat korisnika ne moze da se kreira.");
				// TODO: Ovo treba nekako handleovati
				// Error 500: Internal Server Error - iz nekog razloga ne moze da parsira JSON objekat
				res.status(500);
				return gson.toJson(new Odgovor("Greška prilikom registracije korisnika. Pokušajte ponovo."));
			}
		});
		
		post("/app/login", (req, res) -> {
			String payload = req.body();
			System.out.println("LOGIN: " + payload);
			
			String[] tokeni = payload.split("&");
			
			String korisnickoIme = tokeni[0];
			String lozinka = tokeni[1];
			
			Korisnik korisnik = korisnici.dobaviKorisnika(korisnickoIme);
			
			if (korisnik != null)	{
				if (korisnik.getLozinka().equals(lozinka))	{
					// Uspesno logovanje
					String jws = Jwts.builder().setSubject(korisnik.getKorisnickoIme()).setIssuedAt(new Date()).signWith(key).compact();
					korisnik.setJWTToken(jws);
					return gson.toJson(korisnik);
				} else	{
					// Neuspesno logovanje
					res.status(400);	// Status 400 Bad Request
					System.out.println("LOGIN: Pogresna lozinka za korisnicko ime " + korisnik.getKorisnickoIme());
					return gson.toJson(new Odgovor("Lozinka za nalog " + korisnik.getKorisnickoIme() + " nije ispravna. Pokušajte ponovo."));
				}
			} else	{
				// Korisnik ne postoji
				res.status(400);	// Status 400 Bad Request
				System.out.println("LOGIN: Korisnik " + korisnickoIme + " nije pronadjen u bazi.");
				return gson.toJson(new Odgovor("Ne postoji registrovan korisnik sa korisnickim imenom: " + korisnickoIme));
			}
		});
	}
}
