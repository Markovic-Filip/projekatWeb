package beans;

public class Komentar {

	protected int id;
	protected String korisnickoImeGosta;
	protected int apartman;
	protected String tekst;
	protected int ocena;
	protected boolean odobren;
	
	public Komentar() {
		
		
	}
	
	public Komentar(int id, String korisnickoImeGosta, int apartman, String tekst, int ocena, boolean odobren) {
		this.id = id;
		this.korisnickoImeGosta = korisnickoImeGosta;
		this.apartman = apartman;
		this.tekst = tekst;
		this.ocena = ocena;
		this.odobren = odobren;
		
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
	
	public boolean isOdobren()	{
		return this.odobren;
	}
	
	public void setOdobren(boolean odobren)	{
		this.odobren = odobren;
	}

	@Override
	public String toString() {
		return this.id + ";" + this.korisnickoImeGosta + ";" + this.apartman + ";" + this.tekst + ";" + this.ocena + ";" + this.odobren + "\n";
	}
}
