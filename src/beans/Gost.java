package beans;

import enums.Pol;
import enums.Uloga;

public class Gost extends Korisnik {

	private final Uloga uloga;
	// TODO: Iznajmljeni apartmani
	// TODO: Lista rezervacija
	
	public Gost() {
		super();
		this.uloga = Uloga.GOST;
	}
	
	public Gost(String korisnickoIme, String lozinka, String ime, String prezime, Pol pol)	{
		super(korisnickoIme, lozinka, ime, prezime, pol);
		this.uloga = Uloga.GOST;
	}

	public Uloga getUloga() {
		return uloga;
	}
}
