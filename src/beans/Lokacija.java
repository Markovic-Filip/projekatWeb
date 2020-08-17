package beans;

public class Lokacija {

	protected double geografskaSirina;
	protected double geografskaDuzina;
	protected Adresa adresa;
	
	public Lokacija() {}
	
	public Lokacija(double geografskaSirina, double geografskaDuzina, Adresa adresa) {
		
		this.geografskaSirina = geografskaSirina;
		this.geografskaDuzina = geografskaDuzina;
		this.adresa = adresa;
	}

	public double getGeografskaSirina() {
		return geografskaSirina;
	}

	public void setGeografskaSirina(double geografskaSirina) {
		this.geografskaSirina = geografskaSirina;
	}

	public double getGeografksaDuzina() {
		return geografskaDuzina;
	}

	public void setGeografksaDuzina(double geografksaDuzina) {
		this.geografskaDuzina = geografksaDuzina;
	}

	public Adresa getAdresa() {
		return adresa;
	}

	public void setAdresa(Adresa adresa) {
		this.adresa = adresa;
	}

	@Override
	public String toString() {
		return adresa + " " + geografskaSirina + ", " + geografskaDuzina;
	}
}
