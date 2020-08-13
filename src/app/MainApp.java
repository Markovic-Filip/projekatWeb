package app;

import static spark.Spark.port;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.delete;
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
import enums.Uloga;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import spark.Request;
import spark.Response;

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
					System.out.println("REGISTRACIJA DOMACINA: Domacin " + noviDomacin.getKorisnickoIme() + " uspesno registrovan.\r\n");
					return gson.toJson(noviDomacin);
				} else	{
					res.status(400);	// Status 400 Bad Request
					System.out.println("REGISTRACIJA DOMACINA: Korisničko ime " + noviDomacin.getKorisnickoIme() + " je zauzeto.\r\n");
					return gson.toJson(new Odgovor("Korisničko ime " + noviDomacin.getKorisnickoIme() + " je zauzeto. Pokušajte drugo korisničko ime."));
				}
			} else	{
				System.out.println("REGISTRACIJA DOMACINA: Objekat korisnika ne moze da se kreira.\r\n");
				res.status(500);	// Error 500: Internal Server Error - iz nekog razloga ne moze da parsira JSON objekat
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
				// TODO: ne znam cemu drugi argument za dodajNovogKorisnika, vise u KorisniciDAO
				if (korisnici.dodajNovogKorisnika(noviGost, noviGost.getUloga()))	{
					System.out.println("REGISTRACIJA GOSTA: Gost " + noviGost.getKorisnickoIme() + " uspesno registrovan.");
					logovanjeKorisnika(noviGost);
					System.out.println("REGISTRACIJA GOSTA: JWT novog gosta: " + noviGost.getJWTToken() + "\r\n");
					return gson.toJson(noviGost);
				} else	{
					res.status(400);	// Status 400 Bad Request
					System.out.println("REGISTRACIJA GOSTA: Korisničko ime " + noviGost.getKorisnickoIme() + " je zauzeto.\r\n");
					return gson.toJson(new Odgovor("Korisničko ime " + noviGost.getKorisnickoIme() + " je zauzeto. Pokušajte drugo korisničko ime."));
				}
			} else	{
				System.out.println("REGISTRACIJA GOSTA: Objekat korisnika ne moze da se kreira.\r\n");
				res.status(500);	// Error 500: Internal Server Error - iz nekog razloga ne moze da parsira JSON objekat
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
					logovanjeKorisnika(korisnik);
					
					//String jwt = Jwts.builder().setSubject(korisnik.getKorisnickoIme()).setIssuedAt(new Date()).signWith(key).compact();
					//korisnik.setJWTToken(jwt);
					
					return gson.toJson(korisnik);
				} else	{
					// Neuspesno logovanje
					res.status(400);	// Status 400 Bad Request
					System.out.println("LOGIN: Pogresna lozinka za korisnicko ime " + korisnik.getKorisnickoIme() + "\r\n");
					return gson.toJson(new Odgovor("Lozinka za nalog " + korisnik.getKorisnickoIme() + " nije ispravna. Pokušajte ponovo."));
				}
			} else	{
				// Korisnik ne postoji
				res.status(400);	// Status 400 Bad Request
				System.out.println("LOGIN: Korisnik " + korisnickoIme + " nije pronadjen u bazi.\r\n");
				return gson.toJson(new Odgovor("Ne postoji registrovan korisnik sa korisnickim imenom: " + korisnickoIme));
			}
		});
		
		get("/app/preuzmi_ulogu", (req, res) -> {
			Korisnik korisnik = proveraOvlascenja(req, res);
			
			if (korisnik != null) {
				return gson.toJson(new Odgovor(korisnik.getUloga().name()));
			} else	{
				if (res.status() == 400)	{
					return gson.toJson(new Odgovor("Morate se ulogovati da biste nastavili. Uskoro ćete biti prebačeni na login stranicu."));
				}
				else if (res.status() == 500)	{
					// TODO: error 500 ne treba da log outuje, samo da javi da pokusa ponovo, ili eventualno da se ponovo uloguje
					return gson.toJson(new Odgovor("Morate se ulogovati da biste nastavili. Uskoro ćete biti prebačeni na login stranicu."));
				}
			}
			
			// TODO:
			res.status(500);
			return gson.toJson(new Odgovor("Bas HC greska."));
			
			
			/*System.out.println("GET ULOGA: " + req.headers());
			String autorizacija = req.headers("Authorization");
			System.out.println("GET ULOGA: " + autorizacija);
			if (autorizacija != null && autorizacija.contains("Bearer ")) {	
				String jwt = autorizacija.substring(autorizacija.indexOf("Bearer ") + 7);
				try {
					Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt);
					String korisnickoIme = claims.getBody().getSubject();
					Korisnik korisnik = korisnici.dobaviKorisnika(korisnickoIme);
					return gson.toJson(new Odgovor(korisnik.getUloga().name()));
				} catch (Exception e) {
					res.status(500);
					System.out.println("GET ULOGA: Ne moze da parsira JWT.");
					return gson.toJson(new Odgovor("Ne moze da parsira JWT."));
				}
			} else	{
				res.status(400);
				System.out.println("GET ULOGA: Autentikacija nije validna.");
				return gson.toJson(new Odgovor("Autentikacija nije validna."));
			}*/
		});
		
		get("/app/dobavi_korisnike", (req, res) -> {
			Korisnik korisnik = proveraOvlascenja(req, res);
			
			if (korisnik != null) {
				if (korisnik.getUloga().equals(Uloga.ADMINISTRATOR))	{
					return gson.toJson(korisnici.sviKorisnici());
				}
				else	{
					System.out.println("DOBAVI KORISNIKE: Korisnik " + korisnik.getKorisnickoIme() + " nije ovlascen za ove podatke.\r\n");
					res.status(403); // Error 403: Forbidden
					return gson.toJson(new Odgovor("Niste ovlašćeni za traženi sadržaj."));
				}
			} else	{
				if (res.status() == 400)	{
					return gson.toJson(new Odgovor("Morate se ulogovati da biste nastavili. Uskoro ćete biti prebačeni na login stranicu."));
				}
				else if (res.status() == 500)	{
					// TODO: error 500 ne treba da log outuje, samo da javi da pokusa ponovo, ili eventualno da se ponovo uloguje 
					return gson.toJson(new Odgovor("Morate se ulogovati da biste nastavili. Uskoro ćete biti prebačeni na login stranicu."));
				}
			}
			
			// TODO:
			res.status(500);
			return gson.toJson(new Odgovor("Bas HC greska."));
		});
		
		delete("/app/obirsi_korisnika", (req, res) -> {
			Korisnik korisnik = proveraOvlascenja(req, res);
			
			if (korisnik != null)	{
				if (korisnik.getUloga().equals(Uloga.ADMINISTRATOR))	{
					String payload = req.body();
					System.out.println("OBRISI KORISNIKA: " + payload + "\r\n");
					Korisnik korisnikZaBrisanje = gson.fromJson(payload, Korisnik.class);
					if (korisnici.obrisiKorisnika(korisnikZaBrisanje.getKorisnickoIme()))	{
						System.out.println("OBRISI KORISNIKA: Korisnik " + korisnikZaBrisanje.getKorisnickoIme() + " uspesno obrisan iz baze.\r\n");
						return gson.toJson(korisnici.sviKorisnici());
					} else	{
						System.out.println("OBRISI KORISNIKA: " + korisnikZaBrisanje.getKorisnickoIme() + " nije pronadjen u bazi.\r\n");
						res.status(404); // Error 404: Not Found
						return gson.toJson(new Odgovor("Traženi resurs nije pronađen."));
					}
				} else	{
					System.out.println("OBRISI KORISNIKA: Korisnik " + korisnik.getKorisnickoIme() + " nije ovlascen za ovu metodu.\r\n");
					res.status(403); // Error 403: Forbidden
					return gson.toJson(new Odgovor("Niste ovlašćeni za traženi sadržaj."));
				}
			} else	{
				System.out.println("OBRISI KORISNIKA: Korisnik koji nije ulogovan je pokusao da pozove ovu metodu.\r\n");
				res.status(400);
				return gson.toJson(new Odgovor("Morate se ulogovati da biste nastavili. Uskoro ćete biti prebačeni na login stranicu."));
			}
		});
	}

	private static void logovanjeKorisnika(Korisnik korisnik) {
		String jwt = Jwts.builder().setSubject(korisnik.getKorisnickoIme()).setIssuedAt(new Date()).signWith(key).compact();
		korisnik.setJWTToken(jwt);
	}
	
	private static Korisnik proveraOvlascenja(Request req, Response res) {
		System.out.println("PROVERA OVLASCENJA: " + req.headers());
		String autorizacija = req.headers("Authorization");
		System.out.println("PROVERA OVLASCENJA: " + autorizacija);
		if (autorizacija != null && autorizacija.contains("Bearer ")) {	
			String jwt = autorizacija.substring(autorizacija.indexOf("Bearer ") + 7);
			try {
				Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt);
				String korisnickoIme = claims.getBody().getSubject();
				Korisnik korisnik = korisnici.dobaviKorisnika(korisnickoIme);
				//return gson.toJson(new Odgovor(korisnik.getUloga().name()));
				return korisnik;
			} catch (Exception e) {
				res.status(500);
				System.out.println("PROVERA OVLASCENJA: Ne moze da parsira JWT.");
				//return gson.toJson(new Odgovor("Ne moze da parsira JWT."));
				return null;
			}
		} else	{
			res.status(400);
			System.out.println("PROVERA OVLASCENJA: Korisnik nije ulogovan.");
			//return gson.toJson(new Odgovor("Ovlascenje nije validno."));
			return null;
		}
	}
}
