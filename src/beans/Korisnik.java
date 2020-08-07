package beans;

import enums.Pol;
import enums.Uloga;

public class Korisnik {

	protected String korisnickoIme;
	protected String lozinka;
	protected String ime;
	protected String prezime;
	protected Pol pol;
	protected Uloga uloga;
	protected String JWTToken;
	
	public Korisnik() {	}
	
	public Korisnik(String korisnickoIme, String lozinka, String ime, String prezime, Pol pol)	{
		
		this.korisnickoIme = korisnickoIme;
		this.lozinka = lozinka;
		this.ime = ime;
		this.prezime = prezime;
		this.pol = pol;
	}

	public String getKorisnickoIme() {
		return korisnickoIme;
	}

	public void setKorisnickoIme(String korisnickoIme) {
		this.korisnickoIme = korisnickoIme;
	}

	public String getLozinka() {
		return lozinka;
	}

	public void setLozinka(String lozinka) {
		this.lozinka = lozinka;
	}

	public String getIme() {
		return ime;
	}

	public void setIme(String ime) {
		this.ime = ime;
	}

	public String getPrezime() {
		return prezime;
	}

	public void setPrezime(String prezime) {
		this.prezime = prezime;
	}

	public Pol getPol() {
		return pol;
	}

	public void setPol(Pol pol) {
		this.pol = pol;
	}
	
	public Uloga getUloga()	{
		return uloga;
	}
	
	public String getJWTToken() {
		return JWTToken;
	}

	public void setJWTToken(String jWTToken) {
		JWTToken = jWTToken;
	}

	@Override
	public String toString() {
		return this.korisnickoIme + ";" + this.lozinka + ";" + this.ime + ";" + this.prezime + ";" + this.pol.name() + "\n";
	}
}
