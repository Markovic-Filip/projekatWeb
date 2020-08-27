package beans;

import java.util.Date;

import enums.Status;
import enums.Tip;

public class Apartman {

	protected Tip tip;
	protected int brojSoba;
	protected int brojGostiju;
	protected Lokacija lokacija;
	protected Date date;
	//protected dostupnost po datumima
	protected Domacin domacin;
	//protected komentari
	//protected slike
	protected double cenaPoNoci;
	protected Date vremeZaPrijavu;
	protected Date vremeZaOdjavu;
	protected Status status;
	protected SadrzajApartmana sadrzajApartmana;

	// TODO: predlazem da polje rezervacije bude lista int-ova i da svaki int predstavlja kljuc (odnosno id) rezervacije koja se nalazi u RezervacijeDAO, isto vazi i za polje Domacin

	//protected rezervacije
	
	public Apartman() {
		
		
		
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

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Domacin getDomacin() {
		return domacin;
	}

	public void setDomacin(Domacin domacin) {
		this.domacin = domacin;
	}

	public double getCenaPoNoci() {
		return cenaPoNoci;
	}

	public void setCenaPoNoci(double cenaPoNoci) {
		this.cenaPoNoci = cenaPoNoci;
	}

	public Date getVremeZaPrijavu() {
		return vremeZaPrijavu;
	}

	public void setVremeZaPrijavu(Date vremeZaPrijavu) {
		this.vremeZaPrijavu = vremeZaPrijavu;
	}

	public Date getVremeZaOdjavu() {
		return vremeZaOdjavu;
	}

	public void setVremeZaOdjavu(Date vremeZaOdjavu) {
		this.vremeZaOdjavu = vremeZaOdjavu;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public SadrzajApartmana getSadrzajApartmana() {
		return sadrzajApartmana;
	}

	public void setSadrzajApartmana(SadrzajApartmana sadrzajApartmana) {
		this.sadrzajApartmana = sadrzajApartmana;
	}
	
}
