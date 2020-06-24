package beans;

import enums.Pol;
import enums.Uloga;

public class Domacin extends Korisnik {

	// TODO:private List<Apartman> apartmani;
	
	public Domacin() {
		super();
		this.uloga = Uloga.DOMACIN;
	}
	
	public Domacin(String korisnickoIme, String lozinka, String ime, String prezime, Pol pol)	{
		super(korisnickoIme, lozinka, ime, prezime, pol);
		this.uloga = Uloga.DOMACIN;
	}
}
