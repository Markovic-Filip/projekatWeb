package beans;

import enums.Pol;
import enums.Uloga;

public class Administrator extends Korisnik {

	private final Uloga uloga;
	
	public Administrator() {
		super();
		this.uloga = Uloga.ADMINISTRATOR;
	}
	
	public Administrator(String korisnickoIme, String lozinka, String ime, String prezime, Pol pol) {
		super(korisnickoIme, lozinka, ime, prezime, pol);
		this.uloga = Uloga.ADMINISTRATOR;
	}

	public Uloga getUloga() {
		return uloga;
	}
	
	//public static void main(String[] args) {
	//	Administrator ad = new Administrator("admin", "admin", "admin", "admin", Pol.MUSKO);
	//	System.out.println(ad.getLozinka());
	//}
}
