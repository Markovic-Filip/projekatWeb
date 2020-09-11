package beans;

public class Komentar {

	protected int id;
	protected String korisnickoImeGosta;
	protected int apartman;
	protected String tekst;
	protected int ocena;
	//TODO mozda dodati bool ili enumeraciju da se vidi da li ga je domacina odobrio ili nije?
	
	public Komentar() {
		
		
	}
	
	public Komentar(int id, String korisnickoImeGosta, int apartman, String tekst, int ocena) {
		this.id = id;
		this.korisnickoImeGosta = korisnickoImeGosta;
		this.apartman = apartman;
		this.tekst = tekst;
		this.ocena = ocena;
		
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getKorisnickoImeGosta() {
		return korisnickoImeGosta;
	}

	public void setKorisnickoImeGosta(String korisnickoImeGosta) {
		this.korisnickoImeGosta = korisnickoImeGosta;
	}

	public int getApartman() {
		return apartman;
	}

	public void setApartman(int apartman) {
		this.apartman = apartman;
	}

	public String getTekst() {
		return tekst;
	}

	public void setTekst(String tekst) {
		this.tekst = tekst;
	}

	public int getOcena() {
		return ocena;
	}

	public void setOcena(int ocena) {
		this.ocena = ocena;
	}

	@Override
	public String toString() {
		return this.id + ";" + this.korisnickoImeGosta + ";" + this.apartman + ";" + this.tekst + ";" + this.ocena;
	}
}
