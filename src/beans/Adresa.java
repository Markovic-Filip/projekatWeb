package beans;

public class Adresa {
	
	protected String ulica;
	protected int broj;
	protected String mesto;
	protected int postanskiBroj;
	
	public Adresa() {}
	
	public Adresa(String ulica, int broj, String mesto, int postanskiBroj) {
		
		this.ulica = ulica;
		this.broj = broj;
		this.mesto = mesto;
		this.postanskiBroj = postanskiBroj;
	}

	public String getUlica() {
		return ulica;
	}

	public void setUlica(String ulica) {
		this.ulica = ulica;
	}

	public int getBroj() {
		return broj;
	}

	public void setBroj(int broj) {
		this.broj = broj;
	}

	public String getMesto() {
		return mesto;
	}

	public void setMesto(String mesto) {
		this.mesto = mesto;
	}

	public int getPostanskiBroj() {
		return postanskiBroj;
	}

	public void setPostanskiBroj(int postanskiBroj) {
		this.postanskiBroj = postanskiBroj;
	}

	@Override
	public String toString() {
		return  ulica + "," + broj + "," + mesto + "," + postanskiBroj;
	}

}
