package beans;

import java.util.ArrayList;

import enums.Pol;
import enums.StatusKorisnika;
import enums.Uloga;

public class Gost extends Korisnik {

	// TODO: Iznajmljeni apartmani
	private ArrayList<Integer> iznajmljeniApartmani;
	// TODO: Lista rezervacija
	private ArrayList<Integer> rezervacije;
	private StatusKorisnika status;
	
	public Gost() {
		super();
		this.uloga = Uloga.GOST;
		this.iznajmljeniApartmani = new ArrayList<Integer>();
		this.rezervacije = new ArrayList<Integer>();
		this.status = StatusKorisnika.AKTIVAN;
	}
	
	public Gost(String korisnickoIme, String lozinka, String ime, String prezime, Pol pol, StatusKorisnika status)	{
		super(korisnickoIme, lozinka, ime, prezime, pol);
		this.uloga = Uloga.GOST;
		this.iznajmljeniApartmani = new ArrayList<Integer>();
		this.rezervacije = new ArrayList<Integer>();
		this.status = status;
	}

	public ArrayList<Integer> getIznajmljeniApartmani() {
		return iznajmljeniApartmani;
	}

	public ArrayList<Integer> getRezervacije() {
		return rezervacije;
	}

	public StatusKorisnika getStatus() {
		return status;
	}

	public void setStatus(StatusKorisnika status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return this.korisnickoIme + ";" + this.lozinka + ";" + this.ime + ";" + this.prezime + ";" + this.pol.name() + ";" + this.status.name() + "\n";
	}
}
