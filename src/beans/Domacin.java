package beans;

import java.util.ArrayList;

import enums.Pol;
import enums.Uloga;

public class Domacin extends Korisnik {

	// TODO:private List<Apartman> apartmani;
	private ArrayList<Integer> apartmani;
	
	public Domacin() {
		super();
		this.uloga = Uloga.DOMACIN;
		this.apartmani = new ArrayList<Integer>();
	}
	
	public Domacin(String korisnickoIme, String lozinka, String ime, String prezime, Pol pol)	{
		super(korisnickoIme, lozinka, ime, prezime, pol);
		this.uloga = Uloga.DOMACIN;
		this.apartmani = new ArrayList<Integer>();
	}

	public ArrayList<Integer> getApartmani() {
		return apartmani;
	}
}
