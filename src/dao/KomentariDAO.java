package dao;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import beans.Komentar;


public class KomentariDAO {

	private HashMap<Integer, Komentar> komentari;
	private int idBrojac;
	
	public KomentariDAO(String putanja) {
		komentari = new HashMap<Integer, Komentar>();
		
		ucitajKomentare(putanja);
		ucitajIdBrojacKomentara();
	}

	public HashMap<Integer, Komentar> mapaKomentara()	{
		return komentari;
	}
	
	public ArrayList<Komentar> sviKomentari()	{
		ArrayList<Komentar> retVal = new ArrayList<Komentar>();
		
		for (Komentar komentara : komentari.values())	{
			retVal.add(komentara);
		}
		
		return retVal;
	}
	
	public ArrayList<Komentar> sviKomentariApartmana(int id)	{
		ArrayList<Komentar> retVal = new ArrayList<Komentar>();
		
		for (Komentar komentara : komentari.values())	{
			if(komentara.getApartman() == id) {
				retVal.add(komentara);
			}
		}
		
		return retVal;
	}
	
	public ArrayList<Komentar> sviOdobreniKomentariApartmana(int id)	{
		ArrayList<Komentar> retVal = new ArrayList<Komentar>();
		
		for (Komentar komentara : komentari.values())	{
			if(komentara.getApartman() == id && komentara.isOdobren()) {
				retVal.add(komentara);
			}
		}
		
		return retVal;
	}
	
	public void dodajNoviKomentar(Komentar noviKomentar) {
		
			noviKomentar.setId(idBrojac);
			komentari.put(idBrojac, noviKomentar);
			System.out.println("KomentariDAO: Novi komentar id:" + noviKomentar.getId() + " kreirana.\r\n");
			inkrementirajIdBrojacKomentara();
			upisiNoviKomentar(noviKomentar);
		
		
		
	}
	
	public boolean promeniStatusKomentara(int idKomentara, boolean novaVrednost)	{
		if (komentari.containsKey(idKomentara))	{
			komentari.get(idKomentara).setOdobren(novaVrednost);
			azurirajBazu();
			return true;
		} else	{
			System.out.println("KomentariDAO: Ne postoji komentar id:" + idKomentara + "\r\n");
			return false;
		}
	}

	private void upisiNoviKomentar(Komentar noviKomentar) {
		String putanja = "./static/baza/komentari.txt";
		try {
			FileWriter writer = new FileWriter(putanja, true);
			writer.append(noviKomentar.toString());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Fajl " + putanja + " nije pronadjen!\r\n");
		}
	}
	
	private void azurirajBazu()	{
		String putanja = "./static/baza/komentari.txt";
		try {
			FileWriter writer = new FileWriter(putanja, false);
			for (Komentar komentari : komentari.values()) {
				writer.write(komentari.toString());
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Fajl " + putanja + " nije pronadjen!\r\n");
		}
	}

	private void ucitajKomentare(String putanja) {
		BufferedReader bafer;
		try	{
			bafer = new BufferedReader(new FileReader(putanja));
			String red;
			while ((red = bafer.readLine()) != null)	{
				String[] tokeni = red.split(";");
				int id = Integer.parseInt(tokeni[0]);
				komentari.put(id, new Komentar(id, tokeni[1],Integer.parseInt(tokeni[2]), tokeni[3], Integer.parseInt(tokeni[4]), Boolean.parseBoolean(tokeni[5])));
				
				System.out.println("KomentariDAO: " + komentari.get(id).toString() + "\r\n");
			}
			
			bafer.close();
		} catch (Exception e)	{
			e.printStackTrace();
			System.out.println("Fajl " + putanja + " nije pronadjen.\r\n");
		}
	}
	
	private void ucitajIdBrojacKomentara() {
		String putanja = "./static/baza/idKomentara.txt";
		BufferedReader bafer;
		try	{
			bafer = new BufferedReader(new FileReader(putanja));
			String red;
			if ((red = bafer.readLine()) != null)	{
				idBrojac = Integer.parseInt(red);
			}
			bafer.close();
		} catch (Exception e)	{
			e.printStackTrace();
			System.out.println("Fajl " + putanja + " nije pronadjen.\r\n");
		}
	}
	
	private void inkrementirajIdBrojacKomentara() {
		idBrojac++;
		String putanja = "./static/baza/idKomentara.txt";
		try {
			FileWriter writer = new FileWriter(putanja, false);
			writer.write(idBrojac + "\r\n");
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Fajl " + putanja + " nije pronadjen.\r\n");
		}
	}
}
