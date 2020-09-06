package beans;

import java.util.Date;

import javax.json.bind.annotation.JsonbDateFormat;

import enums.StatusRezervacije;

public class Rezervacija {

	private int id;
	private int apartmanId;
	@JsonbDateFormat(JsonbDateFormat.TIME_IN_MILLIS)
	private Date pocetniDatum;
	private int brojNocenja;
	private double cena;
	private String poruka;
	private String korisnickoImeGosta;
	private StatusRezervacije status;
	
	public Rezervacija()	{
		pocetniDatum = new Date();
	}

	public Rezervacija(int id, int apartmanId, Date pocetniDatum, int brojNocenja, double cena, String poruka, String korisnickoImeGosta, StatusRezervacije status) {
		super();
		this.id = id;
		this.apartmanId = apartmanId;
		this.pocetniDatum = pocetniDatum;
		this.brojNocenja = brojNocenja;
		this.cena = cena;
		this.poruka = poruka;
		this.korisnickoImeGosta = korisnickoImeGosta;
		this.status = status;
	}

	public int getId()	{
		return id;
	}
	
	public void setId(int id)	{
		this.id = id;
	}
	
	public int getApartmanId() {
		return apartmanId;
	}

	public void setApartman(int apartmanId) {
		this.apartmanId = apartmanId;
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

	public String getKorisnickoImeGosta() {
		return korisnickoImeGosta;
	}

	public void setGost(String korisnickoImeGosta) {
		this.korisnickoImeGosta = korisnickoImeGosta;
	}

	public StatusRezervacije getStatus() {
		return status;
	}

	public void setStatus(StatusRezervacije status) {
		this.status = status;
	}

	public Date getKrajnjiDatum()	{
		return new Date(this.pocetniDatum.getTime() + 86400 * 1000 * this.brojNocenja);
	}
	
	@Override
	public String toString() {
		return this.id + ";" + this.apartmanId + ";" + this.pocetniDatum.getTime() + ";" + this.brojNocenja + ";" + this.cena + ";" + this.poruka + ";" + this.korisnickoImeGosta + ";" + this.status.name() + "\n";
	}
}
