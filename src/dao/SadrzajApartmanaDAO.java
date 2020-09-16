package dao;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import beans.Apartman;
import beans.SadrzajApartmana;


public class SadrzajApartmanaDAO {
	private HashMap<Integer, SadrzajApartmana> sadrzaji;
	private int idSadrzaji;
	
	public SadrzajApartmanaDAO(String putanja) {
		sadrzaji = new HashMap<Integer, SadrzajApartmana>();
		
		ucitajSadrzajApartmana(putanja);
		ucitajSadrzajApartmaniBrojac();
		
		
	}

	
	public HashMap<Integer, SadrzajApartmana> mapaSadrzaja()	{
		return sadrzaji;
	}
	
	
	public ArrayList<SadrzajApartmana> sviSadrzaji()	{
		ArrayList<SadrzajApartmana> retVal = new ArrayList<SadrzajApartmana>();
		
		for (SadrzajApartmana sadrzaj : sadrzaji.values())	{
			retVal.add(sadrzaj);
		}
		
		return retVal;
	}
	
	public ArrayList<SadrzajApartmana> dobaviSadrzaje(int[] idSadrzaja)	{
		ArrayList<SadrzajApartmana> retVal = new ArrayList<SadrzajApartmana>();
		
		for (int id : idSadrzaja)	{
			if (sadrzaji.containsKey(id))	{
				retVal.add(sadrzaji.get(id));
			}
		}
		
		return retVal;
	}
	
	public void dodajNoviSadrzaj(SadrzajApartmana noviSadrzajApartman) {
		noviSadrzajApartman.setId(idSadrzaji);
		sadrzaji.put(idSadrzaji, noviSadrzajApartman);
		inkrementirajSadrzajApartmaniBrojac();
		upisiNoviSadrzaj(noviSadrzajApartman);
	}
	
	
	public boolean obrisiSadrzajApartmana(int id)	{
		SadrzajApartmana obrisaniSadrzajApartman =  sadrzaji.remove(id); 
		if (obrisaniSadrzajApartman != null) {
			System.out.println("ApartmaniDAO: Sadrzaj apartmana " + obrisaniSadrzajApartman.getId() + " uspesno obrisan iz base.\r\n");
			azurirajBazuS();
			return true;
		} else	{
			System.out.println("ApartmaniDAO: sadrzaj apartmana " + id + " nije pronadjen u bazi.\r\n");
			return false;
		}
	}
	
	
	private void upisiNoviSadrzaj(SadrzajApartmana novSadrzaj) {
		String putanja = "./static/baza/sadrzajApartmana.txt";
		try {
			FileWriter writer = new FileWriter(putanja, true);
			writer.append(novSadrzaj.toString());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Fajl " + putanja + " nije pronadjen!\r\n");
		}
	}
	
	public boolean izmeniSadrzajApartman(ArrayList<SadrzajApartmana> izmenjenSadrzajApartmana)	{
		for(SadrzajApartmana sa : izmenjenSadrzajApartmana) {	
			if (sadrzaji.containsKey(sa.getId())) {
				sadrzaji.get(sa.getId()).setNaziv(sa.getNaziv());
			}
		}
		azurirajBazuS();
		return true;
		
		
		
	}
	
	
	
	private void azurirajBazuS()	{
		String putanja = "./static/baza/sadrzajApartmana.txt";
		try {
			FileWriter writer = new FileWriter(putanja, false);
			for (SadrzajApartmana sadrzaj : sadrzaji.values()) {
				writer.write(sadrzaj.toString());
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Fajl " + putanja + " nije pronadjen!\r\n");
		}
	}

	private void ucitajSadrzajApartmana(String putanja) {
		BufferedReader bafer;
		try	{
			bafer = new BufferedReader(new FileReader(putanja));
			String red;
			while ((red = bafer.readLine()) != null)	{
				String[] tokeni = red.split(";");
				int id = Integer.parseInt(tokeni[0]);
				
				sadrzaji.put(id, new SadrzajApartmana(id, tokeni[1]));
				System.out.println("ApartmaniDAO: " + sadrzaji.get(id).toString() + "\r\n");
			}
			
			bafer.close();
		} catch (Exception e)	{
			e.printStackTrace();
			System.out.println("Fajl " + putanja + " nije pronadjen.\r\n");
		}
	}
	
	private void ucitajSadrzajApartmaniBrojac() {
		String putanja = "./static/baza/idSadrzajApartmana.txt";
		BufferedReader bafer;
		try	{
			bafer = new BufferedReader(new FileReader(putanja));
			String red;
			if ((red = bafer.readLine()) != null)	{
				idSadrzaji = Integer.parseInt(red);
			}
			bafer.close();
		} catch (Exception e)	{
			e.printStackTrace();
			System.out.println("Fajl " + putanja + " nije pronadjen.\r\n");
		}
	}
	
	private void inkrementirajSadrzajApartmaniBrojac() {
		idSadrzaji++;
		String putanja = "./static/baza/idSadrzajApartmana.txt";
		try {
			FileWriter writer = new FileWriter(putanja, false);
			writer.write(idSadrzaji + "\r\n");
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Fajl " + putanja + " nije pronadjen.\r\n");
		}
	}
}
