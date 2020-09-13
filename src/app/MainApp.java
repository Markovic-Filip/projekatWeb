package app;

import static spark.Spark.port;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;
import static spark.Spark.delete;
import static spark.Spark.staticFiles;

import java.io.File;
import java.io.IOException;
import java.security.Key;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import beans.Apartman;
import beans.Domacin;
import beans.Gost;
import beans.Komentar;
import beans.Korisnik;
import beans.Odgovor;
import beans.Rezervacija;
import dao.ApartmaniDAO;
import dao.KomentariDAO;
import dao.KorisniciDAO;
import dao.RezervacijeDAO;
import dao.SadrzajApartmanaDAO;
import enums.Status;
import enums.StatusKorisnika;
import enums.Uloga;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import spark.Request;
import spark.Response;

public class MainApp {

	//private static Gson gson = new Gson();
	private static Gson gson;
	private static KorisniciDAO korisnici = null;
	private static RezervacijeDAO rezervacije = null;
	private static ApartmaniDAO apartmani = null;
	private static SadrzajApartmanaDAO sadrzaji = null;
	private static KomentariDAO komentari = null;
	private static Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
	
	public static void main(String[] args) throws IOException {

		// Inicijalizacija
		
		port(8080);
		
		staticFiles.externalLocation(new File("./static").getCanonicalPath());
		
		korisnici = new KorisniciDAO("./static/baza/");
		
		rezervacije = new RezervacijeDAO("./static/baza/rezervacije.txt");
		
		apartmani = new ApartmaniDAO("./static/baza/apartmani.txt");
		
		sadrzaji = new SadrzajApartmanaDAO("./static/baza/sadrzajApartmana.txt");
		
		komentari = new KomentariDAO("./static/baza/komentari.txt");
		
		// Izmenjeno iz new Gson() u ovo jer ovaj oblik moze da parsira milisekunde u Date
		gson =  new GsonBuilder().registerTypeAdapter(Date.class, (JsonDeserializer) (json, typeOfT, context) -> new Date(json.getAsLong())).create();
		
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
				StatusKorisnika status = StatusKorisnika.AKTIVAN;
				switch (korisnik.getUloga())	{
				case GOST:
					Gost gost = (Gost) korisnik;
					status = gost.getStatus();
					break;
				case DOMACIN:
					Domacin domacin = (Domacin) korisnik;
					status = domacin.getStatus();
					break;
				default:
					break;
				}
				if (status.equals(StatusKorisnika.AKTIVAN))	{
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
					res.status(400);
					System.out.println("LOGIN: Korisnik " + korisnik.getKorisnickoIme() + " je blokiran i ne moze da se uloguje.\r\n");
					return gson.toJson(new Odgovor("Nalog " + korisnik.getKorisnickoIme() + " je blokiran. Nije moguce ulogovati se pod tim imenom."));
				}
			} else	{
				// Korisnik ne postoji
				res.status(400);	// Status 400 Bad Request
				System.out.println("LOGIN: Korisnik " + korisnickoIme + " nije pronadjen u bazi.\r\n");
				return gson.toJson(new Odgovor("Ne postoji registrovan korisnik sa korisnickim imenom: " + korisnickoIme));
			}
		});
		
		// TODO: Zasad nigde ne koristim ovo, mozda treba obrisati kasnije
		get("/app/preuzmi_ulogu", (req, res) -> {
			Korisnik korisnik = proveraOvlascenja(req, res);
			
			if (korisnik != null) {
				return gson.toJson(new Odgovor(korisnik.getUloga().name()));
			} else	{
				if (res.status() == 400)	{
					System.out.println("PREUZMI ULOGU: Korisnik koji nije ulogovan je pokusao da pozove ovu metodu.\r\n");
					return gson.toJson(new Odgovor("Morate se ulogovati da biste nastavili. Uskoro ćete biti prebačeni na login stranicu."));
				}
				else if (res.status() == 500)	{
					// TODO: error 500 ne treba da log outuje, samo da javi da pokusa ponovo, ili eventualno da se ponovo uloguje
					System.out.println("PREUZMI ULOGU: Ne moze da parsira JWT.\r\n");
					return gson.toJson(new Odgovor("Došlo je do greške na serveru. Pokušajte ponovo."));
				}
				
				res.status(500);
				return gson.toJson(new Odgovor("Došlo je do greške na serveru. Pokušajte ponovo."));
			}
		});
		
		get("/app/dobavi_korisnike", (req, res) -> {
			Korisnik korisnik = proveraOvlascenja(req, res);
			
			if (korisnik != null) {
				if (korisnik.getUloga().equals(Uloga.ADMINISTRATOR))	{
					return gson.toJson(korisnici.sviKorisnici());
				} else	{
					System.out.println("DOBAVI KORISNIKE: Korisnik " + korisnik.getKorisnickoIme() + " nije ovlascen za ove podatke.\r\n");
					res.status(403); // Error 403: Forbidden
					return gson.toJson(new Odgovor("Niste ovlašćeni za traženi sadržaj."));
				}
			} else	{
				if (res.status() == 400)	{
					System.out.println("DOBAVI KORISNIKE: Korisnik koji nije ulogovan je pokusao da pozove ovu metodu.\r\n");
					return gson.toJson(new Odgovor("Morate se ulogovati da biste nastavili. Uskoro ćete biti prebačeni na login stranicu."));
				} else if (res.status() == 500)	{
					System.out.println("DOBAVI KORISNIKE: Ne moze da parsira JWT.\r\n");
					return gson.toJson(new Odgovor("Došlo je do greške na serveru. Pokušajte ponovo."));
				}
				
				res.status(500);
				return gson.toJson(new Odgovor("Došlo je do greške na serveru. Pokušajte ponovo."));
			}
		});
		
		get("/app/dobavi_rezervacije", (req, res) -> {
			Korisnik korisnik = proveraOvlascenja(req, res);
			
			if (korisnik != null)	{
				if (korisnik.getUloga().equals(Uloga.ADMINISTRATOR))	{
					return gson.toJson(rezervacije.sveRezervacije());
				} else	{
					System.out.println("DOBAVI REZERVACIJE: Korisnik " + korisnik.getKorisnickoIme() + " nije ovlascen za ove podatke.\r\n");
					res.status(403); // Error 403: Forbidden
					return gson.toJson(new Odgovor("Niste ovlašćeni za traženi sadržaj."));
				}
			} else	{
				if (res.status() == 400)	{
					System.out.println("DOBAVI REZERVACIJE: Korisnik koji nije ulogovan je pokusao da pozove ovu metodu.\r\n");
					return gson.toJson(new Odgovor("Morate se ulogovati da biste nastavili. Uskoro ćete biti prebačeni na login stranicu."));
				} else if (res.status() == 500)	{
					System.out.println("DOBAVI REZERVACIJE: Ne moze da parsira JWT.\r\n");
					return gson.toJson(new Odgovor("Došlo je do greške na serveru. Pokušajte ponovo."));
				}
				
				res.status(500);
				return gson.toJson(new Odgovor("Došlo je do greške na serveru. Pokušajte ponovo."));
			}
		});
		
		get("/app/dobavi_aktivne_apartmane", (req, res) -> {
			if (req.headers("Authorization") != null)	{
				Korisnik korisnik = proveraOvlascenja(req, res);
				
				if (korisnik != null)	{
					if (korisnik.getUloga().equals(Uloga.DOMACIN))	{
						return gson.toJson(apartmani.dobaviAktivneApartmaneZaDomacina(korisnik.getKorisnickoIme()));
					}
				}
			}
			
			return gson.toJson(apartmani.dobaviAktivneApartmane());
		});
		
		get("/app/dobavi_neaktivne_apartmane", (req, res) -> {
			Korisnik korisnik = proveraOvlascenja(req, res);
			
			if (korisnik != null) {
				if (korisnik.getUloga().equals(Uloga.ADMINISTRATOR))	{
					return gson.toJson(apartmani.dobaviNeaktivneApartmane());
				} else if (korisnik.getUloga().equals(Uloga.DOMACIN))	{
					return gson.toJson(apartmani.dobaviNeaktivneApartmaneZaDomacina(korisnik.getKorisnickoIme()));
				} else	{
					System.out.println("DOBAVI NEAKTIVNE APARTMANE: Korisnik " + korisnik.getKorisnickoIme() + " nije ovlascen za ove podatke.\r\n");
					res.status(403); // Error 403: Forbidden
					return gson.toJson(new Odgovor("Niste ovlašćeni za traženi sadržaj."));
				}
			} else	{
				if (res.status() == 400)	{
					System.out.println("DOBAVI NEAKTIVNE APARTMANE: Korisnik koji nije ulogovan je pokusao da pozove ovu metodu.\r\n");
					return gson.toJson(new Odgovor("Morate se ulogovati da biste nastavili. Uskoro ćete biti prebačeni na login stranicu."));
				} else if (res.status() == 500)	{
					System.out.println("DOBAVI NEAKTIVNE APARTMANE: Ne moze da parsira JWT.\r\n");
					return gson.toJson(new Odgovor("Došlo je do greške na serveru. Pokušajte ponovo."));
				}
				
				res.status(500);
				return gson.toJson(new Odgovor("Došlo je do greške na serveru. Pokušajte ponovo."));
			}
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
				if (res.status() == 400)	{
					System.out.println("OBRISI KORISNIKA: Korisnik koji nije ulogovan je pokusao da pozove ovu metodu.\r\n");
					return gson.toJson(new Odgovor("Morate se ulogovati da biste nastavili. Uskoro ćete biti prebačeni na login stranicu."));
				}
				else if (res.status() == 500)	{ 
					System.out.println("OBIRIS KORISNIKA: Ne moze da parsira JWT.\r\n");
					return gson.toJson(new Odgovor("Došlo je do greške na serveru. Pokušajte ponovo."));
				}
				
				res.status(500);
				return gson.toJson(new Odgovor("Došlo je do greške na serveru. Pokušajte ponovo."));
			}
		});
		
		put("/app/promeni_status_korisnika", (req, res) -> {
			Korisnik korisnik = proveraOvlascenja(req, res);
			
			if (korisnik != null)	{
				if (korisnik.getUloga().equals(Uloga.ADMINISTRATOR))	{
					String payload = req.body();
					System.out.println("PROMENI STATUS KORISNIKA: " + payload + "\r\n");
					Korisnik korisnikZaPromenu = gson.fromJson(payload, Korisnik.class);
					korisnici.promeniStatusKorisnika(korisnikZaPromenu.getKorisnickoIme());
					return gson.toJson(korisnici.sviKorisnici());
				} else	{
					System.out.println("PROMENI STATUS KORISNIKA: Korisnik " + korisnik.getKorisnickoIme() + " nije ovlascen za ovu metodu.\r\n");
					res.status(403); // Error 403: Forbidden
					return gson.toJson(new Odgovor("Niste ovlašćeni za traženi sadržaj."));
				}
			} else	{
				if (res.status() == 400)	{
					System.out.println("PROMENI STATUS KORISNIKA: Korisnik koji nije ulogovan je pokusao da pozove ovu metodu.\r\n");
					return gson.toJson(new Odgovor("Morate se ulogovati da biste nastavili. Uskoro ćete biti prebačeni na login stranicu."));
				}
				else if (res.status() == 500)	{ 
					System.out.println("PROMENI STATUS KORISNIKA: Ne moze da parsira JWT.\r\n");
					return gson.toJson(new Odgovor("Došlo je do greške na serveru. Pokušajte ponovo."));
				}
				
				res.status(500);
				return gson.toJson(new Odgovor("Došlo je do greške na serveru. Pokušajte ponovo."));
			}
		});
		
		delete("/app/obrisi_apartman", (req, res) -> {
			Korisnik korisnik = proveraOvlascenja(req, res);
			
			if (korisnik != null)	{
				if (!korisnik.getUloga().equals(Uloga.GOST))	{
					String payload = req.body();
					System.out.println("OBRISI APARTMAN: " + payload + "\r\n");
					Apartman apartmanZaBrisanje = gson.fromJson(payload, Apartman.class);
					if (apartmani.obrisiApartman(apartmanZaBrisanje.getId()))	{
						System.out.println("OBRISI APARTMAN: Apartman " + apartmanZaBrisanje.getId() + " uspesno obrisan iz baze.\r\n");
						if (korisnik.getUloga().equals(Uloga.ADMINISTRATOR))	{
							if (apartmanZaBrisanje.getStatus().equals(Status.AKTIVNO))	{
								return gson.toJson(apartmani.dobaviAktivneApartmane());
							} else	{
								return gson.toJson(apartmani.dobaviNeaktivneApartmane());
							}
						} else	{
							if (apartmanZaBrisanje.getStatus().equals(Status.AKTIVNO))	{
								return gson.toJson(apartmani.dobaviAktivneApartmaneZaDomacina(korisnik.getKorisnickoIme()));
							} else	{
								return gson.toJson(apartmani.dobaviNeaktivneApartmaneZaDomacina(korisnik.getKorisnickoIme()));
							}
						}
					} else	{
						System.out.println("OBRISI APARTMAN: " + apartmanZaBrisanje.getId() + " nije pronadjen u bazi.\r\n");
						res.status(404); // Error 404: Not Found
						return gson.toJson(new Odgovor("Traženi resurs nije pronađen."));
					}
				} else	{
					System.out.println("OBRISI APARTMAN: Korisnik " + korisnik.getKorisnickoIme() + " nije ovlascen za ovu metodu.\r\n");
					res.status(403); // Error 403: Forbidden
					return gson.toJson(new Odgovor("Niste ovlašćeni za traženi sadržaj."));
				}
			} else	{
				if (res.status() == 400)	{
					System.out.println("OBRISI APARTMAN: Korisnik koji nije ulogovan je pokusao da pozove ovu metodu.\r\n");
					return gson.toJson(new Odgovor("Morate se ulogovati da biste nastavili. Uskoro ćete biti prebačeni na login stranicu."));
				}
				else if (res.status() == 500)	{ 
					System.out.println("OBIRIS APARTMAN: Ne moze da parsira JWT.\r\n");
					return gson.toJson(new Odgovor("Došlo je do greške na serveru. Pokušajte ponovo."));
				}
				
				res.status(500);
				return gson.toJson(new Odgovor("Došlo je do greške na serveru. Pokušajte ponovo."));
			}
		});
		
		post("/app/napravi_rezervaciju", (req, res) -> {
			Korisnik korisnik = proveraOvlascenja(req, res);
			
			if (korisnik != null)	{
				if (korisnik.getUloga().equals(Uloga.GOST))	{
					String payload = req.body();
					System.out.println("NAPRAVI REZERVACIJU: " + payload + "\r\n");
					Rezervacija novaRezervacija = gson.fromJson(payload, Rezervacija.class);
					novaRezervacija.setGost(korisnik.getKorisnickoIme());
					if (rezervacije.dodajNovuRezervaciju(novaRezervacija))	{
						System.out.println("NAPRAVI REZERVACIJU: Nova rezervacija id:" + novaRezervacija.getId() + " uspesno kreirana.\r\n");
						korisnici.dodajRezervaciju(korisnik.getKorisnickoIme(), novaRezervacija.getId());
						return gson.toJson(new Odgovor("Uspešno ste rezervisali apartman."));
					} else	{
						System.out.println("NAPRAVI REZERVACIJU: Datumi se preklapaju, rezervacija nije napravljena.\r\n");
						return gson.toJson(new Odgovor("Datumi za izabrani apartman su zauzeti. Pokušajte neki drugi apartman ili datum."));
					}
				} else	{
					System.out.println("NAPRAVI REZERVACIJU: Korisnik " + korisnik.getKorisnickoIme() + " nije ovlascen za ovu metodu.\r\n");
					res.status(403); // Error 403: Forbidden
					return gson.toJson(new Odgovor("Niste ovlašćeni za traženi sadržaj."));
				}
			} else	{
				if (res.status() == 400)	{
					System.out.println("NAPRAVI REZERVACIJU: Korisnik koji nije ulogovan je pokusao da pozove ovu metodu.\r\n");
					return gson.toJson(new Odgovor("Morate se ulogovati da biste nastavili. Uskoro ćete biti prebačeni na login stranicu."));
				}
				else if (res.status() == 500)	{ 
					System.out.println("NAPRAVI REZERVACIJU: Ne moze da parsira JWT.\r\n");
					return gson.toJson(new Odgovor("Došlo je do greške na serveru. Pokušajte ponovo."));
				}
				
				res.status(500);
				return gson.toJson(new Odgovor("Došlo je do greške na serveru. Pokušajte ponovo."));
			}
		});
		
		post("/app/dodavanje_apartmana", (req, res) ->	{
			res.type("application/json");
			Korisnik korisnik = proveraOvlascenja(req, res);
			String payload = req.body();
			System.out.println("DODAVANJE APARTMANA: " + payload);
			Apartman noviApartman = gson.fromJson(payload, Apartman.class);
			if (korisnik != null)	{
				if (korisnik.getUloga().equals(Uloga.DOMACIN))	{

					if (noviApartman != null)	{
						System.out.println("DODAVANJE APARTMANA: " + noviApartman.getId() + " " + noviApartman.getKorisnickoImeDomacina());
						noviApartman.setKorisnickoImeDomacina(korisnik.getKorisnickoIme());
						apartmani.dodajNoviApartman(noviApartman);
						System.out.println("Dodavanje apartmana: Apartman " + noviApartman.getId() + " uspesno dodat.\r\n");
						korisnici.dodajApartman(korisnik.getKorisnickoIme(), noviApartman.getId());
						return gson.toJson(new Odgovor("Apartman uspešno dodat."));

					} else	{
						System.out.println("DODAVANJE APARTMANA: Objekat apartman ne moze da se kreira.\r\n");
						res.status(500);	// Error 500: Internal Server Error - iz nekog razloga ne moze da parsira JSON objekat
						return gson.toJson(new Odgovor("Greška prilikom dodavanja apartmana. Pokušajte ponovo."));
					}
				} else	{
					System.out.println("DODAVANJE APARTMANA: Korisnik " + korisnik.getKorisnickoIme() + " nije ovlascen za ove podatke.\r\n");
					res.status(403); // Error 403: Forbidden
					return gson.toJson(new Odgovor("Niste ovlašćeni za traženi sadržaj."));
				}
			} else	{
				if (res.status() == 400)	{
					System.out.println("DODAVANJE APARTMANA: Korisnik koji nije ulogovan je pokusao da pozove ovu metodu.\r\n");
					return gson.toJson(new Odgovor("Morate se ulogovati da biste nastavili. Uskoro ćete biti prebačeni na login stranicu."));
				} else if (res.status() == 500)	{
					System.out.println("DODAVANJA APARTMANA: Ne moze da parsira JWT.\r\n");
					return gson.toJson(new Odgovor("Došlo je do greške na serveru. Pokušajte ponovo."));
				}

				res.status(500);
				return gson.toJson(new Odgovor("Došlo je do greške na serveru. Pokušajte ponovo."));
			}

		});
		
		put("/app/izmeni_apartman", (req, res) -> {
			Korisnik korisnik = proveraOvlascenja(req, res);
			
			if (korisnik != null)	{
				if (!korisnik.getUloga().equals(Uloga.GOST))	{
					String payload = req.body();
					System.out.println("IZMENI APARTMAN: " + payload + "\r\n");
					Apartman izmenjenApartman = gson.fromJson(payload, Apartman.class);
					if (apartmani.izmeniApartman(izmenjenApartman))	{
						System.out.println("IZMENI APARTMAN: Apartman id:" + izmenjenApartman.getId() + " uspesno izmenjen i sacuvan.\r\n");
						return gson.toJson(new Odgovor("Apartman uspešno izmenjen."));
					} else	{
						System.out.println("IZMENI APARTMAN: Greska pri izmeni apartmana. Izmenjen apartman nije sacuvan.\r\n");
						return gson.toJson(new Odgovor("Došlo je do greške prilikom izmene. Pokušajte ponovo."));
					}
				} else	{
					System.out.println("IZMENI APARTMAN: Korisnik " + korisnik.getKorisnickoIme() + " nije ovlascen za ovu metodu.\r\n");
					res.status(403); // Error 403: Forbidden
					return gson.toJson(new Odgovor("Niste ovlašćeni za traženi sadržaj."));
				}
			} else	{
				if (res.status() == 400)	{
					System.out.println("IZMENI APARTMAN: Korisnik koji nije ulogovan je pokusao da pozove ovu metodu.\r\n");
					return gson.toJson(new Odgovor("Morate se ulogovati da biste nastavili. Uskoro ćete biti prebačeni na login stranicu."));
				}
				else if (res.status() == 500)	{ 
					System.out.println("IZMENI APARTMAN: Ne moze da parsira JWT.\r\n");
					return gson.toJson(new Odgovor("Došlo je do greške na serveru. Pokušajte ponovo."));
				}
				
				res.status(500);
				return gson.toJson(new Odgovor("Došlo je do greške na serveru. Pokušajte ponovo."));
			}
		});
		
		get("/app/dobavi_sadrzaj_apartmana", (req, res) -> {
			String parametri = req.queryParams("idSadrzaja");
			if (parametri != null)	{
				int[] idSadrzaja = gson.fromJson(parametri, int[].class);
				System.out.println("DOBAVI SADRZAJE: " + req.queryParams("idSadrzaja") + "\r\n");
				return gson.toJson(sadrzaji.dobaviSadrzaje(idSadrzaja));
			} else	{
				return gson.toJson(sadrzaji.sviSadrzaji());
			}
		});
		
		get("/app/dobavi_komentare", (req, res) -> {
			String idApartmana = req.queryParams("idApartmana");
			String domacin = req.queryParams("domacin");
			if (idApartmana != null)	{
				System.out.println("DOBAVI KOMENTARE: Komentari za apartman id:" + idApartmana + "\r\n");
				if (domacin != null)	{
					return gson.toJson(komentari.sviKomentariApartmana(Integer.parseInt(idApartmana)));
				} else	{
					return gson.toJson(komentari.sviOdobreniKomentariApartmana(Integer.parseInt(idApartmana)));
				}
			} else	{
				System.out.println("DOBAVI KOMENTARE: Greska u pozivu. Fali parametar.\r\n");
				res.status(400); // Error 400: Bad Request
				return gson.toJson(new Odgovor("Greška u dobavljanju komentara. Pokušajte da ponovo otvorite stranicu."));
			}
		});
		
		put("/app/promeni_status_komentara", (req, res) -> {
			Korisnik korisnik = proveraOvlascenja(req, res);
			
			if (korisnik != null)	{
				if (korisnik.getUloga().equals(Uloga.DOMACIN))	{
					String payload = req.body();
					System.out.println("IZMENI KOMENTAR: " + payload + "\r\n");
					Komentar izmenjenKomentar = gson.fromJson(payload, Komentar.class);
					if (komentari.promeniStatusKomentara(izmenjenKomentar.getId(), izmenjenKomentar.isOdobren()))	{
						System.out.println("IZMENI KOMENTAR: Komentar id:" + izmenjenKomentar.getId() + " uspesno izmenjen i sacuvan.\r\n");
						return gson.toJson(new Odgovor("Status komentara promenjen."));
					} else	{
						System.out.println("IZMENI KOMENTAR: Greska pri izmeni komentara. Izmenjen komentar nije sacuvan.\r\n");
						return gson.toJson(new Odgovor("Došlo je do greške prilikom izmene. Pokušajte ponovo."));
					}
				} else	{
					System.out.println("IZMENI KOMENTAR: Korisnik " + korisnik.getKorisnickoIme() + " nije ovlascen za ovu metodu.\r\n");
					res.status(403); // Error 403: Forbidden
					return gson.toJson(new Odgovor("Niste ovlašćeni za traženi sadržaj."));
				}
			} else	{
				if (res.status() == 400)	{
					System.out.println("IZMENI APARTMAN: Korisnik koji nije ulogovan je pokusao da pozove ovu metodu.\r\n");
					return gson.toJson(new Odgovor("Morate se ulogovati da biste nastavili. Uskoro ćete biti prebačeni na login stranicu."));
				}
				else if (res.status() == 500)	{ 
					System.out.println("IZMENI APARTMAN: Ne moze da parsira JWT.\r\n");
					return gson.toJson(new Odgovor("Došlo je do greške na serveru. Pokušajte ponovo."));
				}
				
				res.status(500);
				return gson.toJson(new Odgovor("Došlo je do greške na serveru. Pokušajte ponovo."));
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
				return korisnik;
			} catch (Exception e) {
				res.status(500);
				System.out.println("PROVERA OVLASCENJA: Ne moze da parsira JWT.");
				return null;
			}
		} else	{
			res.status(400);
			System.out.println("PROVERA OVLASCENJA: Korisnik nije ulogovan.");
			return null;
		}
	}
}
