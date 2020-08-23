package beans;

import java.util.ArrayList;

import enums.Pol;
import enums.Uloga;

public class Gost extends Korisnik {

	// TODO: Iznajmljeni apartmani
	private ArrayList<Integer> iznajmljeniApartmani;
	// TODO: Lista rezervacija
	private ArrayList<Integer> rezervacije;
	
	public Gost() {
		super();
		this.uloga = Uloga.GOST;
		this.iznajmljeniApartmani = new ArrayList<Integer>();
		this.rezervacije = new ArrayList<Integer>();
	}
	
	public Gost(String korisnickoIme, String lozinka, String ime, String prezime, Pol pol)	{
		super(korisnickoIme, lozinka, ime, prezime, pol);
		this.uloga = Uloga.GOST;
		this.iznajmljeniApartmani = new ArrayList<Integer>();
		this.rezervacije = new ArrayList<Integer>();
	}

	public ArrayList<Integer> getIznajmljeniApartmani() {
		return iznajmljeniApartmani;
	}

	public ArrayList<Integer> getRezervacije() {
		return rezervacije;
	}
}
