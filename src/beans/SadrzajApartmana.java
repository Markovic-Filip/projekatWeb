package beans;

public class SadrzajApartmana {
	
	protected int id;
	protected String naziv;
	
	public SadrzajApartmana() {}

	public SadrzajApartmana( int id, String naziv ) {
		this.id = id;
		this.naziv = naziv;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNaziv() {
		return naziv;
	}

	public void setNaziv(String naziv) {
		this.naziv = naziv;
	}
}
