package beans;

import java.util.Date;

import enums.StatusRezervacije;

public class Rezervacija {

	private Apartman apartman;
	//@JsonbDateFormat(JsonbDateFormat.TIME_IN_MILLIS)
	private Date pocetniDatum;
	private int brojNocenja;
	private double cena;
	private String poruka;
	private Gost gost;
	private StatusRezervacije status;
	
	public Rezervacija()	{}

	public Rezervacija(Apartman apartman, Date pocetniDatum, int brojNocenja, double cena, String poruka, Gost gost, StatusRezervacije status) {
		super();
		this.apartman = apartman;
		this.pocetniDatum = pocetniDatum;
		this.brojNocenja = brojNocenja;
		this.cena = cena;
		this.poruka = poruka;
		this.gost = gost;
		this.status = status;
	}

	public Apartman getApartman() {
		return apartman;
	}

	public void setApartman(Apartman apartman) {
		this.apartman = apartman;
	}

	public Date getPocetniDatum() {
		return pocetniDatum;
	}

	public void setPocetniDatum(Date pocetniDatum) {
		this.pocetniDatum = pocetniDatum;
	}

	public int getBrojNocenja() {
		return brojNocenja;
	}

	public void setBrojNocenja(int brojNocenja) {
		this.brojNocenja = brojNocenja;
	}

	public double getCena() {
		return cena;
	}

	public void setCena(double cena) {
		this.cena = cena;
	}

	public String getPoruka() {
		return poruka;
	}

	public void setPoruka(String poruka) {
		this.poruka = poruka;
	}

	public Gost getGost() {
		return gost;
	}

	public void setGost(Gost gost) {
		this.gost = gost;
	}

	public StatusRezervacije getStatus() {
		return status;
	}

	public void setStatus(StatusRezervacije status) {
		this.status = status;
	}
}
