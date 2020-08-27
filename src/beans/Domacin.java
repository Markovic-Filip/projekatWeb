package beans;

import java.util.ArrayList;

import enums.Pol;
import enums.StatusKorisnika;
import enums.Uloga;

public class Domacin extends Korisnik {

	// TODO:private List<Apartman> apartmani;
	private ArrayList<Integer> apartmani;
	private StatusKorisnika status;
	
	public Domacin() {
		super();
		this.uloga = Uloga.DOMACIN;
		this.apartmani = new ArrayList<Integer>();
		this.status = StatusKorisnika.AKTIVAN;
	}
	
	public Domacin(String korisnickoIme, String lozinka, String ime, String prezime, Pol pol, StatusKorisnika status)	{
		super(korisnickoIme, lozinka, ime, prezime, pol);
		this.uloga = Uloga.DOMACIN;
		this.apartmani = new ArrayList<Integer>();
		this.status = status;
	}

	public ArrayList<Integer> getApartmani() {
		return apartmani;
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
