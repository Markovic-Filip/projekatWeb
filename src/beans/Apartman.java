package beans;


import java.util.ArrayList;
import java.util.Date;

import javax.json.bind.annotation.JsonbDateFormat;

import enums.Status;
import enums.Tip;

public class Apartman {
	
	protected int id;
	protected Tip tip;
	protected int brojSoba;
	protected int brojGostiju;
	protected Lokacija lokacija;
	@JsonbDateFormat(JsonbDateFormat.TIME_IN_MILLIS)
	protected ArrayList<Date> zauzetiDatumi;
	protected String korisnickoImeDomacina;
	protected ArrayList<String> imenaSlika;
	protected ArrayList<String> slike;
	protected double cenaPoNoci;
	protected int vremeZaPrijavu;
	protected int vremeZaOdjavu;
	protected Status status;
	protected ArrayList<Integer> idSadrzaja;
	protected ArrayList<Integer> idRezervacije;
	
	public Apartman() {
		
		this.vremeZaPrijavu = 14;
		this.vremeZaOdjavu = 10;
		this.status = Status.NEAKTIVNO;
		this.idSadrzaja = new ArrayList<Integer>();
		this.idRezervacije = new ArrayList<Integer>();
		this.slike = new ArrayList<String>();
		this.imenaSlika = new ArrayList<String>();
		this.zauzetiDatumi = new ArrayList<Date>();
		
	}
	
	public Apartman(int id, Tip tip, int brojSoba,int brojGostiju, Lokacija lokacija,
			String korisnickoImeDomacina, double cenaPoNoci, int vremeZaPrijavu, int vremeZaOdjavu, Status status,
			ArrayList<Integer> idSadrzaja, ArrayList<Integer> idRezervacije, ArrayList<String> imenaSlika, ArrayList<String> slike) {
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
		this.imenaSlika = imenaSlika;
		this.slike = slike;
		
		
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

	public ArrayList<Date> getZauzetiDatumi() {
		return zauzetiDatumi;
	}

	public void setZauzetiDatumi(ArrayList<Date> zauzetiDatumi) {
		this.zauzetiDatumi = zauzetiDatumi;
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

	public ArrayList<String> getImenaSlika() {
		return imenaSlika;
	}

	public void setImenaSlika(ArrayList<String> imenaSlika) {
		this.imenaSlika = imenaSlika;
	}

	public ArrayList<String> getSlike() {
		return slike;
	}

	public void setSlike(ArrayList<String> slike) {
		this.slike = slike;
	}

	@Override
	public String toString() {
		String r =  this.id + ";" + this.tip.name() + ";" + this.brojSoba + ";" + this.brojGostiju + ";" + this.lokacija.toString() +
				";" + this.korisnickoImeDomacina + ";" + this.cenaPoNoci + ";" + this.vremeZaPrijavu +
				";" + this.vremeZaOdjavu + ";" + this.status.name() + ";" + "sadrzaji,"; 
				
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
		
		if (idSadrzaja.size() == 0)
			r += ";";
		
		r += "rezervacije,";
		
		brojac = 0;
		for (int i : idRezervacije) {
			brojac++;
			r += i;
			if(brojac < idRezervacije.size()) {
				r += ",";
			}else {
				r += ";"; 
			}
		}
		
		if (idRezervacije.size() == 0)
			r += ";";
		
		r += "slike,";
		
		brojac = 0;
		for (String ime : imenaSlika)	{
			brojac++;
			r += ime;
			if(brojac < imenaSlika.size())	{
				r += ",";
			}else {
				//r += ";";
			}
		}
		
		//if (imenaSlika.size() == 0)
		//	r += ";";

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
