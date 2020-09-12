package beans;


import java.util.ArrayList;
import java.util.Date;

import enums.Status;
import enums.Tip;

public class Apartman {
	
	protected int id;
	protected Tip tip;
	protected int brojSoba;
	protected int brojGostiju;
	protected Lokacija lokacija;
	//ovo jos nisam siguran kako cemo odraditi 
	//protected Date date;
	//protected dostupnost po datumima
	protected String korisnickoImeDomacina;
	//protected komentari
	//protected slike
	protected double cenaPoNoci;
	protected int vremeZaPrijavu;
	protected int vremeZaOdjavu;
	protected Status status;
	//protected SadrzajApartmana sadrzajApartmana;
	protected ArrayList<Integer> idSadrzaja;
	// TODO: predlazem da polje rezervacije bude lista int-ova i da svaki int predstavlja kljuc (odnosno id) rezervacije koja se nalazi u RezervacijeDAO, isto vazi i za polje Domacin
	//protected rezervacije
	protected ArrayList<Integer> idRezervacije;
	
	public Apartman() {
		
		this.vremeZaPrijavu = 14;
		this.vremeZaOdjavu = 10;
		this.status = Status.NEAKTIVNO;
		this.idSadrzaja = new ArrayList<Integer>();
		this.idRezervacije = new ArrayList<Integer>();
		
	}
	
	public Apartman(int id, Tip tip, int brojSoba,int brojGostiju, Lokacija lokacija, /*Date date, dostuponost po datumima*/
			String korisnickoImeDomacina,/*komentari, slike*/ double cenaPoNoci, int vremeZaPrijavu, int vremeZaOdjavu, Status status,
			ArrayList<Integer> idSadrzaja, ArrayList<Integer> idRezervacije) {
		super();
		this.id = id;
		this.tip = tip;
		this.brojSoba = brojSoba;
		this.brojGostiju = brojGostiju;
		this.lokacija = lokacija;
		this.korisnickoImeDomacina = korisnickoImeDomacina;
		this.cenaPoNoci = cenaPoNoci;
		this.vremeZaPrijavu = vremeZaPrijavu;
		this.vremeZaOdjavu = vremeZaOdjavu;
		this.status = status;
		this.idSadrzaja = idSadrzaja;
		this.idRezervacije = idRezervacije;
		
		
		
	}

	public Tip getTip() {
		return tip;
	}

	public void setTip(Tip tip) {
		this.tip = tip;
	}

	public int getBrojSoba() {
		return brojSoba;
	}

	public void setBrojSoba(int brojSoba) {
		this.brojSoba = brojSoba;
	}

	public int getBrojGostiju() {
		return brojGostiju;
	}

	public void setBrojGostiju(int brojGostiju) {
		this.brojGostiju = brojGostiju;
	}

	public Lokacija getLokacija() {
		return lokacija;
	}

	public void setLokacija(Lokacija lokacija) {
		this.lokacija = lokacija;
	}

	

	

	public double getCenaPoNoci() {
		return cenaPoNoci;
	}

	public void setCenaPoNoci(double cenaPoNoci) {
		this.cenaPoNoci = cenaPoNoci;
	}

	public int getVremeZaPrijavu() {
		return vremeZaPrijavu;
	}

	public void setVremeZaPrijavu(int vremeZaPrijavu) {
		this.vremeZaPrijavu = vremeZaPrijavu;
	}

	public int getVremeZaOdjavu() {
		return vremeZaOdjavu;
	}

	public void setVremeZaOdjavu(int vremeZaOdjavu) {
		this.vremeZaOdjavu = vremeZaOdjavu;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ArrayList<Integer> getIdSadrzaja() {
		return idSadrzaja;
	}

	public void setIdSadrzaja(ArrayList<Integer> idSadrzaja) {
		this.idSadrzaja = idSadrzaja;
	}

	public ArrayList<Integer> getIdRezervacije() {
		return idRezervacije;
	}

	public void setIdRezervacije(ArrayList<Integer> idRezervacije) {
		this.idRezervacije = idRezervacije;
	}

	@Override
	public String toString() {
		String r =  this.id + ";" + this.tip.name() + ";" + this.brojSoba + ";" + this.brojGostiju + ";" + this.lokacija.toString() +
				";" + this.korisnickoImeDomacina + ";" + this.cenaPoNoci + ";" + this.vremeZaPrijavu +
				";" + this.vremeZaOdjavu + ";" + this.status.name() + ";"; 
				
		int brojac = 0;
		for (int i : idSadrzaja) {
			brojac++;
			r += i;
			if(brojac < idSadrzaja.size()) {
				r += ",";
			}else {
				r+= ";";
			}
			
			
		}
		brojac = 0;
		for (int i : idRezervacije) {
			brojac++;
			r += i;
			if(brojac < idRezervacije.size()) {
				r += ",";
			}else {
			}
		}
		r+="\n";
		
		return r;
	
	}

	public String getKorisnickoImeDomacina() {
		return korisnickoImeDomacina;
	}

	public void setKorisnickoImeDomacina(String korisnickoImeDomacina) {
		this.korisnickoImeDomacina = korisnickoImeDomacina;
	}

	
}
