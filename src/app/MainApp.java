package app;

import static spark.Spark.port;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;
import static spark.Spark.delete;
import static spark.Spark.staticFiles;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;

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
import beans.SadrzajApartmana;
import dao.ApartmaniDAO;
import dao.KomentariDAO;
import dao.KorisniciDAO;
import dao.RezervacijeDAO;
import dao.SadrzajApartmanaDAO;
import enums.Status;
import enums.StatusKorisnika;
import enums.StatusRezervacije;
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
		
		for(Apartman apartman : apartmani.sviApartmani()) {
			apartman.setZauzetiDatumi(rezervacije.zauzetiDatumiApartmana(apartman.getId()));
		}
		
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
						if (korisnikZaBrisanje.getUloga().equals(Uloga.DOMACIN))	{
							ArrayList<Integer> idApartmana = apartmani.obrisiApartmaneDomacina(korisnikZaBrisanje.getKorisnickoIme());
							rezervacije.odbijRezervacijeZaApartmane(idApartmana);
						} else if (korisnikZaBrisanje.getUloga().equals(Uloga.GOST))	{
							rezervacije.obrisiRezervacijeGosta(korisnikZaBrisanje.getKorisnickoIme());
						}
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
					korisnikZaPromenu = korisnici.dobaviKorisnika(korisnikZaPromenu.getKorisnickoIme());
					if (korisnikZaPromenu.getUloga().equals(Uloga.GOST))	{
						Gost gost = (Gost) korisnikZaPromenu;
						if (gost.getStatus().equals(StatusKorisnika.BLOKIRAN))	{
							rezervacije.odustaniOdRezervacija(korisnikZaPromenu.getKorisnickoIme());
							for(Apartman apartman : apartmani.sviApartmani()) {
								apartman.setZauzetiDatumi(rezervacije.zauzetiDatumiApartmana(apartman.getId()));
							}
						}
					} else if (korisnikZaPromenu.getUloga().equals(Uloga.DOMACIN))	{
						Domacin domacin = (Domacin) korisnikZaPromenu;
						if (domacin.getStatus().equals(StatusKorisnika.BLOKIRAN))	{
							ArrayList<Integer> idApartmana = apartmani.deaktivirajApartmaneDomacina(korisnikZaPromenu.getKorisnickoIme());
							rezervacije.odbijRezervacijeZaApartmane(idApartmana);							
						}
					}
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
						ArrayList<Integer> idApartmana = new ArrayList<Integer>();
						idApartmana.add(apartmanZaBrisanje.getId());
						rezervacije.odbijRezervacijeZaApartmane(idApartmana);
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
						apartmani.dobaviApartman(novaRezervacija.getApartmanId()).setZauzetiDatumi(rezervacije.zauzetiDatumiApartmana(novaRezervacija.getApartmanId()));
						System.out.println("NAPRAVI REZERVACIJU: Nova rezervacija id:" + novaRezervacija.getId() + " uspesno kreirana.\r\n");
						korisnici.dodajRezervaciju(korisnik.getKorisnickoIme(), novaRezervacija.getId());
						return gson.toJson(new Odgovor("Uspešno ste rezervisali apartman. Ukupna cena: " + novaRezervacija.getCena()));
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
						res.header("idnovogapartmana", noviApartman.getId() + "");
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
						if (izmenjenApartman.getStatus().equals(Status.NEAKTIVNO))	{
							ArrayList<Integer> idApartmana = new ArrayList<Integer>();
							idApartmana.add(izmenjenApartman.getId());
							rezervacije.odbijRezervacijeZaApartmane(idApartmana);
						}
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
					System.out.println("IZMENI KOMENTAR: Korisnik koji nije ulogovan je pokusao da pozove ovu metodu.\r\n");
					return gson.toJson(new Odgovor("Morate se ulogovati da biste nastavili. Uskoro ćete biti prebačeni na login stranicu."));
				}
				else if (res.status() == 500)	{ 
					System.out.println("IZMENI KOMENTAR: Ne moze da parsira JWT.\r\n");
					return gson.toJson(new Odgovor("Došlo je do greške na serveru. Pokušajte ponovo."));
				}
				
				res.status(500);
				return gson.toJson(new Odgovor("Došlo je do greške na serveru. Pokušajte ponovo."));
			}
		});
		
		get("/app/dobavi_korisnika", (req, res) -> {
			Korisnik korisnik = proveraOvlascenja(req, res);
			
			if (korisnik != null)	{
				return gson.toJson(korisnik);
			} else	{
				if (res.status() == 400)	{
					System.out.println("DOBAVI KORISNIKA: Korisnik koji nije ulogovan je pokusao da pozove ovu metodu.\r\n");
					return gson.toJson(new Odgovor("Morate se ulogovati da biste nastavili. Uskoro ćete biti prebačeni na login stranicu."));
				}
				else if (res.status() == 500)	{ 
					System.out.println("DOBAVI KORISNIKA: Ne moze da parsira JWT.\r\n");
					return gson.toJson(new Odgovor("Došlo je do greške na serveru. Pokušajte ponovo."));
				}
				
				res.status(500);
				return gson.toJson(new Odgovor("Došlo je do greške na serveru. Pokušajte ponovo."));
			}
		});
		
		put("/app/izmeni_korisnika", (req, res) -> {
			Korisnik korisnik = proveraOvlascenja(req, res);
			
			if (korisnik != null)	{
				String payload = req.body();
				System.out.println("IZMENI KORISNIKA: " + payload + "\r\n");
				Korisnik izmenjenKorisnik = gson.fromJson(payload, Korisnik.class);
				if (korisnici.izmeniKorisnika(izmenjenKorisnik))	{
					System.out.println("IZMENI KORISNIKA: Korisnik " + izmenjenKorisnik.getKorisnickoIme() + " uspesno izmenjen i sacuvan.\r\n");
					return gson.toJson(new Odgovor("Nalog uspešno promenjen i sačuvan."));
				} else	{
					System.out.println("IZMENI KORISNIKA: Greska pri izmeni korisnika. Izmenjen korisnik nije sacuvan.\r\n");
					return gson.toJson(new Odgovor("Došlo je do greške prilikom izmene. Pokušajte ponovo."));
				}
			} else	{
				if (res.status() == 400)	{
					System.out.println("IZMENI KORISNIKA: Korisnik koji nije ulogovan je pokusao da pozove ovu metodu.\r\n");
					return gson.toJson(new Odgovor("Morate se ulogovati da biste nastavili. Uskoro ćete biti prebačeni na login stranicu."));
				}
				else if (res.status() == 500)	{ 
					System.out.println("IZMENI KORISNIKA: Ne moze da parsira JWT.\r\n");
					return gson.toJson(new Odgovor("Došlo je do greške na serveru. Pokušajte ponovo."));
				}
				
				res.status(500);
				return gson.toJson(new Odgovor("Došlo je do greške na serveru. Pokušajte ponovo."));
			}
		});
		
		post("/app/dodaj_sliku", (req, res) -> {
			Korisnik korisnik = proveraOvlascenja(req, res);
			
			if (korisnik != null) {
				String location = "image";          // the directory location where files will be stored
				long maxFileSize = 100000000;       // the maximum size allowed for uploaded files
				long maxRequestSize = 100000000;    // the maximum size allowed for multipart/form-data requests
				int fileSizeThreshold = 1024;       // the size threshold after which files will be written to disk

				MultipartConfigElement multipartConfigElement = new MultipartConfigElement(
				     location, maxFileSize, maxRequestSize, fileSizeThreshold);
				 req.raw().setAttribute("org.eclipse.jetty.multipartConfig",
				     multipartConfigElement);

				Collection<Part> parts = req.raw().getParts();
				for (Part part : parts) {
				   System.out.println("DODAJ SLIKU: Name: " + part.getName());
				   System.out.println("DODAJ SLIKU: Size: " + part.getSize());
				   System.out.println("DODAJ SLIKU: Filename: " + part.getSubmittedFileName());
				}

				String fName = req.raw().getPart("file").getSubmittedFileName();
				System.out.println("DODAJ SLIKU: Title: " + req.raw().getParameter("title"));
				System.out.println("DODAJ SLIKU: File: " + fName);

				Part uploadedFile = req.raw().getPart("file");
				Path out = Paths.get("static/slike/" + fName);
				try (final InputStream in = uploadedFile.getInputStream()) {
				   Files.copy(in, out);
				   uploadedFile.delete();
				   apartmani.dodajSliku(Integer.parseInt(req.headers("IdApartmana")), fName);
				}
				// cleanup
				multipartConfigElement = null;
				parts = null;
				uploadedFile = null;
				
				return gson.toJson(apartmani.dobaviApartman(Integer.parseInt(req.headers("IdApartmana"))));
			} else	{
				if (res.status() == 400)	{
					System.out.println("DODAJ SLIKU: Korisnik koji nije ulogovan je pokusao da pozove ovu metodu.\r\n");
					return gson.toJson(new Odgovor("Morate se ulogovati da biste nastavili. Uskoro ćete biti prebačeni na login stranicu."));
				}
				else if (res.status() == 500)	{ 
					System.out.println("DODAJ SLIKU: Ne moze da parsira JWT.\r\n");
					return gson.toJson(new Odgovor("Došlo je do greške na serveru. Pokušajte ponovo."));
				}
				
				res.status(500);
				return gson.toJson(new Odgovor("Došlo je do greške na serveru. Pokušajte ponovo."));
			}
		});
		
		get("/app/dobavi_korisnike_domacin", (req, res) -> {
			Korisnik korisnik = proveraOvlascenja(req, res);

			if (korisnik != null) {
				if (korisnik.getUloga().equals(Uloga.DOMACIN))	{
					Domacin domacin = (Domacin) korisnik;
					ArrayList<Integer> apart = domacin.getApartmani();
					ArrayList<String> gosti = rezervacije.sveKorisniciDomacin(apart);
					return gson.toJson(korisnici.sviKorisniciDomacin(gosti));
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
		
		get("/app/dobavi_rezervacije_gost", (req, res) -> {
			Korisnik korisnik = proveraOvlascenja(req, res);
			if (korisnik != null)	{
				if (korisnik.getUloga().equals(Uloga.GOST))	{
					//Gost gost = korisnici.ucitajRezervacije(korisnik.getKorisnickoIme());
					Gost gost = (Gost) korisnik;
					ArrayList<Rezervacija> rez = rezervacije.sveRezervacijeGosta(gost.getRezervacije());
					return gson.toJson(rez);
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
		
		get("/app/dobavi_rezervacije_domacin", (req, res) -> {
			Korisnik korisnik = proveraOvlascenja(req, res);
			if (korisnik != null)	{
				if (korisnik.getUloga().equals(Uloga.DOMACIN))	{
					//Domacin domacin = korisnici.ucitajApartmane(korisnik.getKorisnickoIme());
					Domacin domacin = (Domacin) korisnik;
					ArrayList<Rezervacija> rez = rezervacije.sveRezervacijeDomacin(domacin.getApartmani());
					return gson.toJson(rez);
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
		
		get("/app/dobavi_apartmane_gost", (req, res) -> {
			Korisnik korisnik = proveraOvlascenja(req, res);

			if (korisnik != null) {
				if (korisnik.getUloga().equals(Uloga.GOST))	{
					Gost gost = (Gost) korisnik;	
					return gson.toJson(apartmani.apartmaniGdeJeGostBio(rezervacije.sviApartmaniGosta(gost)));
				} else	{
					System.out.println("DOBAVI APARTMANE: Korisnik " + korisnik.getKorisnickoIme() + " nije ovlascen za ove podatke.\r\n");
					res.status(403); // Error 403: Forbidden
					return gson.toJson(new Odgovor("Niste ovlašćeni za traženi sadržaj."));
				}
			} else	{
				if (res.status() == 400)	{
					System.out.println("DOBAVI APARTMANE: Korisnik koji nije ulogovan je pokusao da pozove ovu metodu.\r\n");
					return gson.toJson(new Odgovor("Morate se ulogovati da biste nastavili. Uskoro ćete biti prebačeni na login stranicu."));
				} else if (res.status() == 500)	{
					System.out.println("DOBAVI APARTMANE: Ne moze da parsira JWT.\r\n");
					return gson.toJson(new Odgovor("Došlo je do greške na serveru. Pokušajte ponovo."));
				}

				res.status(500);
				return gson.toJson(new Odgovor("Došlo je do greške na serveru. Pokušajte ponovo."));
			}
		});
		
		delete("/app/obrisi_sadrzaj_apartmana", (req, res) -> {
			Korisnik korisnik = proveraOvlascenja(req, res);

			if (korisnik != null)	{
				if (korisnik.getUloga().equals(Uloga.ADMINISTRATOR))	{
					String payload = req.body();
					System.out.println("OBRISI SADRZAJ: " + payload + "\r\n");
					SadrzajApartmana sadrzajZaBrisanje = gson.fromJson(payload, SadrzajApartmana.class);
					if (sadrzaji.obrisiSadrzajApartmana(sadrzajZaBrisanje.getId()))	{
						System.out.println("OBRISI SADRZAJ: Sadrzaj " + sadrzajZaBrisanje.getId() + " uspesno obrisan iz baze.\r\n");
						return gson.toJson(sadrzaji.sviSadrzaji());
					} else	{
						System.out.println("OBRISI SADRZAJ: " + sadrzajZaBrisanje.getId() + " nije pronadjen u bazi.\r\n");
						res.status(404); // Error 404: Not Found
						return gson.toJson(new Odgovor("Traženi resurs nije pronađen."));
					}
				} else	{
					System.out.println("OBRISI SADRZAJ: Korisnik " + korisnik.getKorisnickoIme() + " nije ovlascen za ovu metodu.\r\n");
					res.status(403); // Error 403: Forbidden
					return gson.toJson(new Odgovor("Niste ovlašćeni za traženi sadržaj."));
				}
			} else	{
				if (res.status() == 400)	{
					System.out.println("OBRISI SADRZAJ: Korisnik koji nije ulogovan je pokusao da pozove ovu metodu.\r\n");
					return gson.toJson(new Odgovor("Morate se ulogovati da biste nastavili. Uskoro ćete biti prebačeni na login stranicu."));
				}
				else if (res.status() == 500)	{ 
					System.out.println("OBIRIS SADRZAJ: Ne moze da parsira JWT.\r\n");
					return gson.toJson(new Odgovor("Došlo je do greške na serveru. Pokušajte ponovo."));
				}

				res.status(500);
				return gson.toJson(new Odgovor("Došlo je do greške na serveru. Pokušajte ponovo."));
			}
		});
		
		post("/app/dodavanje_sadrzaja", (req, res) ->	{
			res.type("application/json");
			Korisnik korisnik = proveraOvlascenja(req, res);
			String payload = req.body();
			System.out.println("DODAVANJE SADRZAJA: " + payload);
			SadrzajApartmana noviSadrzaj = gson.fromJson(payload, SadrzajApartmana.class);
			if (korisnik != null)	{
				if (korisnik.getUloga().equals(Uloga.ADMINISTRATOR))	{

					if (noviSadrzaj != null)	{

						sadrzaji.dodajNoviSadrzaj(noviSadrzaj);
						System.out.println("DODAVANJE SADRZAJA: " + noviSadrzaj.getNaziv());
						System.out.println("Dodavanje sadrzaja: Sadrzaj " + noviSadrzaj.getNaziv() + " uspesno dodat.\r\n");
						return gson.toJson(sadrzaji.sviSadrzaji());

					} else	{
						System.out.println("DODAVANJE SADRZAJA: Objekat sadrzaj ne moze da se kreira.\r\n");
						res.status(500);	// Error 500: Internal Server Error - iz nekog razloga ne moze da parsira JSON objekat
						return gson.toJson(new Odgovor("Greška prilikom dodavanja sadrzaja. Pokušajte ponovo."));
					}
				} else	{
					System.out.println("DODAVANJE SADRZAJA: Korisnik " + korisnik.getKorisnickoIme() + " nije ovlascen za ove podatke.\r\n");
					res.status(403); // Error 403: Forbidden
					return gson.toJson(new Odgovor("Niste ovlašćeni za traženi sadržaj."));
				}
			} else	{
				if (res.status() == 400)	{
					System.out.println("DODAVANJE SADRZAJA: Korisnik koji nije ulogovan je pokusao da pozove ovu metodu.\r\n");
					return gson.toJson(new Odgovor("Morate se ulogovati da biste nastavili. Uskoro ćete biti prebačeni na login stranicu."));
				} else if (res.status() == 500)	{
					System.out.println("DODAVANJA SADRZAJA: Ne moze da parsira JWT.\r\n");
					return gson.toJson(new Odgovor("Došlo je do greške na serveru. Pokušajte ponovo."));
				}

				res.status(500);
				return gson.toJson(new Odgovor("Došlo je do greške na serveru. Pokušajte ponovo."));
			}

		});
		
		post("/app/dodavanje_komentara", (req, res) ->	{
			res.type("application/json");
			Korisnik korisnik = proveraOvlascenja(req, res);
			String payload = req.body();
			System.out.println("DODAVANJE KOMENTARA: " + payload);
			Komentar noviKomentar = gson.fromJson(payload, Komentar.class);
			if (korisnik != null)	{
				if (korisnik.getUloga().equals(Uloga.GOST))	{

					if (noviKomentar != null)	{
						System.out.println("DODAVANJE KOMENTARA: " + noviKomentar.getId() + " " + noviKomentar.getKorisnickoImeGosta());
						noviKomentar.setKorisnickoImeGosta(korisnik.getKorisnickoIme());
						komentari.dodajNoviKomentar(noviKomentar);
						System.out.println("Dodavanje komentara: Komentar " + noviKomentar.getId() + " uspesno dodat.\r\n");
						return gson.toJson(new Odgovor("Komentar uspešno dodat."));

					} else	{
						System.out.println("DODAVANJE KOMENTARA: Objekat apartman ne moze da se kreira.\r\n");
						res.status(500);	// Error 500: Internal Server Error - iz nekog razloga ne moze da parsira JSON objekat
						return gson.toJson(new Odgovor("Greška prilikom dodavanja apartmana. Pokušajte ponovo."));
					}
				} else	{
					System.out.println("DODAVANJE KOMENTARA: Korisnik " + korisnik.getKorisnickoIme() + " nije ovlascen za ove podatke.\r\n");
					res.status(403); // Error 403: Forbidden
					return gson.toJson(new Odgovor("Niste ovlašćeni za traženi sadržaj."));
				}
			} else	{
				if (res.status() == 400)	{
					System.out.println("DODAVANJE KOMENTARA: Korisnik koji nije ulogovan je pokusao da pozove ovu metodu.\r\n");
					return gson.toJson(new Odgovor("Morate se ulogovati da biste nastavili. Uskoro ćete biti prebačeni na login stranicu."));
				} else if (res.status() == 500)	{
					System.out.println("DODAVANJA APARTMANA: Ne moze da parsira JWT.\r\n");
					return gson.toJson(new Odgovor("Došlo je do greške na serveru. Pokušajte ponovo."));
				}

				res.status(500);
				return gson.toJson(new Odgovor("Došlo je do greške na serveru. Pokušajte ponovo."));
			}

		});
		
		put("/app/izmeni_sadrzaj_apartmana", (req, res) -> {
			Korisnik korisnik = proveraOvlascenja(req, res);

			if (korisnik != null)	{
				if (korisnik.getUloga().equals(Uloga.ADMINISTRATOR))	{
					String payload = req.body();
					System.out.println("IZMENI SADRZAJ APARTMANA: " + payload + "\r\n");
					SadrzajApartmana[] izmenjenSadrzajApartmana = gson.fromJson(payload,SadrzajApartmana[].class);
					ArrayList<SadrzajApartmana> izmenjenSadrzaj = new ArrayList<SadrzajApartmana>(Arrays.asList(izmenjenSadrzajApartmana));
					if (sadrzaji.izmeniSadrzajApartman(izmenjenSadrzaj))	{
						System.out.println("IZMENI SADRZAJ APARTMANA: uspesno izmenjen i sacuvan.\r\n");
						return gson.toJson(new Odgovor("Sadrzaj apartmana uspešno izmenjen."));
					} else	{
						System.out.println("IZMENI SADRZAJ APARTMANA: Greska pri izmeni sadrzaja apartmana. Izmenjen sadrzaj apartmana nije sacuvan.\r\n");
						return gson.toJson(new Odgovor("Došlo je do greške prilikom izmene. Pokušajte ponovo."));
					}
				} else	{
					System.out.println("IZMENI SADRZAJ APARTMANA: Korisnik " + korisnik.getKorisnickoIme() + " nije ovlascen za ovu metodu.\r\n");
					res.status(403); // Error 403: Forbidden
					return gson.toJson(new Odgovor("Niste ovlašćeni za traženi sadržaj."));
				}
			} else	{
				if (res.status() == 400)	{
					System.out.println("IZMENI SADRZAJ APARTMANA: Korisnik koji nije ulogovan je pokusao da pozove ovu metodu.\r\n");
					return gson.toJson(new Odgovor("Morate se ulogovati da biste nastavili. Uskoro ćete biti prebačeni na login stranicu."));
				}
				else if (res.status() == 500)	{ 
					System.out.println("IZMENI SADRZAJ APARTMANA: Ne moze da parsira JWT.\r\n");
					return gson.toJson(new Odgovor("Došlo je do greške na serveru. Pokušajte ponovo."));
				}

				res.status(500);
				return gson.toJson(new Odgovor("Došlo je do greške na serveru. Pokušajte ponovo."));
			}
		});
		
		put("/app/promeni_status_rezervacije", (req, res) -> {
			Korisnik korisnik = proveraOvlascenja(req, res);

			if (korisnik != null)	{
				if (korisnik.getUloga().equals(Uloga.GOST) || korisnik.getUloga().equals(Uloga.DOMACIN))	{
					String payload = req.body();
					System.out.println("IZMENI REZERVACIJU: " + payload + "\r\n");
					Rezervacija izmenjenaRezervacija = gson.fromJson(payload, Rezervacija.class);
					if (rezervacije.promeniStatusRezervacije(izmenjenaRezervacija.getId(), izmenjenaRezervacija.getStatus()))	{
						if (izmenjenaRezervacija.getStatus().equals(StatusRezervacije.ODUSTANAK) || izmenjenaRezervacija.getStatus().equals(StatusRezervacije.ODBIJENA))	{
							//apartmani.oslobodiDatume(izmenjenaRezervacija.getApartmanId(), izmenjenaRezervacija.getPocetniDatum(), izmenjenaRezervacija.getBrojNocenja());
							apartmani.dobaviApartman(izmenjenaRezervacija.getApartmanId()).setZauzetiDatumi(rezervacije.zauzetiDatumiApartmana(izmenjenaRezervacija.getApartmanId()));
						}
						// TODO: ovo sam dodao sinoc, mozda ne valja
						else if (izmenjenaRezervacija.getStatus().equals(StatusRezervacije.ZAVRSENA))	{
							Gost gost = (Gost) korisnici.dobaviKorisnika(izmenjenaRezervacija.getKorisnickoImeGosta());
							gost.getIznajmljeniApartmani().add(izmenjenaRezervacija.getApartmanId());
						}
						System.out.println("IZMENI REZERVACIJU: Rezervacija id:" + izmenjenaRezervacija.getId() + " uspesno izmenjena i sacuvana.\r\n");
						return gson.toJson(new Odgovor("Status rezervacije promenjen."));
					} else	{
						System.out.println("IZMENI REZERVACIJU: Greska pri izmeni rezervacije. Izmenjena rezervacija nije sacuvana.\r\n");
						return gson.toJson(new Odgovor("Došlo je do greške prilikom izmene. Pokušajte ponovo."));
					}
				} else	{
					System.out.println("IZMENI REZERVACIJU: Korisnik " + korisnik.getKorisnickoIme() + " nije ovlascen za ovu metodu.\r\n");
					res.status(403); // Error 403: Forbidden
					return gson.toJson(new Odgovor("Niste ovlašćeni za traženi sadržaj."));
				}
			} else	{
				if (res.status() == 400)	{
					System.out.println("IZMENI REZERVACIJU: Korisnik koji nije ulogovan je pokusao da pozove ovu metodu.\r\n");
					return gson.toJson(new Odgovor("Morate se ulogovati da biste nastavili. Uskoro ćete biti prebačeni na login stranicu."));
				}
				else if (res.status() == 500)	{ 
					System.out.println("IZMENI REZERVACIJU: Ne moze da parsira JWT.\r\n");
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
